package com.meaningless.powerhour.data.services

import android.os.CountDownTimer
import com.meaningless.powerhour.data.music.common.models.Intervals
import java.lang.ref.WeakReference

class Clock(duration: Long = Intervals.ONE_MINUTE) : CountDownTimer(duration, duration) {

    private var listener: WeakReference<Listener>? = null

    fun setListener(listener: Listener) {
        this.listener = WeakReference(listener)
    }

    override fun onFinish() {
        listener?.get()?.onTimerFinished()
    }

    override fun onTick(remainingMilliseconds: Long) {}

    interface Listener {
        fun onTimerFinished()
    }
}