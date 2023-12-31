package com.tjEnterprises.phase10Counter.ui.updateChecker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tjEnterprises.phase10Counter.data.local.models.SettingsModel
import com.tjEnterprises.phase10Counter.data.local.repositories.SettingsRepository
import com.tjEnterprises.phase10Counter.data.network.repositories.UpdateCheckerCodes
import com.tjEnterprises.phase10Counter.data.network.repositories.UpdateCheckerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdateCheckerViewModel @Inject constructor(
    private val updateCheckerRepository: UpdateCheckerRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _defaultCheckForUpdates = SettingsModel().checkForUpdates

    val checkForUpdates: StateFlow<Boolean> = settingsRepository.settingsModelFlow.map { settings ->
        settings.checkForUpdates
    }.catch { _defaultCheckForUpdates }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = _defaultCheckForUpdates
    )

    private val _versionNumber =
        MutableStateFlow<Int>(UpdateCheckerCodes.NO_RESPONSE)
    val versionNumber: StateFlow<Int> = _versionNumber

    init {
        viewModelScope.launch {
            // Try 3 times, in case there is some unexpected trouble
            for (i in 0..2) {
                try {
                    _versionNumber.value = updateCheckerRepository.getLatestReleaseVersionNumber()
                    if (_versionNumber.value != UpdateCheckerCodes.ERROR_GETTING_LATEST_VERSION_NUMBER) {
                        break
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }

    }

}