package com.meaningless.powerhour.data.music.common.interfaces

import android.content.Context

interface MusicHandler {

    // region Methods
    fun connect(context: Context)
    fun start(context: Context)
    fun skip(context: Context)
    fun stop()
    fun disconnect()
    // endregion

    // region Associated Types
    enum class Type(val rawValue: String) {
        SPOTIFY("spotify");

        companion object {
            fun from(rawValue: String) = values().firstOrNull { it.rawValue == rawValue }
        }
    }
    // endregion
}