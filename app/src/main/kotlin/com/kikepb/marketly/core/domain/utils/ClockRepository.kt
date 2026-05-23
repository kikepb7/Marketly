package com.kikepb.marketly.core.domain.utils

import java.time.Instant

interface ClockRepository {
    fun now(): Instant
}