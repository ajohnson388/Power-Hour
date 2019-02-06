package com.meaningless.powerhour.data.music.spotify.api

import com.meaningless.powerhour.BuildConfig
import com.meaningless.powerhour.data.music.spotify.models.authorization.TokenResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface SpotifyAccountAPI {

    @GET(
        "authorize?client_id=${BuildConfig.SPOTIFY_CLIENT_ID}&response_type=code&redirect_uri=${BuildConfig.SPOTIFY_REDIRECT_URI}&scope=${BuildConfig.SPOTIFY_SCOPES}"
    )
    fun requestAuthorization(): Call<ResponseBody>

    @FormUrlEncoded
    @POST("api/token")
    fun requestAccessToken(
        @Field("code") code: String,
        @Field("client_id") clientId: String = BuildConfig.SPOTIFY_CLIENT_ID,
        @Field("client_secret") clientSecret: String = BuildConfig.SPOTIFY_AUTH_TOKEN,
        @Field("grant_type") grantType: String = "authorization_code",
        @Field("redirect_uri") redirectUri: String = BuildConfig.SPOTIFY_REDIRECT_URI
    ): Call<TokenResponse>

    @FormUrlEncoded
    @POST("api/token")
    fun requestRefreshToken(
        @Field("refresh_token") refreshToken: String,
        @Field("grant_type") grantType: String = "refresh_token",
        @Field("client_id") clientId: String = BuildConfig.SPOTIFY_CLIENT_ID,
        @Field("client_secret") clientSecret: String = BuildConfig.SPOTIFY_AUTH_TOKEN
    ): Call<TokenResponse>
}