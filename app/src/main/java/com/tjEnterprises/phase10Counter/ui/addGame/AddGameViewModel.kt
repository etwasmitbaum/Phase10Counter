package com.tjEnterprises.phase10Counter.ui.addGame

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tjEnterprises.phase10Counter.data.local.repositories.DatabaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddGameViewModel @Inject constructor(
    private val databaseRepository: DatabaseRepository
) : ViewModel() {

    private val _newCreatedGameId = MutableStateFlow(-1L)
    val newCreatedGameId: StateFlow<Long> = _newCreatedGameId

    val tempPlayerNames = mutableStateListOf<String>()

    fun removeTempPlayerName(idx: Int){
        tempPlayerNames.removeAt(idx)
    }

    fun resetNewCreatedGameID (){
        _newCreatedGameId.value = -1
    }

    fun addGame(gameName: String, playerNames: List<String>){
        viewModelScope.launch (Dispatchers.IO) {
            val newGameId = databaseRepository.insertGame(gameName)
            for(i in playerNames.indices.reversed()){
                databaseRepository.insertPlayer(playerName = playerNames[i], gameId = newGameId)
            }
            _newCreatedGameId.value = newGameId
        }
    }
}