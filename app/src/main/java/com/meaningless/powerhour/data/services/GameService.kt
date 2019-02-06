package com.meaningless.powerhour.data.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.meaningless.powerhour.data.music.common.interfaces.MusicHandler
import com.meaningless.powerhour.data.music.common.models.Intervals
import com.meaningless.powerhour.data.music.common.models.TrackMetaData
import com.meaningless.powerhour.data.music.spotify.api.SpotifyHandler
import com.meaningless.powerhour.utils.DataUtils

/**
 * This services acts as a timer that skips the songs in a music playlist and plays feedback on every
 * round change. The service ends when either the game is finished or when the application is closed.
 * */
class GameService : Service(), Clock.Listener, SpotifyHandler.Listener {

    // region Fields
    private var clock = Clock()
    private var currentTrack: TrackMetaData? = null
    private var currentRound = 1
    private var numberOfRounds = 60
    private lateinit var musicHandler: MusicHandler
    // endregion

    // region Lifecycle
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        configureGame(intent ?: return START_STICKY)

        // Trigger the series of callbacks to start the game
        musicHandler.connect(applicationContext)
        return START_STICKY
    }

    private fun configureGame(intent: Intent) {
        // Configure clock
        clock = Clock(
            intent.getLongExtra(
                IntentKey.INTERVAL_DURATION,
                Intervals.ONE_MINUTE
            )
        )

        // Configure the number of rounds for the game
        numberOfRounds = intent.getIntExtra(
            IntentKey.NUMBER_OF_ROUNDS,
            60
        )

        // Configure the music handler for the game
        val musicHandlerRawType = intent.getStringExtra(IntentKey.MUSIC_HANDLER_TYPE)
            ?: MusicHandler.Type.SPOTIFY.rawValue
        val musicHandlerType = MusicHandler.Type.from(musicHandlerRawType)
        when (musicHandlerType) {
            MusicHandler.Type.SPOTIFY -> {
                val spotifyHandler = SpotifyHandler()
                spotifyHandler.setListener(this)
                musicHandler = spotifyHandler
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        endGame()
        super.onDestroy()
    }
    // endregion

    // region Control Methods
    private fun skipRound() {
        currentRound++
        broadcastMinutePassed()
        musicHandler.skip(applicationContext)
        clock.start()
    }

    private fun startGame() {
        // Set the initial round
        currentRound = 1

        // Start the timer
        clock.setListener(this)
        clock.start()
        broadcastMinutePassed()
    }

    private fun endGame() {
        // Cleanup the clock
        clock.cancel()

        // Disconnect the music
        musicHandler.stop()
        musicHandler.disconnect()

        // Reset the data
        currentRound = 0
        currentTrack = null

        broadcastMinutePassed()
        broadcastTrackChanged()
        stopSelf()
    }

    private fun broadcastMinutePassed() =
        broadcast(Intent().also {
            it.action = IntentKey.ROUND_CHANGED_NOTIFICATION
            it.putExtra(MessageKey.CURRENT_ROUND, currentRound)
        })

    private fun broadcastError(error: String) =
        broadcast(Intent().also {
            it.action = IntentKey.ERROR_NOTIFICATION
            it.putExtra(MessageKey.ERROR, error)
        })

    private fun broadcastTrackChanged() =
        broadcast(Intent().also {
            val track = currentTrack
            it.action = IntentKey.TRACK_CHANGED_NOTIFICATION
            if (track != null) it.putExtra(MessageKey.TRACK, DataUtils.makeJsonString(track))
            sendBroadcast(it)
        })

    private fun broadcast(intent: Intent) =
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    // endregion

    // region Clock Listener
    override fun onTimerFinished() = if (currentRound == numberOfRounds) endGame() else skipRound()

    override fun onConnected() {
        musicHandler.start(applicationContext)
    }

    override fun onError(error: String) {
        broadcastError(error)
    }

    override fun onTrackChanged(track: TrackMetaData) {
        if (currentTrack == null) startGame()
        currentTrack = track
        broadcastTrackChanged()
    }
    // endregion

    // region Associated Types
    object IntentKey {
        const val ROUND_CHANGED_NOTIFICATION = "com.meaningless.powerhour.round"
        const val ERROR_NOTIFICATION = "com.meaningless.powerhour.error"
        const val TRACK_CHANGED_NOTIFICATION = "com.meaningless.powerhour.track"
        const val NUMBER_OF_ROUNDS = "numberOfRounds"
        const val INTERVAL_DURATION = "intervalDuration"
        const val MUSIC_HANDLER_TYPE = "musicHandlerType"
    }

    object MessageKey {
        const val CURRENT_ROUND = "currentRound"
        const val IS_GAME_OVER = "isGameOver"
        const val ERROR = "error"
        const val TRACK = "track"
    }
    // endregion
}