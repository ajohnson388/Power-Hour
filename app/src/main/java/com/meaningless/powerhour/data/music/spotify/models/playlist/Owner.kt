package com.meaningless.powerhour.data.music.spotify.models.playlist

import com.google.gson.annotations.SerializedName

data class Owner(
    @SerializedName("external_urls") val externalUrls: ExternalUrls,
    val href: String,
    val id: String,
    val type: String,
    val uri: String
)