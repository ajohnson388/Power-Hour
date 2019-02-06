package com.meaningless.powerhour.data.music.spotify.api

import com.meaningless.powerhour.data.music.spotify.models.playlist.PagingResponse
import com.meaningless.powerhour.data.music.spotify.models.playlist.PlaylistsResponse
import com.meaningless.powerhour.data.music.spotify.models.track.Track
import retrofit2.http.GET
import retrofit2.Call
import retrofit2.http.Path
import retrofit2.http.Query

interface SpotifyUserAPI {

    @GET("users/{user_id}/playlists")
    fun getPlaylists(
        @Path("user_id") userId: String,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): Call<PagingResponse>

    @GET("search?type=playlist")
    fun searchPlaylists(
        @Query("q") searchTerm: String,
        @Query("type") type: String = "playlist",
        @Query("offset") offset: Int = 0
    ): Call<PlaylistsResponse>

    @GET("me/player/currently-playing")
    fun getCurrentTrack(): Call<Track>

    @GET("me/player/seek")
    fun seekTrack(@Query("position_ms") positionMs: Int = 20 * 1000)
}