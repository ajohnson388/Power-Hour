package com.meaningless.powerhour.data.music.common.interfaces

import android.content.Context
import android.net.Uri
import com.meaningless.powerhour.data.music.common.models.Playlist
import com.meaningless.powerhour.data.music.common.models.TrackMetaData

interface MusicAPI {
    fun authorize(context: Context)
    fun handleAuthorizationResponse(context: Context, uri: Uri)
    fun getCurrentTrack()
    fun getPlaylists(searchTerm: String): Cancellable
    fun setListener(listener: Listener)

    interface Listener {
        fun onAuthorizationComplete()
        fun onCurrentTrackReceived(track: TrackMetaData)
        fun onPlaylistsRecevied(playlists: List<Playlist>)
        fun onError(error: String)
    }
}