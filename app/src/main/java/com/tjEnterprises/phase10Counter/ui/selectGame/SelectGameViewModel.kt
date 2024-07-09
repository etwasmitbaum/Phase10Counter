package com.tjEnterprises.phase10Counter.ui.selectGame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tjEnterprises.phase10Counter.data.local.models.GameModel
import com.tjEnterprises.phase10Counter.data.local.models.SettingsModel
import com.tjEnterprises.phase10Counter.data.local.repositories.DatabaseRepository
import com.tjEnterprises.phase10Counter.data.local.repositories.SettingsRepository
import com.tjEnterprises.phase10Counter.ui.SelectGameUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectGameViewModel @Inject constructor(
    private val databaseRepository: DatabaseRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    /*
    * This will first combine all the games and the settings into one flow.
    * The combined flow then gets mapped onto SelectGameUiState to pass a single
    * variable to the SelectGameScreen instead of two separate.
    */
    val selectGameUiState: StateFlow<SelectGameUiState> =
        combine(databaseRepository.games, settingsRepository.settingsModelFlow) { games, settings ->
            Pair(games, settings)
        }.map<Pair<List<GameModel>, SettingsModel>, SelectGameUiState> { pair ->
            SelectGameUiState.SelectGameSuccess(pair.first, pair.second)
        }.catch { emit(SelectGameUiState.SelectGameError(it)) }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SelectGameUiState.SelectGameLoading
        )


    fun deleteGameWithData(gameId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            addHighscore(databaseRepository.getGameFromId(gameId))
            databaseRepository.deleteGame(gameId = gameId)
        }
    }

    fun resetGameWithData(gameId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val game = databaseRepository.getGameFromId(gameId = gameId)
            addHighscore(game)
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

    private suspend fun addHighscore(game: GameModel) {
        game.players.forEach { player ->
            if (!player.phasesOpen.contains(true)) {
                databaseRepository.insertHighscore(
                    playerName = player.name, point = player.pointSum
                )
            }
        }
    }
}