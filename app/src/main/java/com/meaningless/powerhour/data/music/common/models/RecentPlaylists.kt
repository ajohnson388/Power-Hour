package com.meaningless.powerhour.data.music.common.models

import android.content.Context
import com.meaningless.powerhour.data.database.DataManager
import com.meaningless.powerhour.data.music.common.models.Playlist

data class RecentPlaylists(private val playlists: MutableList<Playlist> = mutableListOf()) {

    fun add(playlist: Playlist) {
        val maxRecents = 2
        playlist.type = Playlist.Type.HISTORY
        while (playlists.size >= maxRecents) {
            playlists.removeAt(playlists.size - 1)
        }
        playlists.add(0, playlist)
    }

    fun getPlaylists(): List<Playlist> = playlists

    fun save(context: Context) {
        DataManager.setRecentPlaylists(context, this)
    }
}