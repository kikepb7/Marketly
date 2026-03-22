package com.kikepb.marketly.productlist.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.kikepb.marketly.core.domain.model.ThemeModeModel
import com.kikepb.marketly.core.domain.model.ThemeModeModel.DARK
import com.kikepb.marketly.core.domain.model.ThemeModeModel.LIGHT
import com.kikepb.marketly.core.domain.model.ThemeModeModel.SYSTEM
import com.kikepb.marketly.productlist.domain.model.SortOptionModel
import com.kikepb.marketly.productlist.domain.model.SortOptionModel.NONE
import com.kikepb.marketly.productlist.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
): SettingsRepository {

    companion object {
        private val IN_STOCK_ONLY_KEY = booleanPreferencesKey(name = "IN_STOCK_ONLY_KEY")
        private val FILTERS_VISIBLE_KEY = booleanPreferencesKey(name = "FILTERS_VISIBLE_KEY")
        private val SELECTED_CATEGORY_KEY = stringPreferencesKey(name = "SELECTED_CATEGORY_KEY")
        private val THEME_MODE_KEY = intPreferencesKey(name = "THEME_MODE_KEY")
        private val SORT_OPTION_KEY = stringPreferencesKey(name = "SORT_OPTION_KEY")
    }

    private val dataStoreFlow: Flow<Preferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }

    override val inStockOnly = dataStoreFlow.map { preferences -> preferences[IN_STOCK_ONLY_KEY] ?: false }

    override val themeMode = dataStoreFlow.map { preferences ->
        when (preferences[THEME_MODE_KEY]) {
            SYSTEM.id -> SYSTEM
            LIGHT.id -> LIGHT
            DARK.id -> DARK
            else -> SYSTEM
        }
    }
    override val selectedCategory = dataStoreFlow.map { preferences -> preferences[SELECTED_CATEGORY_KEY] }
    override val filtersVisible = dataStoreFlow.map { preferences -> preferences[FILTERS_VISIBLE_KEY] ?: true }
    override val sortOption = dataStoreFlow.map { preferences ->
        val raw = preferences[SORT_OPTION_KEY]
        runCatching { SortOptionModel.valueOf(value = raw ?: NONE.name) }.getOrDefault(defaultValue = NONE)
    }

    override suspend fun setInStockOnly(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[IN_STOCK_ONLY_KEY] = value
        }
    }

    override suspend fun setThemeMode(value: ThemeModeModel) {
        dataStore.edit { preferences ->
            when (value) {
                DARK -> preferences[THEME_MODE_KEY] = DARK.id
                LIGHT -> preferences[THEME_MODE_KEY] = LIGHT.id
                SYSTEM -> preferences[THEME_MODE_KEY] = SYSTEM.id
            }
        }
    }

    override suspend fun setSelectedCategory(value: String?) {
        dataStore.edit { preferences ->
            if (value == null) preferences.remove(SELECTED_CATEGORY_KEY)
            else preferences[SELECTED_CATEGORY_KEY] = value
        }
    }

    override suspend fun setFiltersVisible(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[FILTERS_VISIBLE_KEY] = value
        }
    }

    override suspend fun setSortOption(value: SortOptionModel) {
        dataStore.edit { preferences ->
            preferences[SORT_OPTION_KEY] = value.name
        }
    }
}