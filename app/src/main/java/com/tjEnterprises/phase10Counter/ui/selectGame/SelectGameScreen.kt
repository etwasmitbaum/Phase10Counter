package com.tjEnterprises.phase10Counter.ui.selectGame

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tjEnterprises.phase10Counter.R
import com.tjEnterprises.phase10Counter.data.local.models.GameModel
import com.tjEnterprises.phase10Counter.data.local.models.PlayerModel
import com.tjEnterprises.phase10Counter.ui.SelectGameUiState
import com.tjEnterprises.phase10Counter.ui.component.DefaultScaffold
import com.tjEnterprises.phase10Counter.ui.component.GamePreviewComponent
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
            DefaultScaffold(title = stringResource(id = R.string.selectGameLoading), openDrawer = openDrawer) {}
        }
        is SelectGameUiState.SelectGameError -> {
            DefaultScaffold(title = stringResource(id = R.string.selectGameError), openDrawer = openDrawer) {}
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
    DefaultScaffold(title = title, openDrawer = openDrawer) { scaffoldModifier ->
        Column {
            updateChecker(Modifier)
            LazyVerticalGrid(modifier = scaffoldModifier
                .then(modifier)
                .fillMaxWidth().padding(bottom = 4.dp),
                columns = GridCells.Adaptive(330.dp),
                content = {

                    items(games, key = {game -> game.gameId}) { game ->
                        GamePreviewComponent(
                            game = game,
                            navigateToGame = navigateToGame,
                            deleteGame = deleteGame,
                            resetGame = resetGame
                        )

                    }
                })
        }
    }
}

@Preview(showBackground = true, locale = "DE")
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
fun SelectGamePreviewLandscape(){
    SelectGamePreview()
}