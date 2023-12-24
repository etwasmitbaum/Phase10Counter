/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tjEnterprises.phase10Counter.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tjEnterprises.phase10Counter.data.DatabaseRepository
import com.tjEnterprises.phase10Counter.data.PhasesModel
import com.tjEnterprises.phase10Counter.data.local.database.Player
import com.tjEnterprises.phase10Counter.data.local.database.PointHistory
import com.tjEnterprises.phase10Counter.ui.GameUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val databaseRepository: DatabaseRepository
) : ViewModel() {

    private val gameID = MutableStateFlow(1L)

    // TODO check if it makes a new db request when changing gameID

    // TODO sort out the games in here instead of "GameScreen.kt" to retrieve only one single game
    // TODO Combine all states into one

    private val _gameUiState = MutableStateFlow<GameUiState>(GameUiState.GameLoading)
    val gameUiState: StateFlow<GameUiState> get() = _gameUiState

    init {

        loadGame()

    }

    private fun loadGame() {


        val gameId = this.gameID.value

        viewModelScope.launch {

            try {
                // Call suspend function within coroutine
                val game = databaseRepository.getGameFromId(gameId)

                // Emit GamesSuccess state with the list of games
                _gameUiState.value = GameUiState.GameSuccess(game)
            } catch (e: Exception) {
                // Emit GamesError state on exception
                _gameUiState.value = GameUiState.GameError(e)
            }

        }
    }


    fun setGameId(gameId: Long) {
        gameID.value = gameId

        loadGame()

    }

    fun addPointHistoryEntry(pointHistory: PointHistory) {
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.insertPointHistory(pointHistory = pointHistory)

            loadGame()

        }
    }

    fun changePlayerPhases(phases: List<PhasesModel>){
        viewModelScope.launch (Dispatchers.IO) {
            for (phase in phases) {
                databaseRepository.changePlayerPhase(phase = phase.phase, state = phase.state, playerId = phase.playerId)
            }
            loadGame()

        }
    }
}

