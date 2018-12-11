package com.meaningless.powerhour.data.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.meaningless.powerhour.data.models.Intervals
import com.meaningless.powerhour.data.music.MusicHandler
import com.meaningless.powerhour.data.music.SpotifyHandler

/**
 * This services acts as a timer that skips the songs in a music playlist and plays feedback on every
 * round change. The service ends when either the game is finished or when the application is closed.
 * */
class GameService : Service(), Clock.Listener, SpotifyHandler.Listener {

    // region Fields
    private val clock = Clock()
    private var currentRound = 1
    private var gameDuration = Intervals.ONE_HOUR
    private var intervalDuration = Intervals.ONE_MINUTE
    private lateinit var musicHandler: MusicHandler
    // endregion

    // region Lifecycle
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) return START_STICKY

        // Configure the game
        gameDuration = intent.getLongExtra(
            IntentKey.GAME_DURATION,
            Intervals.ONE_HOUR
        )
        intervalDuration = intent.getLongExtra(
            IntentKey.INTERVAL_DURATION,
            Intervals.ONE_MINUTE
        )

        // Determine the music player we need to connect to
        val musicHandlerRawType = intent.getStringExtra(IntentKey.MUSIC_HANDLER_TYPE)
            ?: MusicHandler.Type.SPOTIFY.rawValue
        val musicHandlerType = MusicHandler.Type.from(musicHandlerRawType)
        when (musicHandlerType) {
            MusicHandler.Type.SPOTIFY -> {
                val spotifyHandler = SpotifyHandler(applicationContext)
                spotifyHandler.listener = this
                musicHandler = spotifyHandler
            }
        }

        // Start the game
        startGame()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

//    override fun stopService(name: Intent?): Boolean {
//        endGame()
//        return true
//    }

    override fun onDestroy() {
        endGame()
        super.onDestroy()
    }
    // endregion

    // region Control Methods
    private fun skipRound() {
        currentRound++
        broadcastMinutePassed()
        musicHandler.skip()
        clock.start()
    }

    private fun startGame() {
        // Update the states
        currentRound = 1

        // Start the clock
        clock.listener = this
        clock.start()

        // Connect and start the music
        musicHandler.connect()

        broadcastMinutePassed()
    }

    private fun endGame() {
        // Cleanup the clock
        clock.cancel()
        clock.listener = null

        // Disconnect the music
        musicHandler.stop()
        musicHandler.disconnect()

        // Reset the round
        currentRound = 0

        broadcastMinutePassed()
        stopSelf()
    }

    private fun broadcastMinutePassed() {
        Intent().also {
            it.action = IntentKey.NOTIFICATION
            it.putExtra(MessageKey.CURRENT_ROUND, currentRound)
            sendBroadcast(it)
        }
    }
    // endregion

    // region Clock Listener
    override fun onMinutePassed() = if (currentRound == 60) endGame() else skipRound()

    override fun onConnected() {
        musicHandler.start()
    }
    // endregion

    // region Associated Types
    object IntentKey {
        const val NOTIFICATION = "com.meaningless.powerhour.round"
        const val GAME_DURATION = "gameDuration"
        const val INTERVAL_DURATION = "intervalDuration"
        const val MUSIC_HANDLER_TYPE = "musicHandlerType"
    }

    object MessageKey {
        const val CURRENT_ROUND = "currentRound"
        const val IS_GAME_OVER = "isGameOver"
    }
    // endregion
}