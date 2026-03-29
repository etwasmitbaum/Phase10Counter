package com.tjEnterprises.phase10Counter.ui.editGame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tjEnterprises.phase10Counter.data.local.database.Player
import com.tjEnterprises.phase10Counter.data.local.models.PointHistoryItem
import com.tjEnterprises.phase10Counter.data.local.models.SettingsModel
import com.tjEnterprises.phase10Counter.data.local.repositories.DatabaseRepository
import com.tjEnterprises.phase10Counter.data.local.repositories.SettingsRepository
import com.tjEnterprises.phase10Counter.ui.EditGameUiState
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
class EditGameViewModel @Inject constructor(
    private val databaseRepository: DatabaseRepository,
    private val settingsRepository: SettingsRepository
): ViewModel() {
    private val _defaultDontChangeUiWideScreen = SettingsModel().dontChangeUiOnWideScreen
    val dontChangeUiWideScreen: StateFlow<Boolean> = settingsRepository.settingsModelFlow.map { settings ->
        settings.dontChangeUiOnWideScreen
    }.catch { _defaultDontChangeUiWideScreen }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = _defaultDontChangeUiWideScreen
    )

    private val _editGameUiState = MutableStateFlow<EditGameUiState>(EditGameUiState.EditGameLoading)
    val editGameUiState: StateFlow<EditGameUiState> = _editGameUiState

    fun setGameFromId(gameId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.getGameFlowFromId(gameId).collect {
                _editGameUiState.value = EditGameUiState.EditGameSuccess(it)
            }
        }
    }

    fun addPointHistoryEntry(point: Long, gameId: Long, playerId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.insertPointHistory(point = point, gameId = gameId, playerId = playerId)
        }
    }

    fun updatePointHistoryEntry(pointHistoryItem: PointHistoryItem) {
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.updatePointHistoryEntry(pointHistoryItem)
        }
    }

    fun deletePointHistoryEntry(pointHistoryItem: PointHistoryItem) {
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.deletePointHistoryEntry(pointHistoryItem)
        }
    }

    fun insertPlayer(playerName: String, gameId: Long) {
        viewModelScope.launch {
            databaseRepository.insertPlayer(playerName, gameId)
        }
    }

    fun updatePlayer(player: Player) {
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.updatePlayer(player)
        }
    }

    fun deletePlayer(playerId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.deletePlayer(playerId)
        }
    }

    fun savePlayerPhases(playerId: Long, gameId: Long, openPhases: List<Boolean>) {
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.updatePlayerPhases(playerId, gameId, openPhases)
        }
    }

    fun updateGameName(gameId: Long, gameName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.updateGameName(gameId, gameName)
        }
    }
}