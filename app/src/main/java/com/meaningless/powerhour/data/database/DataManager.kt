package com.meaningless.powerhour.data.database

import android.content.Context
import com.meaningless.powerhour.data.models.GameConfig
import com.meaningless.powerhour.data.music.common.models.Playlist
import com.meaningless.powerhour.data.music.common.models.RecentPlaylists
import com.meaningless.powerhour.utils.DataUtils
import com.meaningless.powerhour.data.music.spotify.models.authorization.TokenResponse

object DataManager {

    // region Access Methods
    fun getSelectedPlaylist(context: Context): Playlist? {
        val jsonString = context.getSharedPreferences(Key.POWER_HOUR_PREFS, Context.MODE_PRIVATE)
            .getString(Key.SELECTED_PLAYLIST, null) ?: return null
        return DataUtils.makeModel(jsonString, Playlist::class.java)
    }

    fun isSpotifyAuthorized(context: Context): Boolean =
        context.getSharedPreferences(Key.POWER_HOUR_PREFS, Context.MODE_PRIVATE)
            .getBoolean(Key.IS_SPOTIFY_AUTHORIZED, false)

    fun isSpotifyAuthenticated(context: Context): Boolean =
        getMusicAPIAuthorizationData(context)?.refreshToken != null

    fun getMusicAPIAuthorizationData(context: Context): TokenResponse? {
        val jsonString = context.getSharedPreferences(Key.POWER_HOUR_PREFS, Context.MODE_PRIVATE)
            .getString(Key.AUTHORIZATION_DATA, null) ?: return null
        val token = DataUtils.makeModel(jsonString, TokenResponse::class.java)
        return token
    }

    fun getGameConfig(context: Context): GameConfig? {
        val jsonString = context.getSharedPreferences(Key.POWER_HOUR_PREFS, Context.MODE_PRIVATE)
            .getString(Key.GAME_CONFIG, null) ?: return null
        return DataUtils.makeModel(jsonString, GameConfig::class.java)
    }

    fun getRecentPlaylists(context: Context): RecentPlaylists {
        val jsonString = context.getSharedPreferences(Key.POWER_HOUR_PREFS, Context.MODE_PRIVATE)
            .getString(Key.RECENT_PLAYLISTS, null) ?: return RecentPlaylists()
        return DataUtils.makeModel(jsonString, RecentPlaylists::class.java)
    }
    // endregion

    // region Write Methods
    fun setSelectedPlaylist(context: Context, playlist: Playlist?) =
        setData(context, playlist, Key.SELECTED_PLAYLIST)

    fun setIsSpotifyAuthorized(context: Context, isAuthorized: Boolean) =
        setBoolean(context, isAuthorized, Key.IS_SPOTIFY_AUTHORIZED)

    fun setMusicAPIAuthorizationData(context: Context, data: TokenResponse?) =
        setData(context, data, Key.AUTHORIZATION_DATA)

    fun setGameConfig(context: Context, data: TokenResponse?) =
        setData(context, data, Key.GAME_CONFIG)

    fun setRecentPlaylists(context: Context, data: RecentPlaylists?) =
        setData(context, data, Key.RECENT_PLAYLISTS)

    private fun <T> setData(context: Context, data: T?, key: String) =
        context.getSharedPreferences(Key.POWER_HOUR_PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(
                key,
                if (data == null) null else DataUtils.makeJsonString(data)
            )
            .apply()

    private fun setBoolean(context: Context, value: Boolean, key: String) =
        context.getSharedPreferences(Key.POWER_HOUR_PREFS, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(key, value)
            .apply()
    // endregion

    // region Associated Types
    private object Key {
        const val POWER_HOUR_PREFS = "powerHourPrefs"
        const val SELECTED_PLAYLIST = "selectedPlaylist"
        const val IS_SPOTIFY_AUTHORIZED = "isSpotifyAuthorized"
        const val AUTHORIZATION_DATA = "authorizationData"
        const val RECENT_PLAYLISTS = "recentPlaylists"
        const val GAME_CONFIG = "gameConfig"
    }
    // endregion
}