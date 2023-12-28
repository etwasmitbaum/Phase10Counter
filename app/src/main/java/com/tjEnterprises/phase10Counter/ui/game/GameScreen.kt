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

import androidx.activity.compose.BackHandler
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
import com.tjEnterprises.phase10Counter.data.local.PlayerModel
import com.tjEnterprises.phase10Counter.data.local.database.PointHistory
import com.tjEnterprises.phase10Counter.ui.GameUiState
import com.tjEnterprises.phase10Counter.ui.PlayersUiState
import com.tjEnterprises.phase10Counter.ui.component.DefaultScaffold
import com.tjEnterprises.phase10Counter.ui.component.OnePlayerView
import javax.inject.Singleton

@Composable
@Singleton
fun GameScreen(
    modifier: Modifier = Modifier,
    gameId: Long,
    viewModel: GameViewModel = hiltViewModel(),
    navigateToGameSelect: () -> Unit,
    openDrawer: () -> Unit,
) {
    val gamesUiState by viewModel.gameUiState.collectAsState()
    viewModel.setGameFromId(gameId)

    BackHandler {
        navigateToGameSelect()
    }

    when (gamesUiState) {
        is GameUiState.GameSuccess -> {
            val games = (gamesUiState as GameUiState.GameSuccess).data
            GameScreen(
                players = games.players,
                gameTitle = games.name,
                openDrawer = openDrawer,
                addPointHistoryEntry = { point, pointGameId, playerId -> viewModel.addPointHistoryEntry(point = point, gameId = pointGameId, playerId = playerId) },
                savePhasesOfPlayer = { playerId, gameIdPlayer, openPhases -> viewModel.savePlayerPhases(playerId, gameIdPlayer, openPhases) },
                modifier = modifier
            )
        }

        is GameUiState.GameLoading -> {
            GameScreen(
                players = emptyList(),
                gameTitle = "Loading Games",
                openDrawer = openDrawer,
                addPointHistoryEntry = {_, _, _ ->},
                savePhasesOfPlayer = {_, _, _ ->}
            )
        }

        is GameUiState.GameError -> {
            GameScreen(
                players = emptyList(),
                gameTitle = "Error Games",
                openDrawer = openDrawer,
                addPointHistoryEntry = {_, _, _ ->},
                savePhasesOfPlayer = {_, _, _ ->}
            )
        }
    }

}

@Composable
internal fun GameScreen(
    players: List<PlayerModel>,
    gameTitle: String,
    openDrawer: () -> Unit,
    addPointHistoryEntry: (Long, Long, Long) -> Unit,
    savePhasesOfPlayer: (Long, Long, List<Boolean>) -> Unit,
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
                    listOfPoints = listOf(player.pointSum),
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
        PlayerModel(1L, 1L, "Player1", listOf(256L), 256L, listOf(true, true, true, true, true, true, true, true, true, true)),
        PlayerModel(1L, 1L, "Player2", listOf(256L), 256L, listOf(true, true, true, true, true, true, true, true, true, true)),
        PlayerModel(1L, 1L, "Player3", listOf(256L), 256L, listOf(true, true, true, true, true, true, true, true, true, true))
    ), openDrawer = {}, gameTitle = "Game 1", addPointHistoryEntry = {_, _, _ ->}, savePhasesOfPlayer = {_, _, _ ->})
}