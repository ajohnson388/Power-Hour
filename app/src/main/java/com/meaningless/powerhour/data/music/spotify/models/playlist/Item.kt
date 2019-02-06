package com.meaningless.powerhour.data.music.spotify.models.playlist

import com.google.gson.annotations.SerializedName
import com.meaningless.powerhour.data.music.spotify.models.track.Image

data class Item(
    val collaborative: Boolean,
    @SerializedName("external_urls") val externalUrls: ExternalUrls,
    val href: String,
    val id: String,
    val images: List<Image>,
    val name: String,
    val owner: Owner,
    val public: Boolean?,
    @SerializedName("snapshot_id") val snapshotId: String,
    val tracks: Tracks,
    val type: String,
    val uri: String
)