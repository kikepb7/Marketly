package com.kikepb.marketly.core.fakes

import com.kikepb.marketly.core.domain.utils.ClockRepository
import java.time.Instant

class FakeClockRepository : ClockRepository {
    private var currentTime: Instant = Instant.now()

    fun setTime(time: Instant) { currentTime = time }
    fun advanceTime(seconds: Long) { currentTime = currentTime.plusSeconds(seconds) }
    override fun now(): Instant = currentTime
}