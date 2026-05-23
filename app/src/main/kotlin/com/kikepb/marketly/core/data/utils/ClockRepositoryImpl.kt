package com.kikepb.marketly.core.data.utils

import com.kikepb.marketly.core.domain.utils.ClockRepository
import java.time.Instant
import javax.inject.Inject

class ClockRepositoryImpl @Inject constructor(): ClockRepository {

    override fun now(): Instant = Instant.now()
}