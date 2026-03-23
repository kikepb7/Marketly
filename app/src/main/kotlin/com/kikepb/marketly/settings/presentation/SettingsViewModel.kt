package com.kikepb.marketly.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kikepb.marketly.core.domain.model.ThemeModeModel
import com.kikepb.marketly.productlist.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(value = SettingsState())
    val state = _state.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        combine(
            flow = settingsRepository.inStockOnly,
            flow2 = settingsRepository.themeMode
        ) { inStockOnly, themeMode ->
            _state.update { SettingsState(inStockOnly = inStockOnly, themeMode = themeMode) }
        }.launchIn(scope = viewModelScope)
    }

    fun setInStockOnly(newState: Boolean) {
        viewModelScope.launch {
            settingsRepository.setInStockOnly(value = newState)
        }
    }

    fun setThemeMode(themeMode: ThemeModeModel) {
        viewModelScope.launch {
            settingsRepository.setThemeMode(value = themeMode)
        }
    }
}

data class SettingsState(
    val inStockOnly: Boolean = false,
    val themeMode: ThemeModeModel = ThemeModeModel.SYSTEM
)