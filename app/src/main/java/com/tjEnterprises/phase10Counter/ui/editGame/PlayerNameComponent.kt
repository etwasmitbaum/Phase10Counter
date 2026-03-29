package com.tjEnterprises.phase10Counter.ui.editGame

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.DialogProperties
import com.tjEnterprises.phase10Counter.R
import com.tjEnterprises.phase10Counter.data.local.database.Player
import com.tjEnterprises.phase10Counter.data.local.models.PlayerModel
import com.tjEnterprises.phase10Counter.data.local.models.PointHistoryItem

@Composable
fun PlayerNameComponent(
    modifier: Modifier = Modifier,
    player: PlayerModel,
    closeDialog: () -> Unit,
    updatePlayer: (Player) -> Unit
) {
    var playerName = rememberSaveable { mutableStateOf(player.name) }

    AlertDialog(
        modifier = modifier
            .fillMaxWidth(0.85f)
            .wrapContentSize(),
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        ),
        onDismissRequest = {
            dismiss(
                newPlayerName = playerName.value,
                player = player,
                updatePlayer = updatePlayer,
                closeDialog = closeDialog
            )
        },
        confirmButton = {
            TextButton(onClick = {
                dismiss(
                    newPlayerName = playerName.value,
                    player = player,
                    updatePlayer = updatePlayer,
                    closeDialog = closeDialog
                )
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
        title = { Text(text = stringResource(id = R.string.editPlayerName)) },
        text = {
            TextField(
                value = playerName.value,
                onValueChange = { playerName.value = it },
                singleLine = true
            )
        }
    )
}

internal fun dismiss(
    newPlayerName: String,
    player: PlayerModel,
    updatePlayer: (Player) -> Unit,
    closeDialog: () -> Unit
) {
    if (player.name != newPlayerName) {
        updatePlayer(Player(
            gameID = player.gameId,
            name = newPlayerName,
            showMarker = player.showMarker,
            playerId = player.playerId
        ))
    }

    closeDialog()
}

@Preview(device = Devices.NEXUS_5)
@Composable
fun PlayerNameComponentPreview() {
    PlayerNameComponent(
        player = PlayerModel(
            1L,
            1L,
            "Player1",
            listOf(PointHistoryItem(256L, 1L)),
            256L,
            listOf(true, true, true, true, true, true, true, true, true, true),
            showMarker = false
        ),
        closeDialog = {},
        updatePlayer = {}
    )
}