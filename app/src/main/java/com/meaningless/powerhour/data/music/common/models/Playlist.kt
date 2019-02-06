package com.meaningless.powerhour.data.music.common.models

data class Playlist(
    val name: String? = null,
    var uri: String? = null,
    var type: Type = Type.INCLUDED
) {
    enum class Type {
        INCLUDED, HISTORY, NONE
    }
}