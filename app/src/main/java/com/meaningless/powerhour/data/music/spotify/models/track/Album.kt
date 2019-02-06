package com.meaningless.powerhour.data.music.spotify.models.track

import com.google.gson.annotations.SerializedName
import com.meaningless.powerhour.data.music.spotify.models.playlist.ExternalUrls

data class Album(
    @SerializedName("album_type") val albumType: String,
    @SerializedName("external_urls") val externalUrls: ExternalUrls,
    val href: String,
    val id: String,
    val images: List<Image>,
    val name: String,
    val type: String,
    val uri: String
)