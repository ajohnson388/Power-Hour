package com.meaningless.powerhour.data.models

import com.meaningless.powerhour.data.music.common.models.Intervals

data class GameConfig(var numberOfRounds: Int = 60, var roundDuration: Long = Intervals.ONE_MINUTE)