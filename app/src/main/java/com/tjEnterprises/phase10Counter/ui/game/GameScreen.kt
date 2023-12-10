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

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tjEnterprises.phase10Counter.data.local.database.Player
import com.tjEnterprises.phase10Counter.data.local.database.PointHistory
import com.tjEnterprises.phase10Counter.ui.GamesUiState
import com.tjEnterprises.phase10Counter.ui.PlayerUiState
import com.tjEnterprises.phase10Counter.ui.PointHistoryUiState
import com.tjEnterprises.phase10Counter.ui.component.DefaultScaffold
import com.tjEnterprises.phase10Counter.ui.component.OnePlayerView
import javax.inject.Singleton

@Composable
@Singleton
fun GameScreen(
    modifier: Modifier = Modifier,
    gameId: Long,
    viewModel: GameViewModel = hiltViewModel(),
    openDrawer: () -> Unit,
) {
    val playersUiState by viewModel.playerUiState.collectAsState()
    val gamesUiState by viewModel.gamesUiState.collectAsState()
    val pointHistoryUiState by viewModel.pointHistoryUiState.collectAsState()
    viewModel.setGameId(gameId)

    when (gamesUiState) {
        is GamesUiState.GamesSuccess -> {
            val games = (gamesUiState as GamesUiState.GamesSuccess).data
            when (playersUiState) {
                is PlayerUiState.PlayersSuccess -> {
                    when (pointHistoryUiState) {
                        is PointHistoryUiState.PointHistorySuccess -> {
                            GameScreen(
                                players = (playersUiState as PlayerUiState.PlayersSuccess).data,
                                gameTitle = games.find { it.id == gameId }?.name ?: "Error 123",
                                openDrawer = openDrawer,
                                pointHistory = (pointHistoryUiState as PointHistoryUiState.PointHistorySuccess).data,
                                addPointHistoryEntry = { viewModel.addPointHistoryEntry(it) },
                                savePhasesOfPlayer = { viewModel.savePlayerPhases(it) },
                                modifier = modifier
                            )
                        }

                        is PointHistoryUiState.PointHistoryLoading -> {

                        }

                        is PointHistoryUiState.PointHistoryError -> {
                            Text(text = "Error Point History")
                        }

                    }

                }

                is PlayerUiState.PlayersLoading -> {
                }

                is PlayerUiState.PlayersError -> {
                    Text(text = "Error Players")
                }
            }
        }

        is GamesUiState.GamesLoading -> {

        }

        is GamesUiState.GamesError -> {
            Text(text = "Error Games")
        }
    }

}

@Composable
internal fun GameScreen(
    players: List<Player>,
    gameTitle: String,
    openDrawer: () -> Unit,
    pointHistory: List<PointHistory>,
    addPointHistoryEntry: (PointHistory) -> Unit,
    savePhasesOfPlayer: (Player) -> Unit,
    modifier: Modifier = Modifier
) {

    DefaultScaffold(title = gameTitle, openDrawer = openDrawer, content = { scaffoldModifier ->
        LazyVerticalGrid(
            modifier = scaffoldModifier
                .then(modifier)
                .padding(top = 8.dp, bottom = 4.dp),
            columns = GridCells.Adaptive(370.dp)
        ) {
            items(players) { player ->
                OnePlayerView(
                    player = player,
                    modifier = Modifier
                        .padding(8.dp)
                        .padding(bottom = 8.dp)
                        .fillMaxWidth(),
                    listOfPoints = pointHistory.filter { it.playerID == player.id },
                    addPointHistoryEntry = addPointHistoryEntry,
                    savePhasesOfPlayer = savePhasesOfPlayer
                )
            }

            // add some padding at the bottom
            item {
                Spacer(
                    modifier = Modifier.height(
                        LocalConfiguration.current.screenHeightDp.dp.div(
                            4
                        )
                    )
                )
            }
        }
    })
}

// Previews
@Preview(showBackground = true, widthDp = 500)
@Preview(showBackground = true, widthDp = 800, heightDp = 350)
@Preview(device = Devices.TABLET)
@Composable
fun GameScreenPreview() {
    GameScreen(players = listOf(
        Player(0L, "Player1", 0L, "1, 2, 3, 4, 5, 6, 7, 8, 9, 10"),
        Player(0L, "Player2", 0L, "1, 2, 3, 4, 5, 6, 7, 8, 9, 10"),
        Player(0L, "Player3", 0L, "1, 2, 3, 4, 5, 6, 7, 8, 9, 10")
    ), openDrawer = {}, gameTitle = "Game 1", pointHistory = listOf(
        PointHistory(70L, 0L), PointHistory(180L, 0L)
    ), addPointHistoryEntry = {}, savePhasesOfPlayer = {})
}