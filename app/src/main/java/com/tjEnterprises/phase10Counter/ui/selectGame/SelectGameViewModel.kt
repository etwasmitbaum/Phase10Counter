package com.tjEnterprises.phase10Counter.ui.selectGame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tjEnterprises.phase10Counter.data.DatabaseRepository
import com.tjEnterprises.phase10Counter.data.local.GameModel
import com.tjEnterprises.phase10Counter.ui.GamesUiState
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
class SelectGameViewModel @Inject constructor(
    private val databaseRepository: DatabaseRepository
) : ViewModel() {

    val gamesUiState: StateFlow<GamesUiState> =
        databaseRepository.games.map<List<GameModel>, GamesUiState>(GamesUiState::GamesSuccess)
            .catch {
                emit(GamesUiState.GamesError(it))
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = GamesUiState.GamesLoading
            )

    fun deleteGameWithData(gameId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.deleteGame(gameId = gameId)
        }
    }

    fun resetGameWithData(gameId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.getGameFromId(gameId = gameId).collect { game ->
                game.players.forEach { player ->
                    databaseRepository.deletePointHistoryOfPlayer(playerId = player.playerId)
                    val phaseReset = mutableListOf<Boolean>()
                    for (i in 0..9) {
                        phaseReset.add(true)
                    }
                    databaseRepository.updatePlayerPhases(
                        playerId = player.playerId, gameId = gameId, openPhases = phaseReset
                    )
                }
            }
        }
    }
}