package com.tjEnterprises.phase10Counter.ui.editGame

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
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
    val playerName = rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(
            TextFieldValue(
                text = player.name,
                selection = TextRange(player.name.length)
            )
        )
    }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    AlertDialog(
        modifier = modifier
            .fillMaxWidth(0.85f)
            .wrapContentSize(),
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        ),
        onDismissRequest = { closeDialog() },
        confirmButton = {
            TextButton(onClick = {
                if (player.name != playerName.value.text && playerName.value.text.isNotBlank()) {
                    updatePlayer(
                        Player(
                            gameID = player.gameId,
                            name = playerName.value.text,
                            showMarker = player.showMarker,
                            playerId = player.playerId,
                            orderIndex = player.orderIndex
                        )
                    )
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
        title = { Text(text = stringResource(id = R.string.playerName)) },
        text = {
            TextField(
                value = playerName.value,
                onValueChange = { playerName.value = it },
                singleLine = true,
                modifier = Modifier.focusRequester(focusRequester)
            )
        }
    )
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
            showMarker = false,
            orderIndex = 0
        ),
        closeDialog = {},
        updatePlayer = {}
    )
}