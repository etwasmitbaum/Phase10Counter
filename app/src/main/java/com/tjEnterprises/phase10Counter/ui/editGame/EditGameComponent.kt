package com.tjEnterprises.phase10Counter.ui.editGame

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.tjEnterprises.phase10Counter.R
import com.tjEnterprises.phase10Counter.data.local.models.GameModel
import com.tjEnterprises.phase10Counter.data.local.models.GameType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditGameComponent(
    modifier: Modifier = Modifier,
    game: GameModel,
    updateGameName: (Long, String) -> Unit,
    updateGameType: (Long, GameType.Type) -> Unit
) {
    val openEditNameDialog = remember {
        mutableStateOf(false)
    }

    val openGameTypeDropdownMenu = remember { mutableStateOf(false) }

    if (openEditNameDialog.value) {
        EditGameNameDialog(
            gameId = game.gameId,
            gameName = game.name,
            updateGameName = updateGameName,
            closeDialog = { openEditNameDialog.value = false }
        )
    }

    OutlinedCard(
        modifier = modifier
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically) {
                Text(modifier = Modifier.padding(start = 5.dp), text = stringResource(id = R.string.gameName) + ": " + game.name)
                IconButton(onClick = { openEditNameDialog.value = true }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null
                    )
                }
            }

            ExposedDropdownMenuBox(
                expanded = openGameTypeDropdownMenu.value,
                onExpandedChange = { openGameTypeDropdownMenu.value = it },
                modifier = Modifier
                    .padding(5.dp)
                    .then(modifier)
                    .widthIn(1.dp, 150.dp)
            ) {
                OutlinedTextField(
                    value = stringResource(id = game.gameType.resourceId),
                    onValueChange = {

                    },
                    readOnly = true,
                    label = { Text(stringResource(id = R.string.gameType)) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = openGameTypeDropdownMenu.value)
                    },
                    modifier = Modifier
                        .menuAnchor(
                            ExposedDropdownMenuAnchorType.SecondaryEditable, enabled = true
                        )
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = openGameTypeDropdownMenu.value,
                    onDismissRequest = { openGameTypeDropdownMenu.value = false }
                ) {
                    GameType.availableGameTypes.forEach { item: GameType.Type ->
                        DropdownMenuItem(text = { Text(text = stringResource(id = item.resourceId)) },
                            onClick = {
                                openGameTypeDropdownMenu.value = false
                                updateGameType(game.gameId, item)
                            })
                    }
                }
            }
        }
    }
}

@Composable
fun EditGameNameDialog(
    modifier: Modifier = Modifier,
    gameId: Long,
    gameName: String,
    updateGameName: (Long, String) -> Unit,
    closeDialog: () -> Unit
) {
    val newGameName = remember { mutableStateOf(gameName) }

    AlertDialog(
        modifier = modifier
            .fillMaxWidth(0.85f)
            .wrapContentSize(),
        properties = DialogProperties(
            usePlatformDefaultWidth = false, dismissOnBackPress = true, dismissOnClickOutside = true
        ),
        onDismissRequest = {},
        confirmButton = {
            TextButton(onClick = {
                updateGameName(gameId, newGameName.value)
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
        title = {},
        text = {
            TextField(
                value = newGameName.value,
                onValueChange = { newGameName.value = it },
                singleLine = true
            )
        }
    )
}

@Composable
@Preview
fun EditGameComponentPreview() {
    EditGameComponent(
        game = GameModel(
            gameId = 1,
            name = "TestGame",
            gameType = GameType.Standard,
            created = 1,
            modified = 1,
            players = listOf()
        ),
        updateGameName = {_,_ ->},
        updateGameType = {_,_ ->}
    )
}