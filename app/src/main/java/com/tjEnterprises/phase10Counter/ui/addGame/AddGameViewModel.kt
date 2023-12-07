package com.tjEnterprises.phase10Counter.ui.addGame

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tjEnterprises.phase10Counter.data.DatabaseRepository
import com.tjEnterprises.phase10Counter.data.local.database.Game
import com.tjEnterprises.phase10Counter.data.local.database.Player
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

    fun addGame(game: Game, playerNames: List<String>){
        viewModelScope.launch (Dispatchers.IO) {
            val newGameID = databaseRepository.insertGame(game)
            for(i in playerNames.indices.reversed()){
                databaseRepository.insertPlayer(player = Player(id = 0L, name = playerNames[i], gameID = newGameID))
            }
            newCreatedGameID.value = newGameID
        }
    }
}