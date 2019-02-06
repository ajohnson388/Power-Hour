package com.meaningless.powerhour.data.music.spotify.models.authorization

data class AuthorizationResponse(
    val code: String?,
    val state: String?,
    val error: String?
)