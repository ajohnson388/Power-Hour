package com.meaningless.powerhour.ui.game

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.meaningless.powerhour.R
import com.meaningless.powerhour.data.database.DataManager
import com.meaningless.powerhour.data.models.GameConfig
import com.meaningless.powerhour.data.music.common.models.Intervals
import com.meaningless.powerhour.utils.DataUtils
import com.meaningless.powerhour.data.music.common.models.Playlist
import com.meaningless.powerhour.data.music.common.models.TrackMetaData
import com.meaningless.powerhour.data.music.common.interfaces.MusicAPI
import com.meaningless.powerhour.data.services.GameService
import com.meaningless.powerhour.data.music.spotify.api.SpotifyAPI
import com.meaningless.powerhour.ui.searchplaylists.SearchActivity

/**
 * Bridges the logic of the power hour game service to the UI.
 * */
class GameActivityViewModel(application: Application) : AndroidViewModel(application),
    MusicAPI.Listener {

    // region Properties
    private val _trackLiveData = MutableLiveData<TrackMetaData>()
    private val _playlistLiveData = MutableLiveData<Playlist>()
    private val _roundLiveData = MutableLiveData<Int>()
    private val _errorLiveData = MutableLiveData<String>()

    val trackLiveData: LiveData<TrackMetaData> get() = _trackLiveData
    val playlistLiveData: LiveData<Playlist> get() = _playlistLiveData
    val roundLiveData: LiveData<Int> get() = _roundLiveData
    val errorLiveData: LiveData<String> get() = _errorLiveData
    val isAuthenticated: Boolean get() = DataManager.isSpotifyAuthenticated(getApplication())

    var musicApi: MusicAPI? = SpotifyAPI(application)

    var config = GameConfig(60, Intervals.ONE_MINUTE)
    // endregion

    init {
        _playlistLiveData.value = DataManager.getSelectedPlaylist(getApplication())
        musicApi?.setListener(this)
    }

    // region Observers
    private val onRoundChanged = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent == null) return
            _roundLiveData.value = intent.getIntExtra(GameService.MessageKey.CURRENT_ROUND, 0)
            Log.d(TAG, "Round changed to ${_roundLiveData.value}")
        }
    }

    private val onMusicHandlerError = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent == null) return
            _errorLiveData.value = intent.getStringExtra(GameService.MessageKey.ERROR)
            Log.d(TAG, "Music error occured: ${_errorLiveData.value}")
        }
    }

    private val onTrackChanged = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            _trackLiveData.value = intent?.getStringExtra(GameService.MessageKey.TRACK)?.let {
                DataUtils.makeModel(it, TrackMetaData::class.java)
            }
        }
    }
    // endregion

    // region Control Functions
    fun handleIntent(intent: Intent?) {
        intent?.getStringExtra(SearchActivity.IntentKey.PLAYLIST)?.let {
            val playlist = DataUtils.makeModel(it, Playlist::class.java)
            pickPlaylist(playlist)
        } ?: intent?.data?.let {
            handleAuthorizationResponse(it)
        }
    }

    fun authorize() {
        musicApi?.authorize(getApplication())
    }

    private fun handleAuthorizationResponse(uri: Uri) {
        musicApi?.handleAuthorizationResponse(getApplication(), uri)
    }

    fun getPlaylists(): List<Playlist> {
        val playlists = mutableListOf(
            Playlist(
                getString(R.string.playlist_90),
                "spotify:user:spotify:playlist:37i9dQZF1DXbTxeAdrVG2l"
            ),
            Playlist(
                getString(R.string.playlist_80),
                "spotify:user:spotify:playlist:37i9dQZF1DX4UtSsGT1Sbe"
            ),
            Playlist(
                getString(R.string.playlist_70),
                "spotify:user:spotify:playlist:37i9dQZF1DWTJ7xPn4vNaz"
            )
        )
        val recents = DataManager.getRecentPlaylists(getApplication())
        playlists.addAll(recents.getPlaylists())
        return playlists
    }

    fun pickPlaylist(playlist: Playlist) {
        DataManager.setSelectedPlaylist(getApplication(), playlist)
        _playlistLiveData.value = playlist
    }

    fun toggleGameStart(): Boolean =
        if (_roundLiveData.value ?: 0 == 0) {
            startGame()
            true
        } else {
            endGame()
            false
        }
    // endregion

    // region Helper Functions
    private fun startGame() {
        Log.d(TAG, "Game starting")
        registerReceivers()
        Intent(getApplication(), GameService::class.java).also {
            it.putExtra(GameService.IntentKey.NUMBER_OF_ROUNDS, config.numberOfRounds)
            it.putExtra(GameService.IntentKey.INTERVAL_DURATION, config.roundDuration)
            getApplication<Application>().startService(it)
        }
    }

    private fun endGame() {
        Log.d(TAG, "Game ending")
        unregisterReceivers()
        Intent(getApplication(), GameService::class.java).also {
            getApplication<Application>().stopService(it)
        }
    }

    private fun getString(@StringRes stringRes: Int) =
        getApplication<Application>().getString(stringRes)

    private fun registerReceivers() {
        LocalBroadcastManager.getInstance(getApplication())
            .registerReceiver(
                onMusicHandlerError,
                IntentFilter(GameService.IntentKey.ERROR_NOTIFICATION)
            )
        LocalBroadcastManager.getInstance(getApplication())
            .registerReceiver(
                onRoundChanged,
                IntentFilter(GameService.IntentKey.ROUND_CHANGED_NOTIFICATION)
            )
        LocalBroadcastManager.getInstance(getApplication())
            .registerReceiver(
                onTrackChanged,
                IntentFilter(GameService.IntentKey.TRACK_CHANGED_NOTIFICATION)
            )
        Log.d(TAG, "Receivers registered")
    }

    private fun unregisterReceivers() {
        LocalBroadcastManager.getInstance(getApplication()).unregisterReceiver(onMusicHandlerError)
        LocalBroadcastManager.getInstance(getApplication()).unregisterReceiver(onMusicHandlerError)
    }
    // endregion

    // region Music API Listener
    override fun onError(error: String) {
        _errorLiveData.value = error
    }

    override fun onAuthorizationComplete() {
        DataManager.setIsSpotifyAuthorized(getApplication(), true)
    }

    override fun onCurrentTrackReceived(track: TrackMetaData) {
        _trackLiveData.value = track
    }

    override fun onPlaylistsRecevied(playlists: List<Playlist>) {

    }
    // endregion

    // region Associated Types
    companion object {
        private val TAG = GameActivityViewModel::class.java.simpleName
    }
    // endregion
}