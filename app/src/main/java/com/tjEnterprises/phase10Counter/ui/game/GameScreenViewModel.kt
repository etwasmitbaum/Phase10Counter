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
import com.tjEnterprises.phase10Counter.data.local.database.Game
import com.tjEnterprises.phase10Counter.data.local.database.Player
import com.tjEnterprises.phase10Counter.data.local.database.PointHistory
import com.tjEnterprises.phase10Counter.ui.GamesUiState
import com.tjEnterprises.phase10Counter.ui.GamesUiState.GamesError
import com.tjEnterprises.phase10Counter.ui.GamesUiState.GamesLoading
import com.tjEnterprises.phase10Counter.ui.GamesUiState.GamesSuccess
import com.tjEnterprises.phase10Counter.ui.PlayerUiState
import com.tjEnterprises.phase10Counter.ui.PlayerUiState.PlayersError
import com.tjEnterprises.phase10Counter.ui.PlayerUiState.PlayersLoading
import com.tjEnterprises.phase10Counter.ui.PlayerUiState.PlayersSuccess
import com.tjEnterprises.phase10Counter.ui.PointHistoryUiState
import com.tjEnterprises.phase10Counter.ui.PointHistoryUiState.PointHistoryError
import com.tjEnterprises.phase10Counter.ui.PointHistoryUiState.PointHistorySuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val databaseRepository: DatabaseRepository
) : ViewModel() {

    private val gameID = MutableStateFlow(1L)

    // TODO check if it makes a new db request when changing gameID
    val playerUiState: StateFlow<PlayerUiState> =
        databaseRepository.players.combine(gameID) { list, gameID ->
                list.filter { item -> item.gameID == gameID }
            }.map<List<Player>, PlayerUiState>(::PlayersSuccess).catch { emit(PlayersError(it)) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = PlayersLoading
            )

    // TODO sort out the games in here instead of "GameScreen.kt" to retrieve only one single game
    // TODO Combine all states into one

    val gamesUiState: StateFlow<GamesUiState> =
        databaseRepository.games.map<List<Game>, GamesUiState>(::GamesSuccess)
            .catch { emit(GamesError(it)) }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = GamesLoading
            )

    val pointHistoryUiState: StateFlow<PointHistoryUiState> =
        databaseRepository.pointHistory.map<List<PointHistory>, PointHistoryUiState>(::PointHistorySuccess)
            .catch { emit ( PointHistoryError(it) ) }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = PointHistoryUiState.PointHistoryLoading
            )

    fun setGameId(gameId: Long) {
        gameID.value = gameId
    }

    fun addPointHistoryEntry(pointHistory: PointHistory) {
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.insertPointHistory(pointHistory = pointHistory)
        }
    }

    fun savePlayerPhases(player: Player){
        viewModelScope.launch (Dispatchers.IO) {
            databaseRepository.changePlayerPhases(player)
        }
    }
}

