package com.kikepb.marketly.core.presentation.utils

import kotlin.math.roundToInt

fun Double.roundToTwoDecimals(): Double = (this * 100).roundToInt() / 100.0
