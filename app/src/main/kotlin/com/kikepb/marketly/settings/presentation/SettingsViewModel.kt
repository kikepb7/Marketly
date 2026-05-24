package com.kikepb.marketly.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kikepb.marketly.core.domain.model.ThemeModeModel
import com.kikepb.marketly.productlist.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val state = combine(
        flow = settingsRepository.inStockOnly,
        flow2 = settingsRepository.themeMode
    ) { inStockOnly, themeMode ->
        SettingsState(inStockOnly = inStockOnly, themeMode = themeMode)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = SettingsState()
    )

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