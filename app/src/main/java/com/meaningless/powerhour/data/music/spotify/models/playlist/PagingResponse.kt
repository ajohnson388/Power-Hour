package com.meaningless.powerhour.data.music.spotify.models.playlist

data class PagingResponse(
    val href: String,
    val items: List<Item>,
    val limit: Int,
    val next: String?,
    val offset: Int,
    val previous: String?,
    val total: Int
)