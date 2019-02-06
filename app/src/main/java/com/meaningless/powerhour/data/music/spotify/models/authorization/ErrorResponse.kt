package com.meaningless.powerhour.data.music.spotify.models.authorization

import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    val error: String?,
    @SerializedName("error_description") val errorDescription: String?
)