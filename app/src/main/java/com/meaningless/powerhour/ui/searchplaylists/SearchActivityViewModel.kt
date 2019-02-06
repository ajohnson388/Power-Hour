package com.meaningless.powerhour.ui.searchplaylists

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.meaningless.powerhour.data.music.common.interfaces.Cancellable
import com.meaningless.powerhour.data.music.common.models.Playlist
import com.meaningless.powerhour.data.music.common.models.TrackMetaData
import com.meaningless.powerhour.data.music.common.interfaces.MusicAPI
import com.meaningless.powerhour.data.music.spotify.api.SpotifyAPI

class SearchActivityViewModel(application: Application) : AndroidViewModel(application), MusicAPI.Listener {

    private val _searchResultsLiveData = MutableLiveData<List<Playlist>>()
    private val _errorLiveData = MutableLiveData<String>()

    private var musicApi: MusicAPI? = null
    private var currentRequest: Cancellable? = null

    val searchResultsLiveData: LiveData<List<Playlist>> get() = _searchResultsLiveData
    val errorLiveData: LiveData<String> get() = _errorLiveData

    init {
        val spotifyApi =
            SpotifyAPI(getApplication())
        musicApi = spotifyApi
        musicApi?.setListener(this)
    }

    fun search(query: String) {
        currentRequest?.cancel()
        currentRequest = musicApi?.getPlaylists(query)
    }

    override fun onAuthorizationComplete() {}

    override fun onCurrentTrackReceived(track: TrackMetaData) {}

    override fun onPlaylistsRecevied(playlists: List<Playlist>) {
        _searchResultsLiveData.value = playlists
    }

    override fun onError(error: String) {
        _errorLiveData.value = error
    }

    companion object {
        private val TAG = SearchActivityViewModel::class.java.simpleName
    }
}