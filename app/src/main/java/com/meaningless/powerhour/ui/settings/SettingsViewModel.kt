package com.meaningless.powerhour.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.meaningless.powerhour.data.database.DataManager
import com.meaningless.powerhour.data.models.GameConfig

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val _maxRounds = MutableLiveData<Int>()
    private val _isLoggedOut = MutableLiveData<Boolean>()

    val maxRounds: LiveData<Int> = _maxRounds
    val isLoggedOut: LiveData<Boolean> = _isLoggedOut

    fun setMaxRounds(rounds: Int) {
//        val config = DataManager.getGameConfig(getApplication()) ?: GameConfig()
//        config?.roundDuration = duration
//        DataManager.setGameConfig()
    }

    fun setRoundDuration(duration: Long) {
        val config = DataManager.getGameConfig(getApplication()) ?: GameConfig()
        config?.roundDuration = duration
        //DataManager.setGameConfig()
    }

    fun logout() {
        DataManager.setMusicAPIAuthorizationData(getApplication(), null)
    }
}