package com.meaningless.powerhour.data.music.spotify.models.track

import com.google.gson.annotations.SerializedName

data class Track(
    val context: Context?,
    @SerializedName("currently_playing_type") val currentlyPlayingType: String,
    @SerializedName("is_playing") val isPlaying: Boolean,
    val item: FullItem?,
    @SerializedName("progress_ms") val progressMs: Int?,
    val timestamp: Long
)