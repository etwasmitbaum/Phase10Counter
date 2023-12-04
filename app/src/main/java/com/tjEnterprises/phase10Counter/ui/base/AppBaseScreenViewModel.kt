package com.tjEnterprises.phase10Counter.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tjEnterprises.phase10Counter.data.DatabaseRepository
import com.tjEnterprises.phase10Counter.data.local.database.Game
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
import javax.inject.Singleton

@HiltViewModel
class AppBaseScreenViewModel @Inject constructor(
    private val databaseRepository: DatabaseRepository
) : ViewModel() {

    val gamesUiState: StateFlow<GamesUiState> =
        databaseRepository.games.map<List<Game>, GamesUiState>(GamesUiState::GamesSuccess).catch {
            emit(
                GamesUiState.GamesError(
                    it
                )
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = GamesUiState.GamesLoading
        )

    fun deleteGameWithData(game: Game) {
        viewModelScope.launch(Dispatchers.IO) {
            val playersToDelete = databaseRepository.getPlayerFromGame(game.id)
                .map { players -> players.filter { it.gameID == game.id } }

            // collect is a suspend function, so it need its own coroutine
            // else removeGame(game) will not be called
            viewModelScope.launch(Dispatchers.IO) {
                playersToDelete.collect { listOfPlayer ->
                    listOfPlayer.forEach { player ->
                        // TODO Delete all points from player
                        databaseRepository.deletePlayer(player)
                    }
                }
            }
            databaseRepository.removeGame(game)
        }
    }
}