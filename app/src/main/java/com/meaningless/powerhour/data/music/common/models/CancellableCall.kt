package com.meaningless.powerhour.data.music.common.models

import com.meaningless.powerhour.data.music.common.interfaces.Cancellable
import retrofit2.Call

class CancellableCall<T>(private val call: Call<T>) :
    Cancellable {

    override fun cancel() {
        call.cancel()
    }
}