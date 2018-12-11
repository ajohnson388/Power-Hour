package com.meaningless.powerhour.data.music

import android.content.Context
import android.util.Log
import com.meaningless.powerhour.data.database.DataManager
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector

class SpotifyHandler(private val context: Context) : MusicHandler, Connector.ConnectionListener {

    // region Fields
    var listener: Listener? = null
    private var appRemote: SpotifyAppRemote? = null
    private val isConnected: Boolean get() = appRemote?.isConnected ?: false
    // endregion

    // region MusicHandler
    override fun connect() {
        if (isConnected) return
        val connectionParams = ConnectionParams.Builder(CLIENT_ID)
            .setRedirectUri(REDIRECT_URI)
            .showAuthView(true)
            .build()
        SpotifyAppRemote.connect(context, connectionParams, this)
    }

    override fun start() {
        val startUri = DataManager.getPlayListUri(context) ?: DEFAULT_PLAY_LIST_URI
        appRemote?.playerApi?.play(startUri)
    }

    override fun skip() {
        appRemote?.playerApi?.skipNext()
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
        listener?.onConnected()
    }

    override fun onFailure(error: Throwable) {
        Log.e(TAG, error.message, error)
    }
    // endregion

    // region Associated Types
    interface Listener {
        fun onConnected()
    }

    companion object {
        private val TAG = SpotifyHandler::class.java.simpleName
        private const val CLIENT_ID = "d98ba0369f7843e285bc90f14e4132be"
        private const val REDIRECT_URI = "com.meaningless.powerhour://callback"
        private const val DEFAULT_PLAY_LIST_URI =
            "spotify:user:spotify:playlist:37i9dQZF1DWTJ7xPn4vNaz"
    }
    // endregion
}