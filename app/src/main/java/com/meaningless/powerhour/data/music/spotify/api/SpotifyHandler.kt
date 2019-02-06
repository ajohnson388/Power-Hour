package com.meaningless.powerhour.data.music.spotify.api

import android.content.Context
import android.util.Log
import com.meaningless.powerhour.BuildConfig
import com.meaningless.powerhour.data.database.DataManager
import com.meaningless.powerhour.utils.DataUtils
import com.meaningless.powerhour.data.music.common.models.TrackMetaData
import com.meaningless.powerhour.data.music.common.interfaces.MusicHandler
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import java.lang.ref.WeakReference

/**
 * An implementation of the MusicHandler for the Spotify SDK.
 * */
class SpotifyHandler : MusicHandler, Connector.ConnectionListener {

    // region Fields
    private var listener: WeakReference<Listener>? = null
    private var appRemote: SpotifyAppRemote? = null
    private val isConnected: Boolean get() = appRemote?.isConnected ?: false
    private var lastTrackUri: String? = null
    private var lastPlaylistUri: String? = null
    // endregion

    fun setListener(listener: Listener) {
        this.listener = WeakReference(listener)
    }

    override fun connect(context: Context) {
        if (isConnected) return
        val connectionParams = ConnectionParams.Builder(BuildConfig.SPOTIFY_CLIENT_ID)
            .setRedirectUri(BuildConfig.SPOTIFY_REDIRECT_URI)
            .showAuthView(true)
            .build()
        SpotifyAppRemote.connect(context, connectionParams, this)
    }

    override fun start(context: Context) {
        lastPlaylistUri = DataManager.getSelectedPlaylist(context)?.uri ?:
                BuildConfig.SPOTIFY_DEFAULT_PLAYLIST
        appRemote?.playerApi?.play(lastPlaylistUri)
        appRemote?.playerApi?.seekTo(20 * 1000)
    }

    override fun skip(context: Context) {
        val playlistUri = DataManager.getSelectedPlaylist(context)?.uri
        if (playlistUri != lastPlaylistUri) {
            lastPlaylistUri = playlistUri
            appRemote?.playerApi?.play(playlistUri)

        } else {
            appRemote?.playerApi?.skipNext()
        }
        appRemote?.playerApi?.seekTo(20 * 1000)
    }

    override fun stop() {
        appRemote?.playerApi?.pause()
    }

    override fun disconnect() {
        SpotifyAppRemote.disconnect(appRemote ?: return)
    }
    // endregion

    // region Spotify Connection Listener
    override fun onConnected(appRemote: SpotifyAppRemote) {
        this.appRemote = appRemote
        appRemote.playerApi?.subscribeToPlayerState()?.setEventCallback { playerState ->
            val currentTrack = playerState?.track
            val currentTrackUri = currentTrack?.uri ?: return@setEventCallback
            if (currentTrackUri == lastTrackUri) return@setEventCallback
            lastTrackUri = currentTrackUri
            appRemote.imagesApi?.getImage(currentTrack.imageUri)?.setResultCallback {
                val metaData = TrackMetaData(
                    currentTrack.name,
                    currentTrack.artist.name,
                    currentTrack.album?.name,
                    if (it == null) null else DataUtils.makeString(it)
                )
                listener?.get()?.onTrackChanged(metaData)
            }
        }
        listener?.get()?.onConnected()
    }

    override fun onFailure(error: Throwable) {
        Log.e(TAG, error.message, error)
    }
    // endregion

    // region Associated Types
    interface Listener {
        fun onConnected()
        fun onTrackChanged(track: TrackMetaData)
        fun onError(error: String)
    }

    companion object {
        private val TAG = SpotifyHandler::class.java.simpleName
    }
    // endregion
}