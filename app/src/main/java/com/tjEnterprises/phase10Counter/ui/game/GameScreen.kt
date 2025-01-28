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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tjEnterprises.phase10Counter.R
import com.tjEnterprises.phase10Counter.data.local.models.GameType
import com.tjEnterprises.phase10Counter.data.local.models.PlayerModel
import com.tjEnterprises.phase10Counter.data.local.models.PointHistoryItem
import com.tjEnterprises.phase10Counter.ui.GameUiState
import com.tjEnterprises.phase10Counter.ui.component.DefaultScaffoldNavigation
import kotlinx.coroutines.launch
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
    val dontChangeUiWideScreen by viewModel.dontChangeUiWideScreen.collectAsState()
    val gamesUiState by viewModel.gameUiState.collectAsState()
    viewModel.setGameFromId(gameId)

    BackHandler {
        navigateToGameSelect()
    }

    when (gamesUiState) {
        is GameUiState.GameSuccess -> {
            val games = (gamesUiState as GameUiState.GameSuccess).game
            GameScreen(
                players = games.players,
                gameTitle = games.name,
                gameType = games.gameType,
                openDrawer = openDrawer,
                addPointHistoryEntry = { point, pointGameId, playerId ->
                    viewModel.addPointHistoryEntry(
                        point = point, gameId = pointGameId, playerId = playerId
                    )
                },
                savePhasesOfPlayer = { playerId, gameIdPlayer, openPhases ->
                    viewModel.savePlayerPhases(
                        playerId, gameIdPlayer, openPhases
                    )
                },
                dontChangeUiWideScreen = dontChangeUiWideScreen,
                deletePointHistoryItem = { viewModel.deletePointHistoryEntry(it) },
                updatePointHistoryItem = { viewModel.updatePointHistoryEntry(it) },
                modifier = modifier
            )
        }

        is GameUiState.GameLoading -> {
            DefaultScaffoldNavigation(
                title = stringResource(id = R.string.gameScreenLoading),
                openDrawer = openDrawer,
            ) {}
        }

        is GameUiState.GameError -> {
            DefaultScaffoldNavigation(
                title = stringResource(id = R.string.gameScreenError),
                openDrawer = openDrawer,
            ) {}
        }
    }

}

@Composable
internal fun GameScreen(
    players: List<PlayerModel>,
    gameTitle: String,
    gameType: GameType.Type,
    dontChangeUiWideScreen: Boolean,
    openDrawer: () -> Unit,
    addPointHistoryEntry: (Long, Long, Long) -> Unit,
    savePhasesOfPlayer: (Long, Long, List<Boolean>) -> Unit,
    deletePointHistoryItem: (PointHistoryItem) -> Unit,
    updatePointHistoryItem: (PointHistoryItem) -> Unit,
    modifier: Modifier = Modifier
) {

    val gameTypeString = stringResource(id = gameType.resourceId)

    DefaultScaffoldNavigation(title = "$gameTitle($gameTypeString)",
        openDrawer = openDrawer,
        dontChangeUiWideScreen = dontChangeUiWideScreen,
        content = { scaffoldModifier ->

            val gridState = rememberLazyGridState()
            val coroutineScope = rememberCoroutineScope()

            LazyVerticalGrid(
                modifier = scaffoldModifier
                    .then(modifier)
                    .padding(bottom = 4.dp)
                    .fillMaxSize(),
                state = gridState,
                horizontalArrangement = Arrangement.Center,
                verticalArrangement = Arrangement.Center,
                columns = if (dontChangeUiWideScreen) GridCells.Fixed(1) else GridCells.Adaptive(400.dp)
            ) {
                itemsIndexed(items = players) { idx, player ->
                    OnePlayerView(player = player,
                        gameType = gameType,
                        modifier = Modifier
                            .padding(8.dp)
                            .padding(bottom = 8.dp)
                            .fillMaxWidth(),
                        addPointHistoryEntry = addPointHistoryEntry,
                        savePhasesOfPlayer = savePhasesOfPlayer,
                        deletePointHistoryItem = deletePointHistoryItem,
                        updatePointHistoryItem = updatePointHistoryItem,
                        scrollToNextPosition = {
                            coroutineScope.launch {
                                gridState.animateScrollToItem(if (idx > 1) idx - 1 else 0)
                            }
                        })
                }

                // add some padding at the bottom
                item {
                    Spacer(
                        modifier = Modifier
                            .height(
                                LocalConfiguration.current.screenHeightDp.dp.div(
                                    3
                                )
                            )
                            .width(1.dp)
                    )
                }
            }
        })
}

// Previews
@Preview(showBackground = true, widthDp = 500)
@Preview(showBackground = true, widthDp = 900, heightDp = 350)
@Preview(showBackground = true, widthDp = 900, heightDp = 350, fontScale = 2f)
@Preview(showBackground = true, widthDp = 400, fontScale = 2f)
@Preview(device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
fun GameScreenPreview() {
    GameScreen(
        players = listOf(
            PlayerModel(
                1L,
                1L,
                "Player1",
                listOf(PointHistoryItem(256L, 1)),
                256L,
                listOf(true, true, true, true, true, true, true, true, true, true)
            ), PlayerModel(
                2L,
                1L,
                "Player2",
                listOf(PointHistoryItem(256L, 1)),
                256L,
                listOf(true, true, true, true, true, true, true, true, true, true)
            ), PlayerModel(
                3L,
                1L,
                "Player3",
                listOf(PointHistoryItem(256L, 1)),
                256L,
                listOf(true, true, true, true, true, true, true, true, true, true)
            )
        ),
        openDrawer = {},
        gameTitle = "Game 1",
        gameType = GameType.defaultGameType,
        addPointHistoryEntry = { _, _, _ -> },
        savePhasesOfPlayer = { _, _, _ -> },
        dontChangeUiWideScreen = false, deletePointHistoryItem = {},
        updatePointHistoryItem = {},
    )
}