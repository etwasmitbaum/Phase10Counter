package com.tjEnterprises.phase10Counter.ui.editGame

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.tjEnterprises.phase10Counter.R
import com.tjEnterprises.phase10Counter.data.local.database.Player
import com.tjEnterprises.phase10Counter.data.local.models.GameModel
import com.tjEnterprises.phase10Counter.data.local.models.GameType
import com.tjEnterprises.phase10Counter.data.local.models.PlayerModel
import com.tjEnterprises.phase10Counter.data.local.models.PointHistoryItem
import com.tjEnterprises.phase10Counter.ui.EditGameUiState
import com.tjEnterprises.phase10Counter.ui.component.DefaultScaffoldNavigation
import kotlinx.coroutines.launch
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyGridState

@Composable
fun EditGameScreen(
    modifier: Modifier = Modifier,
    gameId: Long,
    viewModel: EditGameViewModel = hiltViewModel(),
    openDrawer: () -> Unit
) {
    val dontChangeUiWideScreen by viewModel.dontChangeUiWideScreen.collectAsState()
    val editGameUiState by viewModel.editGameUiState.collectAsState()

    LaunchedEffect(gameId) {
        viewModel.setGameFromId(gameId)
    }

    when (editGameUiState) {
        is EditGameUiState.EditGameSuccess -> {
            val game = (editGameUiState as EditGameUiState.EditGameSuccess).game
            EditGameScreen(
                players = game.players,
                game = game,
                dontChangeUiWideScreen = dontChangeUiWideScreen,
                openDrawer = openDrawer,
                addPointHistoryEntry = { point: Long, pointGameId: Long, playerId: Long ->
                    viewModel.addPointHistoryEntry(
                        point = point, gameId = pointGameId, playerId = playerId
                    )
                },
                savePhasesOfPlayer = { playerId: Long, gameIdPlayer: Long, openPhases: List<Boolean> ->
                    viewModel.savePlayerPhases(
                        playerId, gameIdPlayer, openPhases
                    )
                },
                deletePointHistoryItem = { item: PointHistoryItem ->
                    viewModel.deletePointHistoryEntry(item)
                },
                updatePointHistoryItem = { item: PointHistoryItem ->
                    viewModel.updatePointHistoryEntry(item)
                },
                insertPlayer = { playerName: String, gameId: Long ->
                    viewModel.insertPlayer(playerName, gameId)
                },
                updatePlayer = { player: Player ->
                    viewModel.updatePlayer(player)
                },
                deletePlayer = { playerId: Long ->
                    viewModel.deletePlayer(playerId)
                },
                updateGameName = { gameId: Long, gameName: String ->
                    viewModel.updateGameName(gameId, gameName)
                },
                updateGameType = { gameId: Long, gameType: GameType.Type ->
                    viewModel.updateGameType(gameId, gameType)
                },
                changePlayerOrderFromTo = { gameId: Long, from: Int, to: Int ->
                    viewModel.changePlayerOrderFromTo(gameId, from, to)
                },
                modifier = modifier
            )
        }

        is EditGameUiState.EditGameLoading -> {
            DefaultScaffoldNavigation(
                title = stringResource(id = R.string.gameScreenLoading), openDrawer = openDrawer
            ) { }
        }

        is EditGameUiState.EditGameError -> {
            DefaultScaffoldNavigation(
                title = stringResource(id = R.string.gameScreenError), openDrawer = openDrawer
            ) { }
        }
    }
}

@Composable
internal fun EditGameScreen(
    players: List<PlayerModel>,
    game: GameModel,
    dontChangeUiWideScreen: Boolean,
    openDrawer: () -> Unit,
    addPointHistoryEntry: (point: Long, pointGameId: Long, playerId: Long) -> Unit,
    savePhasesOfPlayer: (playerId: Long, gameId: Long, openPhases: List<Boolean>) -> Unit,
    deletePointHistoryItem: (PointHistoryItem) -> Unit,
    updatePointHistoryItem: (PointHistoryItem) -> Unit,
    insertPlayer: (playerName: String, gameId: Long) -> Unit,
    updatePlayer: (Player) -> Unit,
    deletePlayer: (Long) -> Unit,
    updateGameName: (gameId: Long, gameName: String) -> Unit,
    updateGameType: (gameId: Long, gameType: GameType.Type) -> Unit,
    changePlayerOrderFromTo: (gameId: Long, from: Int, to: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    DefaultScaffoldNavigation(
        title = stringResource(R.string.editing_game),
        openDrawer = openDrawer,
        dontChangeUiWideScreen = dontChangeUiWideScreen,
        content = { scaffoldModifier ->

            val openAddPlayerDialog = remember {
                mutableStateOf(false)
            }
            val showPlayerAddedToast = remember {
                mutableStateOf(false)
            }

            when {
                openAddPlayerDialog.value -> {
                    AddPlayerDialog(
                        closeDialog = { openAddPlayerDialog.value = false },
                        insertNewPlayer = { playerName ->
                            insertPlayer(playerName, game.gameId)
                        },
                        showPlayerAddedToast = { showPlayerAddedToast.value = true })
                }

                showPlayerAddedToast.value -> {
                    Toast.makeText(
                        LocalContext.current,
                        stringResource(id = R.string.playerAdded),
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }

            val gridState = rememberLazyGridState()
            val coroutineScope = rememberCoroutineScope()
            val reorderableLazyGridState = rememberReorderableLazyGridState(gridState) { from, to ->
                // subtract 1 since in scope of the full LazyVerticalGrid the players start at
                // index 1. Index 0 is the EditGameComponent
                changePlayerOrderFromTo(game.gameId, from.index - 2, to.index - 2)
            }

            LazyVerticalGrid(
                modifier = scaffoldModifier.then(modifier),
                state = gridState,
                horizontalArrangement = Arrangement.Center,
                verticalArrangement = Arrangement.Center,
                contentPadding = PaddingValues(bottom = 200.dp),
                columns = if (dontChangeUiWideScreen) GridCells.Fixed(1) else GridCells.Adaptive(
                    400.dp
                )
            ) {

                item {
                    EditGameComponent(
                        modifier = Modifier
                            .padding(8.dp)
                            .padding(bottom = 16.dp),
                        game = game,
                        updateGameName = updateGameName,
                        updateGameType = updateGameType
                    )
                }

                item {
                    OutlinedIconButton(
                        modifier = Modifier.wrapContentSize(), onClick = {
                            openAddPlayerDialog.value = true
                        }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(id = R.string.addPlayer),
                        )
                    }
                }

                items(items = players, key = { it.playerId }) { player ->
                    val idx = players.indexOf(player)
                    ReorderableItem(reorderableLazyGridState, key = player.playerId) { isDragging ->
                        // Keep for future, currently i don't think the elevation looks good
                        // val elevation by animateDpAsState(if (isDragging) 4.dp else 0.dp)

                        EditPlayerComponent(
                            player = player,
                            gameType = game.gameType,
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth(),
                            dragHandleModifier = Modifier.draggableHandle(),
                            addPointHistoryEntry = addPointHistoryEntry,
                            savePhasesOfPlayer = savePhasesOfPlayer,
                            deletePointHistoryItem = deletePointHistoryItem,
                            updatePointHistoryItem = updatePointHistoryItem,
                            scrollToNextPosition = {
                                coroutineScope.launch {
                                    gridState.animateScrollToItem(if (idx > 1) idx - 1 else 0)
                                }
                            },
                            updatePlayer = updatePlayer,
                            deletePlayer = deletePlayer
                        )

                    }
                }

            }

        })
}

@Composable
fun AddPlayerDialog(
    closeDialog: () -> Unit,
    insertNewPlayer: (playerName: String) -> Unit,
    showPlayerAddedToast: () -> Unit
) {
    val newPlayerName = remember {
        mutableStateOf("")
    }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    AlertDialog(
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .wrapContentSize(),
        properties = DialogProperties(
            usePlatformDefaultWidth = false, dismissOnBackPress = true, dismissOnClickOutside = true
        ),
        onDismissRequest = {
            closeDialog()
        },
        confirmButton = {
            TextButton(onClick = {
                if (newPlayerName.value.isNotBlank()) {
                    insertNewPlayer(newPlayerName.value)
                    showPlayerAddedToast()
                }

                closeDialog()
            }) {
                Text(text = stringResource(id = R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = {
                closeDialog()
            }) {
                Text(text = stringResource(id = R.string.cancel))
            }
        },
        title = {
            Text(text = stringResource(id = R.string.playerName))
        },
        text = {
            TextField(
                value = newPlayerName.value,
                onValueChange = { newPlayerName.value = it },
                singleLine = true,
                modifier = Modifier.focusRequester(focusRequester)
            )
        })
}

@Preview(showBackground = true, locale = "EN")
@Preview(showBackground = true, locale = "DE")
@Preview(showBackground = true, locale = "PL")
@Preview(showBackground = true, widthDp = 900, heightDp = 400)
@Preview(showBackground = true, heightDp = 1000)
@Composable
fun EditGameScreenPreview() {
    val players = listOf(
        PlayerModel(
            1L,
            1L,
            "Player1",
            listOf(PointHistoryItem(256L, 1)),
            256L,
            listOf(true, true, true, true, true, true, true, true, true, true),
            showMarker = false,
            orderIndex = 0
        ), PlayerModel(
            2L,
            1L,
            "Player2 has a very very ver very very very very very very long name",
            listOf(PointHistoryItem(256L, 1)),
            256L,
            listOf(true, true, true, true, true, true, true, true, true, true),
            showMarker = true,
            orderIndex = 0
        ), PlayerModel(
            3L,
            1L,
            "Player3",
            listOf(PointHistoryItem(256L, 1)),
            256L,
            listOf(true, true, true, true, true, true, true, true, true, true),
            showMarker = false,
            orderIndex = 0
        ), PlayerModel(
            4L,
            1L,
            "Player4 has a very very ver very very very very very very long name",
            listOf(PointHistoryItem(256L, 1)),
            256L,
            listOf(true, true, true, true, true, true, true, true, true, true),
            showMarker = false,
            orderIndex = 0
        )
    )
    EditGameScreen(
        players = players,
        game = GameModel(
            gameId = 0,
            name = "TestGame",
            gameType = GameType.Standard,
            created = 0L,
            modified = 0L,
            players = players
        ),
        dontChangeUiWideScreen = false,
        openDrawer = {},
        addPointHistoryEntry = { _, _, _ -> },
        savePhasesOfPlayer = { _, _, _ -> },
        deletePointHistoryItem = {},
        updatePointHistoryItem = {},
        insertPlayer = { _, _ -> },
        updatePlayer = {},
        deletePlayer = {},
        updateGameName = { _, _ -> },
        updateGameType = { _, _ -> },
        changePlayerOrderFromTo = { _, _, _ -> })
}