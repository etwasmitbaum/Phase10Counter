package com.tjEnterprises.phase10Counter.ui.highscores

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tjEnterprises.phase10Counter.data.local.database.Highscore
import com.tjEnterprises.phase10Counter.data.local.models.SettingsModel
import com.tjEnterprises.phase10Counter.data.local.repositories.DatabaseRepository
import com.tjEnterprises.phase10Counter.data.local.repositories.SettingsRepository.SettingsRepositoryImpl
import com.tjEnterprises.phase10Counter.ui.HighscoresUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HighscoresViewModel @Inject constructor(
    private val databaseRepository: DatabaseRepository,
    private val settingsRepository: SettingsRepositoryImpl
) : ViewModel() {

    private val _defaultDontChangeUiWideScreen = SettingsModel().dontChangeUiOnWideScreen
    val dontChangeUiWideScreen: StateFlow<Boolean> =
        settingsRepository.settingsModelFlow.map { settings ->
            settings.dontChangeUiOnWideScreen
        }.catch { _defaultDontChangeUiWideScreen }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = _defaultDontChangeUiWideScreen
        )

    val highscoresUiState: StateFlow<HighscoresUiState> =
        databaseRepository.highscores.map<List<Highscore>, HighscoresUiState>(HighscoresUiState::HighscoresSuccess)
            .catch {
                emit(
                    HighscoresUiState.HighscoresError(it)
                )
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = HighscoresUiState.HighscoresLoading
            )
}