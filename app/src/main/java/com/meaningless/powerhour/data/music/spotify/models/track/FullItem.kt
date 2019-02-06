package com.meaningless.powerhour.data.music.spotify.models.track

import com.google.gson.annotations.SerializedName
import com.meaningless.powerhour.data.music.spotify.models.playlist.ExternalUrls

data class FullItem(
    val album: Album,
    val artists: List<Artist>,
    @SerializedName("available_markets") val availableMarkets: List<String>,
    @SerializedName("discNumber") val discNumber: Int,
    @SerializedName("durationMs") val durationMs: Int,
    val explicit: Boolean,
    @SerializedName("externalIds") val externalIds: ExternalIds,
    @SerializedName("external_urls") val externalUrls: ExternalUrls,
    val href: String,
    val id: String,
    val name: String,
    val popularity: Int,
    @SerializedName("preview_url") val previewUrl: String?,
    @SerializedName("track_number") val trackNumber: Int,
    val type: String,
    val uri: String
)