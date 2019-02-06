package com.meaningless.powerhour.data.music.spotify.models.track

import com.google.gson.annotations.SerializedName
import com.meaningless.powerhour.data.music.spotify.models.playlist.ExternalUrls

data class Artist(
    @SerializedName("external_urls") val externalUrls: ExternalUrls,
    val href: String,
    val id: String,
    val name: String,
    val type: String,
    val uri: String
)