package com.tjEnterprises.phase10Counter.ui.selectGame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tjEnterprises.phase10Counter.data.DatabaseRepository
import com.tjEnterprises.phase10Counter.data.GameModel
import com.tjEnterprises.phase10Counter.ui.GamesUiState
import com.tjEnterprises.phase10Counter.ui.GamesUiState.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectGameViewModel @Inject constructor(
    private val databaseRepository: DatabaseRepository
) : ViewModel() {

    private val _gamesUiState = MutableStateFlow<GamesUiState>(GamesLoading)
    val gamesUiState: StateFlow<GamesUiState> get() = _gamesUiState

    init {
        viewModelScope.launch {
            try {
                // Call suspend function within coroutine
                val games = databaseRepository.getAllGames().first()

                // Emit GamesSuccess state with the list of games
                _gamesUiState.value = GamesSuccess(games)
            } catch (e: Exception) {
                // Emit GamesError state on exception
                _gamesUiState.value = GamesError(e)
            }
        }
    }

    fun deleteGameWithData(game: GameModel) {
        /*viewModelScope.launch(Dispatchers.IO) {
            val playersToDelete = databaseRepository.getPlayerFromGame(game.id)
                .map { players -> players.filter { it.id == game.id } }

            // collect is a suspend function, so it need its own coroutine
            // else removeGame(game) will not be called
            viewModelScope.launch(Dispatchers.IO) {
                playersToDelete.collect { listOfPlayer ->
                    listOfPlayer.forEach { player ->
                        databaseRepository.getPointHistoryFromPlayerId(player.id).forEach { pointHistory ->
                            databaseRepository.removePointHistory(pointHistory)
                        }
                        databaseRepository.deletePlayer(player)
                    }
                }
            }
            databaseRepository.removeGame(game)
        }*/
    }

    fun resetGameWithData(game: GameModel) {
        /*viewModelScope.launch(Dispatchers.IO) {
            val playersToReset = databaseRepository.getPlayerFromGame(game.id)
                .map { players -> players.filter { it.gameID == game.id } }

            viewModelScope.launch(Dispatchers.IO) {
                playersToReset.collect { listOfPlayer ->
                    listOfPlayer.forEach { player ->
                        databaseRepository.getPointHistoryFromPlayerId(player.id).forEach { pointHistory ->
                            databaseRepository.removePointHistory(pointHistory)
                        }
                        player.phases = ""
                        databaseRepository.changePlayerPhases(player)
                    }
                }
            }
        }*/
    }
}

