package com.kikepb.marketly

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kikepb.marketly.core.domain.model.ThemeModeModel
import com.kikepb.marketly.core.domain.model.ThemeModeModel.SYSTEM
import com.kikepb.marketly.productlist.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    settingsRepository: SettingsRepository
) : ViewModel() {

    val themeMode: Flow<ThemeModeModel> = settingsRepository.themeMode
        .stateIn(
            scope = viewModelScope,
            initialValue = SYSTEM,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000)
        )
}