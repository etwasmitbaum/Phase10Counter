package com.tjEnterprises.phase10Counter.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tjEnterprises.phase10Counter.data.local.models.SettingsModel
import com.tjEnterprises.phase10Counter.data.local.repositories.SettingsRepository
import com.tjEnterprises.phase10Counter.ui.SettingsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    val settingsUiState: StateFlow<SettingsUiState> =
        settingsRepository.settingsModelFlow.map<SettingsModel, SettingsUiState.SettingsSuccess>(
            SettingsUiState::SettingsSuccess
        ).catch { SettingsUiState.SettingsError(it) }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SettingsUiState.SettingsLoading
        )

    fun updateCheckForUpdates(checkForUpdates: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepository.updateCheckForUpdates(checkForUpdates)
        }
    }
    fun updateUseDynamicColors(useDynamicColors: Boolean){
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepository.updateUseDynamicColors(useDynamicColors)
        }
    }
    fun updateUseSystemTheme(useSystemTheme: Boolean){
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepository.updateUseSystemTheme(useSystemTheme)
        }
    }
    fun updateUseDarkTheme(useDarkTheme: Boolean){
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepository.updateUseDarkTheme(useDarkTheme)
        }
    }
    fun updateDontChangeUiWideScreen(dontChangeUiWideScreen: Boolean){
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepository.updateDontChangeUiWideScreen(dontChangeUiWideScreen)
        }
    }
}