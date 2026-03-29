package com.tjEnterprises.phase10Counter.ui.editGame

import android.app.AlertDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.tjEnterprises.phase10Counter.R
import com.tjEnterprises.phase10Counter.data.local.database.Game
import com.tjEnterprises.phase10Counter.data.local.database.Player
import com.tjEnterprises.phase10Counter.data.local.models.GameModel
import com.tjEnterprises.phase10Counter.data.local.models.GameType
import com.tjEnterprises.phase10Counter.data.local.models.PlayerModel
import com.tjEnterprises.phase10Counter.data.local.models.PointHistoryItem
import com.tjEnterprises.phase10Counter.ui.EditGameUiState
import com.tjEnterprises.phase10Counter.ui.component.DefaultScaffoldNavigation
import kotlinx.coroutines.launch

@Composable
fun EditGameScreen(
    modifier: Modifier = Modifier,
    gameId: Long,
    viewModel: EditGameViewModel = hiltViewModel(),
    openDrawer: () -> Unit
){
    val dontChangeUiWideScreen by viewModel.dontChangeUiWideScreen.collectAsState()
    val editGameUiState by viewModel.editGameUiState.collectAsState()
    viewModel.setGameFromId(gameId)

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
                modifier = modifier
            )
        }

        is EditGameUiState.EditGameLoading -> {
            DefaultScaffoldNavigation(
                title = stringResource(id = R.string.gameScreenLoading),
                openDrawer = openDrawer
            ) { }
        }

        is EditGameUiState.EditGameError -> {
            DefaultScaffoldNavigation(
                title = stringResource(id = R.string.gameScreenError),
                openDrawer = openDrawer
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
    modifier: Modifier = Modifier
) {
    DefaultScaffoldNavigation(
        title = "",
        openDrawer = openDrawer,
        dontChangeUiWideScreen = dontChangeUiWideScreen,
        content = { scaffoldModifier ->

            val openAddPlayerDialog = remember {
                mutableStateOf(false)
            }

            if (openAddPlayerDialog.value) {
                AddPlayerDialog(
                    closeDialog = { openAddPlayerDialog.value = false },
                    insertNewPlayer = { playerName ->
                        insertPlayer(playerName, game.gameId)
                    }
                )
            }

            val gridState = rememberLazyGridState()
            val coroutineScope = rememberCoroutineScope()

            Column(modifier = scaffoldModifier
                .then(modifier)
                .padding(bottom = 4.dp)
                .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                EditGameComponent(
                    game = game,
                    updateGameName = updateGameName,
                    updateGameType = updateGameType
                )

                LazyVerticalGrid(
                    state = gridState,
                    horizontalArrangement = Arrangement.Center,
                    verticalArrangement = Arrangement.Center,
                    columns = if (dontChangeUiWideScreen) GridCells.Fixed(1) else GridCells.Adaptive(400.dp)
                ) {
                    itemsIndexed(items = players) { idx, player ->
                        EditPlayerComponent(
                            player = player,
                            gameType = game.gameType,
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
                            },
                            updatePlayer = updatePlayer,
                            deletePlayer = deletePlayer
                        )
                    }
                }

                IconButton(onClick = {
                    openAddPlayerDialog.value = true
                }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                }
            }
        })
}

@Composable
fun AddPlayerDialog(
    closeDialog: () -> Unit,
    insertNewPlayer: (playerName: String) -> Unit
) {
    val newPlayerName = remember {
        mutableStateOf("")
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
                insertNewPlayer(newPlayerName.value)
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
                singleLine = true
            )
        }
    )
}

@Preview
@Composable
fun EditGameScreenPreview() {
    EditGameScreen(
        players = listOf(),
        game = GameModel(
            gameId = 0,
            name = "TestGame",
            gameType = GameType.Standard,
            created = 0L,
            modified = 0L,
            players = listOf()
        ),
        dontChangeUiWideScreen = false,
        openDrawer = {},
        addPointHistoryEntry = {_,_,_ ->},
        savePhasesOfPlayer = {_,_,_ ->},
        deletePointHistoryItem = {},
        updatePointHistoryItem = {},
        insertPlayer = {_,_->},
        updatePlayer = {},
        deletePlayer = {},
        updateGameName = {_,_->},
        updateGameType = {_,_ ->}
    )
}