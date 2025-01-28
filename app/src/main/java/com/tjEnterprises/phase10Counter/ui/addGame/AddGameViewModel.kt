package com.tjEnterprises.phase10Counter.ui.addGame

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tjEnterprises.phase10Counter.data.local.models.GameType
import com.tjEnterprises.phase10Counter.data.local.models.SettingsModel
import com.tjEnterprises.phase10Counter.data.local.repositories.DatabaseRepository
import com.tjEnterprises.phase10Counter.data.local.repositories.SettingsRepository
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
class AddGameViewModel @Inject constructor(
    private val databaseRepository: DatabaseRepository,
    private val settingsRepository: SettingsRepository.SettingsRepositoryImpl
) : ViewModel() {

    private val _defaultDontChangeUiWideScreen = SettingsModel().dontChangeUiOnWideScreen
    val dontChangeUiWideScreen: StateFlow<Boolean> = settingsRepository.settingsModelFlow.map { settings ->
        settings.dontChangeUiOnWideScreen
    }.catch { _defaultDontChangeUiWideScreen }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = _defaultDontChangeUiWideScreen
    )

    private val _newCreatedGameId = MutableStateFlow(-1L)
    val newCreatedGameId: StateFlow<Long> = _newCreatedGameId

    val tempPlayerNames = mutableStateListOf<String>()

    fun removeTempPlayerName(idx: Int){
        tempPlayerNames.removeAt(idx)
    }

    fun resetNewCreatedGameID (){
        _newCreatedGameId.value = -1
    }

    fun addGame(gameName: String, gameType: GameType.Type, playerNames: List<String>){
        viewModelScope.launch (Dispatchers.IO) {
            val newGameId = databaseRepository.insertGame(gameName, gameType)
            for(i in playerNames.indices.reversed()){
                databaseRepository.insertPlayer(playerName = playerNames[i], gameId = newGameId)
            }
            _newCreatedGameId.value = newGameId
        }
    }
}