package com.kikepb.marketly.core.domain.model

sealed class ThemeModeModel(val id: Int) {
    data object SYSTEM: ThemeModeModel(id = 0)
    data object LIGHT: ThemeModeModel(id = 1)
    data object DARK: ThemeModeModel(id = 2)
}