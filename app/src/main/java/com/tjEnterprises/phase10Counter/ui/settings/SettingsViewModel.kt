package com.tjEnterprises.phase10Counter.ui.settings

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.MutableFloatState
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tjEnterprises.phase10Counter.data.FileUtils.Companion.copyFileWithUri
import com.tjEnterprises.phase10Counter.data.local.database.AppDatabase
import com.tjEnterprises.phase10Counter.data.local.models.SettingsModel
import com.tjEnterprises.phase10Counter.data.local.repositories.SettingsRepository
import com.tjEnterprises.phase10Counter.ui.SettingsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
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

    private val _copyError: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val copyError: StateFlow<Boolean> = _copyError

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

    fun updateUseDynamicColors(useDynamicColors: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepository.updateUseDynamicColors(useDynamicColors)
        }
    }

    fun updateUseSystemTheme(useSystemTheme: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepository.updateUseSystemTheme(useSystemTheme)
        }
    }

    fun updateUseDarkTheme(useDarkTheme: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepository.updateUseDarkTheme(useDarkTheme)
        }
    }

    fun updateDontChangeUiWideScreen(dontChangeUiWideScreen: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepository.updateDontChangeUiWideScreen(dontChangeUiWideScreen)
        }
    }

    fun backUpDatabase(context: Context, pickedUri: Uri, progress: MutableFloatState) {
        viewModelScope.launch(Dispatchers.IO) {
            //appDatabase.close()   // No need to close database, it is in TRUNCATE mode
            _copyError.value != copyFileWithUri(
                context = context,
                sourceUri = context.getDatabasePath(AppDatabase.getName()).toUri(),
                destinationUri = pickedUri,
                progress = progress
            )
        }
    }

    fun restoreDatabase(context: Context, pickedUri: Uri, progress: MutableFloatState) {
        viewModelScope.launch(Dispatchers.IO) {
            //appDatabase.close()   // No need to close database, it is in TRUNCATE mode
            _copyError.value != copyFileWithUri(
                context = context,
                sourceUri = pickedUri,
                destinationUri = context.getDatabasePath(AppDatabase.getName()).toUri(),
                progress = progress
            )
        }
    }
}

object WasCopyRestore {
    const val WAS_NEITHER = 0
    const val WAS_BACKUP = 1
    const val WAS_RESTORE = 2
}