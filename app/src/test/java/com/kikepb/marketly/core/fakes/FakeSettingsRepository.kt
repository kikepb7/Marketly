package com.kikepb.marketly.core.fakes

import com.kikepb.marketly.core.domain.model.ThemeModeModel
import com.kikepb.marketly.core.domain.model.ThemeModeModel.SYSTEM
import com.kikepb.marketly.productlist.domain.model.SortOptionModel
import com.kikepb.marketly.productlist.domain.model.SortOptionModel.NONE
import com.kikepb.marketly.productlist.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeSettingsRepository: SettingsRepository {
    private val _inStockOnly = MutableStateFlow(value = false)
    private val _themeMode = MutableStateFlow<ThemeModeModel>(value = SYSTEM)
    private val _selectedCategory = MutableStateFlow<String?>(value = null)
    private val _filtersVisible = MutableStateFlow(value = true)
    private val _sortOption = MutableStateFlow(value = NONE)

    override val inStockOnly: Flow<Boolean> = _inStockOnly.asStateFlow()
    override val themeMode: Flow<ThemeModeModel> = _themeMode.asStateFlow()
    override val selectedCategory: Flow<String?> = _selectedCategory.asStateFlow()
    override val filtersVisible: Flow<Boolean> = _filtersVisible.asStateFlow()
    override val sortOption: Flow<SortOptionModel> = _sortOption.asStateFlow()

    override suspend fun setInStockOnly(value: Boolean) { _inStockOnly.value = value }

    override suspend fun setThemeMode(value: ThemeModeModel) { _themeMode.value = value }

    override suspend fun setSelectedCategory(value: String?) { _selectedCategory.value = value }

    override suspend fun setFiltersVisible(value: Boolean) { _filtersVisible.value = value }

    override suspend fun setSortOption(value: SortOptionModel) { _sortOption.value = value }
}