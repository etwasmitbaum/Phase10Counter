package com.tjEnterprises.phase10Counter.ui.selectGame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tjEnterprises.phase10Counter.data.DatabaseRepository
import com.tjEnterprises.phase10Counter.data.local.database.Game
import com.tjEnterprises.phase10Counter.data.local.database.Player
import com.tjEnterprises.phase10Counter.ui.GamesUiState
import com.tjEnterprises.phase10Counter.ui.PlayerUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SelectGameViewModel @Inject constructor(
    private val databaseRepository: DatabaseRepository
) : ViewModel() {

    val playerUiState: StateFlow<PlayerUiState> = databaseRepository.players
        .map<List<Player>, PlayerUiState>(PlayerUiState::PlayersSuccess).catch { emit(
            PlayerUiState.PlayersError(
                it
            )
        ) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PlayerUiState.PlayersLoading
        )

    //TODO sort out the games in here instead of "GameScreen.kt" to retrieve only one single game

    val gamesUiState: StateFlow<GamesUiState> =
        databaseRepository.games
            .map<List<Game>, GamesUiState>(GamesUiState::GamesSuccess).catch { emit(
                GamesUiState.GamesError(
                    it
                )
            ) }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = GamesUiState.GamesLoading
            )
}