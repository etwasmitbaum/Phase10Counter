package com.tjEnterprises.phase10Counter.ui.selectGame

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.tjEnterprises.phase10Counter.data.local.GameModel
import com.tjEnterprises.phase10Counter.data.local.PlayerModel
import com.tjEnterprises.phase10Counter.ui.GamesUiState
import com.tjEnterprises.phase10Counter.ui.component.DefaultScaffold
import com.tjEnterprises.phase10Counter.ui.component.GamePreviewComponent

@Composable
fun SelectGame(
    modifier: Modifier = Modifier,
    viewModel: SelectGameViewModel = hiltViewModel(),
    openDrawer: () -> Unit,
    navigateToGame: (String) -> Unit
) {
    val gamesUiState by viewModel.gamesUiState.collectAsState()


    when (gamesUiState) {
        is GamesUiState.GamesSuccess -> {
            SelectGame(
                games = (gamesUiState as GamesUiState.GamesSuccess).data,
                openDrawer = openDrawer,
                navigateToGame = navigateToGame,
                resetGame = { viewModel.resetGameWithData(it) },
                deleteGame = { viewModel.deleteGameWithData(it) },
                modifier = modifier
            )
        }

        else -> {}
    }

}

@Composable
internal fun SelectGame(
    modifier: Modifier = Modifier,
    games: List<GameModel>,
    openDrawer: () -> Unit,
    navigateToGame: (String) -> Unit,
    resetGame: (Long) -> Unit,
    deleteGame: (Long) -> Unit
) {
    // TODO Expand card to show all players with their points
    // TODO Delete Games
    // TODO reset games -> delete the point history and phases
    // TODO make gridLayout
    // TODO add "Reset Game" Button
    DefaultScaffold(title = "Select Game", openDrawer = openDrawer) { scaffoldModifier ->
        LazyColumn(modifier = scaffoldModifier
            .then(modifier)
            .fillMaxWidth(),
            horizontalAlignment = Alignment.End,
            content = {
                items(games) { game ->
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

@Preview(showBackground = true)
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
            1L, "Game 3", 0L, 0L,
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