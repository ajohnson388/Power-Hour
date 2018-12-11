package com.meaningless.powerhour.data.services

import android.os.CountDownTimer
import com.meaningless.powerhour.data.models.Intervals

class Clock : CountDownTimer(Intervals.ONE_MINUTE, Intervals.ONE_MINUTE) {

    // region Fields
    var listener: Listener? = null
    // endregion

    // region Count Down Timer
    override fun onFinish() {
        listener?.onMinutePassed()
    }

    override fun onTick(remainingMilliseconds: Long) {}
    // endregion

    // region Associated Types
    interface Listener {
        fun onMinutePassed()
    }
    // endregion
}