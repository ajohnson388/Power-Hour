package com.meaningless.powerhour.data.music.spotify.models.track

import com.google.gson.annotations.SerializedName
import com.meaningless.powerhour.data.music.spotify.models.playlist.ExternalUrls

data class Context(
    @SerializedName("external_urls") val externalUrls: ExternalUrls?,
    val href: String?,
    val type: String,
    val uri: String
)