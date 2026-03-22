package com.kikepb.marketly.productlist.domain.repository

import com.kikepb.marketly.core.domain.model.ThemeModeModel
import com.kikepb.marketly.productlist.domain.model.SortOptionModel
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val inStockOnly: Flow<Boolean>
    val themeMode: Flow<ThemeModeModel>
    val selectedCategory: Flow<String?>
    val filtersVisible: Flow<Boolean>
    val sortOption: Flow<SortOptionModel>

    suspend fun setInStockOnly(value: Boolean)
    suspend fun setThemeMode(value: ThemeModeModel)
    suspend fun setSelectedCategory(value: String?)
    suspend fun setFiltersVisible(value: Boolean)
    suspend fun setSortOption(value: SortOptionModel)
}