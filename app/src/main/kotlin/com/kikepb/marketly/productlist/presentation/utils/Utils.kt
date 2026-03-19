package com.kikepb.marketly.productlist.presentation.utils

import java.util.Locale

fun Double.toPriceAmount() = String.format(Locale.getDefault(), "%.2f €", this)