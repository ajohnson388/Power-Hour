package com.meaningless.powerhour.data.music.spotify.models.authorization

import com.google.gson.annotations.SerializedName

data class TokenResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String,
    val scope: String,
    @SerializedName("expires_in") val expiresIn: Long,
    @SerializedName("refresh_token") val refreshToken: String
)