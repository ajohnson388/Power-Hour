package com.meaningless.powerhour.data.music

interface MusicHandler {

    // region Methods
    fun start()
    fun skip()
    fun stop()
    fun connect()
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