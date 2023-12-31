package com.tjEnterprises.phase10Counter.ui.highscores

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tjEnterprises.phase10Counter.data.local.database.Highscore
import com.tjEnterprises.phase10Counter.data.local.repositories.DatabaseRepository
import com.tjEnterprises.phase10Counter.ui.HighscoresUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HighscoresViewModel @Inject constructor(databaseRepository: DatabaseRepository) :
    ViewModel() {
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