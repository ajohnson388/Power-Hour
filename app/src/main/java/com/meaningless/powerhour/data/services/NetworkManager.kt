package com.meaningless.powerhour.data.services

import com.meaningless.powerhour.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkManager {

    val spotifyAccountAPI: SpotifyAccountAPI = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BuildConfig.SPOTIFY_ACCOUNTS_URL)
        .build()
        .create(SpotifyAccountAPI::class.java)
}