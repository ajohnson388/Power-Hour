package com.meaningless.powerhour.data.services

import com.meaningless.powerhour.BuildConfig
import com.meaningless.powerhour.data.models.SpotifyLoginResponse
import retrofit2.Call
import retrofit2.http.*

interface SpotifyAccountAPI {

    @GET(
        """
        authorize
        ?client_id=${BuildConfig.SPOTIFY_CLIENT_ID}
        &response_type=code
        &redirect_uri=${BuildConfig.SPOTIFY_REDIRECT_URI}
        &scope=${BuildConfig.SPOTIFY_SCOPES}
        """
    )
    fun requestAuthorization(): Call<SpotifyLoginResponse>

    @Headers("Authorization: Basic ${BuildConfig.SPOTIFY_AUTH_TOKEN}")
    @POST(
        """
        api/token
        ?grant_type=authorization_code
        &redirect_uri=${BuildConfig.SPOTIFY_REDIRECT_URI}
        """
    )
    fun requestTokens(@Query("code") code: String): Call<SpotifyLoginResponse>
}