package com.meaningless.powerhour.data.database

import android.content.Context

object DataManager {

    // region Access Methods
    fun getPlayListUri(context: Context): String? =
        context.getSharedPreferences(Key.POWER_HOUR_PREFS, Context.MODE_PRIVATE)
            .getString(Key.PLAY_LIST_URI, null)
    // endregion

    // region Write Methods
    fun setPlayListUri(context: Context, uri: String) =
        context.getSharedPreferences(Key.POWER_HOUR_PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(Key.PLAY_LIST_URI, uri)
            .apply()
    // endregion

    // region Associated Types
    object Key {
        const val POWER_HOUR_PREFS = "powerHourPrefs"
        const val PLAY_LIST_URI = "playListUri"
    }
    // endregion
}