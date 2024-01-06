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
import com.tjEnterprises.phase10Counter.data.local.models.SettingsModel
import com.tjEnterprises.phase10Counter.data.local.repositories.DatabaseRepository
import com.tjEnterprises.phase10Counter.data.local.repositories.SettingsRepository
import com.tjEnterprises.phase10Counter.ui.GameUiState
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
class GameViewModel @Inject constructor(
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

    private val _gameUiState = MutableStateFlow<GameUiState>(GameUiState.GameLoading)
    val gameUiState: StateFlow<GameUiState> = _gameUiState

    fun setGameFromId(gameId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.getGameFlowFromId(gameId).collect {
                _gameUiState.value = GameUiState.GameSuccess(it)
            }
        }
    }

    fun addPointHistoryEntry(point: Long, gameId: Long, playerId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.insertPointHistory(point = point, gameId = gameId, playerId = playerId)
        }
    }

    fun savePlayerPhases(playerId: Long, gameId: Long, openPhases: List<Boolean>) {
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.updatePlayerPhases(playerId, gameId, openPhases)
        }
    }
}

