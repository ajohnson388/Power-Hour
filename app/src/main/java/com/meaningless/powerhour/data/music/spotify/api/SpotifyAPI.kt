package com.meaningless.powerhour.data.music.spotify.api

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.browser.customtabs.CustomTabsIntent
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.meaningless.powerhour.BuildConfig
import com.meaningless.powerhour.data.database.DataManager
import com.meaningless.powerhour.data.music.common.interfaces.Cancellable
import com.meaningless.powerhour.data.music.common.models.CancellableCall
import com.meaningless.powerhour.data.music.common.models.Playlist
import com.meaningless.powerhour.utils.DataUtils
import com.meaningless.powerhour.data.music.common.models.TrackMetaData
import com.meaningless.powerhour.data.music.spotify.models.authorization.AuthorizationResponse
import com.meaningless.powerhour.data.music.spotify.models.authorization.TokenResponse
import com.meaningless.powerhour.data.music.spotify.models.playlist.PlaylistsResponse
import com.meaningless.powerhour.data.music.spotify.models.track.Track
import com.meaningless.powerhour.data.music.common.interfaces.MusicAPI
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.ref.WeakReference

class SpotifyAPI(val context: Context) : MusicAPI {

    // region Properties
    private val gson: Gson = GsonBuilder().setLenient().create()
    private val loggingInterceptor: HttpLoggingInterceptor =
        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    private val httpClient = OkHttpClient.Builder()
        .authenticator(TokenRefresher())
        .addInterceptor(loggingInterceptor)
        .addInterceptor(::interceptRequest).build()

    private var tokenStore: TokenResponse? = DataManager.getMusicAPIAuthorizationData(context)
    private var listener: WeakReference<MusicAPI.Listener>? = null

    private val accountApi: SpotifyAccountAPI = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create(gson))
        .baseUrl(BuildConfig.SPOTIFY_ACCOUNTS_URL)
        .client(httpClient)
        .build()
        .create(SpotifyAccountAPI::class.java)

    private val userApi: SpotifyUserAPI = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BuildConfig.SPOTIFY_API_URL)
        .client(httpClient)
        .build()
        .create(SpotifyUserAPI::class.java)
    // endregion

    override fun setListener(listener: MusicAPI.Listener) {
        this.listener = WeakReference(listener)
    }

    // region Music API
    override fun authorize(context: Context) {
        // If token data exists store it in memory
        // Check if token is expired
        val refreshToken = tokenStore?.refreshToken
        if (refreshToken != null) {
            // TODO: Check if token is expired
            return
        } else {
            makeAuthorizationRequest(context)
        }
    }

    override fun handleAuthorizationResponse(context: Context, uri: Uri) {
        val data = DataUtils.parseIntentData(uri)
        val spotifyResponse = DataUtils.makeModel(data, AuthorizationResponse::class.java)
        spotifyResponse.code?.let {
            val accessTokensRequest = accountApi.requestAccessToken(it)
            accessTokensRequest.enqueue(onRequestAccessTokenCompleted(context))
        } ?: spotifyResponse.error?.let {
            listener?.get()?.onError(it)
        } ?: run {
            Log.e(TAG, "Spotify authorization response is missing the 'code' parameter.")
            listener?.get()?.onError("Failed to authorize Power Hour for Spotify.")
        }
    }

    override fun getCurrentTrack() {
        val call = userApi.getCurrentTrack()
        call.enqueue(object :
            MusicCallback<Track>("Failed to fetch current track data.", listener?.get()) {
            override fun onSuccess(data: Track) {
                val metaData = TrackMetaData(
                    data.item?.album?.name,
                    data.item?.artists?.map { it.name }?.joinToString(", "),
                    data.item?.album?.name,
                    null//data.item?.album?.images?.lastOrNull()?.url
                )
                listener?.get()?.onCurrentTrackReceived(metaData)
            }
        })
    }

    override fun getPlaylists(searchTerm: String): Cancellable {
        val call = userApi.searchPlaylists(searchTerm)
        call.enqueue(object :
            MusicCallback<PlaylistsResponse>("Failed to search playlists.", listener?.get()) {
            override fun onSuccess(data: PlaylistsResponse) {
                val playlists = data.playlists.items.map {
                    Playlist(
                        it.name,
                        it.uri,
                        Playlist.Type.NONE
                    )
                }
                listener?.get()?.onPlaylistsRecevied(playlists)
            }
        })
        return CancellableCall(call)
    }
    // endregion

    // region Helper Functions
    private fun onRequestAccessTokenCompleted(context: Context): Callback<TokenResponse> =
        object : Callback<TokenResponse> {

            override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                response.body()?.let {
                    DataManager.setMusicAPIAuthorizationData(context, it)
                    listener?.get()?.onAuthorizationComplete()
                    tokenStore = it
                }
                Log.d(TAG, "Received Spotify request tokens.")
            }

            override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                Log.d(TAG, "Failed to receive Spotify request tokens: ${t.message}")
            }
        }

    private fun interceptRequest(chain: Interceptor.Chain): okhttp3.Response {
        // Check for 401 and user api
        if (chain.request().url().toString().contains(BuildConfig.SPOTIFY_API_URL)) {

            val accessToken = tokenStore?.accessToken ?: run {
                Log.e(TAG, "Access token required to make Spotify User API calls.")
                return chain.proceed(chain.request())
            }
            val newRequest = chain.request()
                .newBuilder()
                .addHeader("Authorization", "Bearer $accessToken")
                .build()
            return chain.proceed(newRequest)
        } else {
            return chain.proceed(chain.request())
        }
    }

    private fun makeAuthorizationRequest(context: Context) {
        val url = "${BuildConfig.SPOTIFY_ACCOUNTS_URL}authorize" +
                "?client_id=${BuildConfig.SPOTIFY_CLIENT_ID}" +
                "&response_type=code" +
                "&redirect_uri=${BuildConfig.SPOTIFY_REDIRECT_URI}" +
                "&scope=${BuildConfig.SPOTIFY_SCOPES}"
        CustomTabsIntent.Builder().build().launchUrl(context, Uri.parse(url))
    }

    private fun isUserAPIRequest(request: Request): Boolean =
        request.url().toString().contains(BuildConfig.SPOTIFY_API_URL)
    // endregion

    // region Inner Types
    abstract inner class MusicCallback<T>(
        private val errorMessage: String, private val listener: MusicAPI.Listener?
    ) : Callback<T> {

        private val TAG = MusicCallback::class.java.simpleName

        abstract fun onSuccess(data: T)

        final override fun onFailure(call: Call<T>, t: Throwable) {
            Log.e(TAG, t.message)
            listener?.onError(errorMessage)
        }

        override fun onResponse(call: Call<T>, response: Response<T>) {
            when (response.code()) {
                in 200..Int.MAX_VALUE -> {
                    response.body()?.let {
                        onSuccess(it)
                    } ?: kotlin.run {
                        listener?.onError(errorMessage)
                    }
                }
                else -> {
                    listener?.onError(errorMessage)
                }
            }
        }
    }

    inner class TokenRefresher : Authenticator {

        override fun authenticate(route: Route?, response: okhttp3.Response): Request? {
            if (!isUserAPIRequest(response.request())) return response.request()

            val refreshToken = tokenStore?.refreshToken
                ?: DataManager.getMusicAPIAuthorizationData(context)?.refreshToken
            if (refreshToken != null) {
                val refreshCall = accountApi.requestRefreshToken(refreshToken).execute()

                // Store the new token
                val newTokenData = refreshCall.body() ?: return null
                tokenStore = newTokenData
                DataManager.setMusicAPIAuthorizationData(context, newTokenData)

                // Continue the request with new header
                return response.request()
                    .newBuilder()
                    .header("Authorization", "Bearer ${newTokenData.accessToken}")
                    .build()
            } else {
                // TODO: Prompt authentication error
                return null
            }
        }
    }
    // endregion

    companion object {
        private val TAG = SpotifyAPI::class.java.simpleName
    }
}