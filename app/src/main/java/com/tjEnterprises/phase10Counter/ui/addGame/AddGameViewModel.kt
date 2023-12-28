package com.tjEnterprises.phase10Counter.ui.addGame

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tjEnterprises.phase10Counter.data.local.repositories.DatabaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddGameViewModel @Inject constructor(
    private val databaseRepository: DatabaseRepository
) : ViewModel() {

    val newCreatedGameID = MutableStateFlow(-1L)

    val tempPlayerNames = mutableStateListOf<String>()

    fun removeTempPlayerName(idx: Int){
        tempPlayerNames.removeAt(idx)
    }

    fun resetNewCreatedGameID (){
        newCreatedGameID.value = -1
    }

    fun addGame(gameName: String, playerNames: List<String>){
        viewModelScope.launch (Dispatchers.IO) {
            val newGameId = databaseRepository.insertGame(gameName)
            for(i in playerNames.indices.reversed()){
                val newPlayerId = databaseRepository.insertPlayer(playerName = playerNames[i], gameId = newGameId)
                databaseRepository.insertPhasesForPlayer(playerId = newPlayerId, gameId = newGameId)
            }
            newCreatedGameID.value = newGameId
        }
    }
}