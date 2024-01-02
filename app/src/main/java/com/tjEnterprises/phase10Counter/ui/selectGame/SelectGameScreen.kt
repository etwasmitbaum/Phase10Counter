package com.tjEnterprises.phase10Counter.ui.selectGame

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tjEnterprises.phase10Counter.R
import com.tjEnterprises.phase10Counter.data.local.models.GameModel
import com.tjEnterprises.phase10Counter.data.local.models.PlayerModel
import com.tjEnterprises.phase10Counter.ui.SelectGameUiState
import com.tjEnterprises.phase10Counter.ui.component.DefaultScaffoldNavigation
import com.tjEnterprises.phase10Counter.ui.updateChecker.UpdateCheckerComponent

@Composable
fun SelectGame(
    modifier: Modifier = Modifier,
    viewModel: SelectGameViewModel = hiltViewModel(),
    openDrawer: () -> Unit,
    navigateToGame: (String) -> Unit
) {
    val gamesUiState by viewModel.selectGameUiState.collectAsState()

    when (gamesUiState) {
        is SelectGameUiState.SelectGameSuccess -> {
            SelectGame(
                games = (gamesUiState as SelectGameUiState.SelectGameSuccess).games,
                openDrawer = openDrawer,
                navigateToGame = navigateToGame,
                resetGame = { viewModel.resetGameWithData(it) },
                deleteGame = { viewModel.deleteGameWithData(it) },
                updateChecker = { UpdateCheckerComponent(it) },
                modifier = modifier
            )
        }

        is SelectGameUiState.SelectGameLoading -> {
            DefaultScaffoldNavigation(
                title = stringResource(id = R.string.selectGameLoading), openDrawer = openDrawer
            ) {}
        }

        is SelectGameUiState.SelectGameError -> {
            DefaultScaffoldNavigation(
                title = stringResource(id = R.string.selectGameError), openDrawer = openDrawer
            ) {}
        }
    }
}

@Composable
internal fun SelectGame(
    modifier: Modifier = Modifier,
    title: String = stringResource(id = R.string.title_selectGame),
    games: List<GameModel>,
    openDrawer: () -> Unit,
    navigateToGame: (String) -> Unit,
    resetGame: (Long) -> Unit,
    deleteGame: (Long) -> Unit,
    updateChecker: @Composable (Modifier) -> Unit = {}
) {
    // TODO make gridLayout maybe?
    DefaultScaffoldNavigation(title = title, openDrawer = openDrawer) { scaffoldModifier ->
        Column(modifier = scaffoldModifier) {
            updateChecker(Modifier.padding(top = 8.dp))

            LazyVerticalGrid(modifier = Modifier
                .then(modifier)
                .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalArrangement = Arrangement.Center,
                columns = GridCells.Adaptive(330.dp),
                content = {
                    items(games, key = { game -> game.gameId }) { game ->
                        GamePreviewComponent(
                            game = game,
                            navigateToGame = navigateToGame,
                            deleteGame = deleteGame,
                            resetGame = resetGame,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }

                    if (games.isEmpty()) {
                        item {
                            Text(
                                text = stringResource(id = R.string.noGamesAddedYet),
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                fontSize = 18.sp
                            )
                        }

                    }
                })
        }
    }
}


@Preview(showBackground = true, locale = "DE")
@Preview(showBackground = true, widthDp = 800, heightDp = 300)
@Composable
fun SelectGamePreview() {
    SelectGame(games = listOf(
        GameModel(
            1L, "Game 1", 0L, 0L,
            listOf(
                PlayerModel(
                    1L,
                    1L,
                    "Player1",
                    listOf(256L),
                    256L,
                    listOf(true, true, true, true, true, true, true, true, true, true)
                ), PlayerModel(
                    1L,
                    1L,
                    "Player1",
                    listOf(256L),
                    256L,
                    listOf(true, true, true, true, true, true, true, true, true, true)
                ), PlayerModel(
                    1L,
                    1L,
                    "Player1",
                    listOf(256L),
                    256L,
                    listOf(true, true, true, true, true, true, true, true, true, true)
                )
            ),
        ), GameModel(
            2L, "Game 2", 0L, 0L,
            listOf(
                PlayerModel(
                    1L,
                    2L,
                    "Player1",
                    listOf(256L),
                    256L,
                    listOf(true, true, true, true, true, true, true, true, true, true)
                ), PlayerModel(
                    1L,
                    2L,
                    "Player1",
                    listOf(256L),
                    256L,
                    listOf(true, true, true, true, true, true, true, true, true, true)
                ), PlayerModel(
                    1L,
                    2L,
                    "Player1",
                    listOf(256L),
                    256L,
                    listOf(true, true, true, true, true, true, true, true, true, true)
                )
            ),
        ), GameModel(
            3L, "Game 3", 0L, 0L,
            listOf(
                PlayerModel(
                    1L,
                    3L,
                    "Player1",
                    listOf(256L),
                    256L,
                    listOf(true, true, true, true, true, true, true, true, true, true)
                ), PlayerModel(
                    1L,
                    3L,
                    "Player1",
                    listOf(256L),
                    256L,
                    listOf(true, true, true, true, true, true, true, true, true, true)
                ), PlayerModel(
                    1L,
                    3L,
                    "Player1",
                    listOf(256L),
                    256L,
                    listOf(true, true, true, true, true, true, true, true, true, true)
                )
            ),
        )
    ), openDrawer = {}, navigateToGame = {}, resetGame = {}, deleteGame = {})
}

@Preview(showBackground = true, widthDp = 800, heightDp = 300)
@Composable
fun SelectGamePreviewWithOneGame() {
    SelectGame(games = listOf(
        GameModel(
            1L, "Game 1", 0L, 0L,
            listOf(
                PlayerModel(
                    1L,
                    1L,
                    "Player1",
                    listOf(256L),
                    256L,
                    listOf(true, true, true, true, true, true, true, true, true, true)
                ), PlayerModel(
                    1L,
                    1L,
                    "Player1",
                    listOf(256L),
                    256L,
                    listOf(true, true, true, true, true, true, true, true, true, true)
                ), PlayerModel(
                    1L,
                    1L,
                    "Player1",
                    listOf(256L),
                    256L,
                    listOf(true, true, true, true, true, true, true, true, true, true)
                )
            ),
        )
    ), openDrawer = {}, navigateToGame = {}, resetGame = {}, deleteGame = {})
}

@Preview(showBackground = true, widthDp = 400, heightDp = 500)
@Composable
fun SelectGamePreviewWithNoGames() {
    SelectGame(games = listOf(),
        openDrawer = {},
        navigateToGame = {},
        resetGame = {},
        deleteGame = {})
}