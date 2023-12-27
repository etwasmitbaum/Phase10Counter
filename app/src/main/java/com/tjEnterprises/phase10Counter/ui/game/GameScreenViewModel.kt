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
import com.tjEnterprises.phase10Counter.data.local.database.Player
import com.tjEnterprises.phase10Counter.data.local.database.PointHistory
import com.tjEnterprises.phase10Counter.ui.GameUiState
import com.tjEnterprises.phase10Counter.ui.PlayersUiState
import com.tjEnterprises.phase10Counter.ui.PlayersUiState.PlayersSuccess
import com.tjEnterprises.phase10Counter.ui.PointHistoryUiState
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

    private val _gameUiState = MutableStateFlow<GameUiState>(GameUiState.GameLoading)
    val gameUiState: StateFlow<GameUiState> = _gameUiState

    private val _playersUiState = MutableStateFlow<PlayersUiState>(PlayersUiState.PlayersLoading)
    val playersUiState: StateFlow<PlayersUiState> = _playersUiState

    private val _pointHistoryUiState =
        MutableStateFlow<PointHistoryUiState>(PointHistoryUiState.PointHistoryLoading)
    val pointHistoryUiState: StateFlow<PointHistoryUiState> = _pointHistoryUiState

    fun setGameId(gameId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            _gameUiState.value = GameUiState.GameSuccess(databaseRepository.getGameFromId(gameId))
        }
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.getPlayerFromGame(gameId).collect {
                _playersUiState.value = PlayersSuccess(it)
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.getPointHistoryOfGame(gameId).collect {
                _pointHistoryUiState.value = PointHistoryUiState.PointHistorySuccess(it)
            }
        }
    }

    fun addPointHistoryEntry(pointHistory: PointHistory) {
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.insertPointHistory(pointHistory = pointHistory)
        }
    }

    fun savePlayerPhases(player: Player) {
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.changePlayerPhases(player)
        }
    }
}

