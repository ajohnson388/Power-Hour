package com.meaningless.powerhour.ui.game

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.meaningless.powerhour.data.services.GameService

/**
 * Bridges the logic of the power hour game service to the UI.
 * */
class GameActivityViewModel : ViewModel() {

    // region Properties
    private lateinit var application: Application
    private val _roundLiveData = MutableLiveData<Int>()
    private val onRoundUpdated = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent == null) return
            _roundLiveData.value = intent.getIntExtra(GameService.MessageKey.CURRENT_ROUND, 0)
            Log.d(TAG, "Round changed to ${_roundLiveData.value}")
        }
    }

    var maxRounds = 60
    var roundDuration = 60
    val roundLiveData: LiveData<Int> get() = _roundLiveData
    // endregion

    // region Setup Functions
    fun init(application: Application) {
        this.application = application
    }
    // endregion

    // region Control Functions
    fun toggleGameStart(): Boolean =
        if (_roundLiveData.value ?: 0 == 0) {
            startGame()
            true
        } else {
            endGame()
            false
        }

    private fun startGame() {
        Log.d(TAG, "Game starting")
        registerReceiver()
        Intent(application, GameService::class.java).also {
            it.putExtra(GameService.IntentKey.GAME_DURATION, maxRounds)
            it.putExtra(GameService.IntentKey.INTERVAL_DURATION, roundDuration)
            application.startService(it)
        }
    }

    private fun endGame() {
        Log.d(TAG, "Game ending")
        Intent(application, GameService::class.java).also {
            application.stopService(it)
        }
    }

    private fun registerReceiver() {
        Log.d(TAG, "Receiver registered")
        IntentFilter().apply {
            addAction(GameService.IntentKey.NOTIFICATION)
            application.registerReceiver(onRoundUpdated, this)
        }
    }
    // endregion

    // region Associated Types
    companion object {
        private val TAG = GameActivityViewModel::class.java.simpleName
    }
    // endregion
}