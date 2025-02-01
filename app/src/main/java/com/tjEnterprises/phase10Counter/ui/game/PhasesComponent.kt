package com.tjEnterprises.phase10Counter.ui.game

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.tjEnterprises.phase10Counter.R
import com.tjEnterprises.phase10Counter.data.local.models.GameType
import com.tjEnterprises.phase10Counter.data.local.models.PlayerModel
import com.tjEnterprises.phase10Counter.data.local.models.PointHistoryItem

@Composable
// well i know this is no mvvm here, but for this ONE fixed function it seems a bit overkill
// to create a view-model
fun PhasesComponent(
    modifier: Modifier = Modifier,
    player: PlayerModel,
    gameType: GameType.Type,
    closeDialog: () -> Unit,
    savePhasesOfPlayer: (Long, Long, List<Boolean>) -> Unit
) {
    val openPhases = remember { mutableStateListOf<Boolean>() }

    player.phasesOpen.forEach {
        openPhases.add(it)
    }

    AlertDialog(modifier = modifier
        .fillMaxWidth(0.85f)
        .wrapContentSize(),
        properties = DialogProperties(
            usePlatformDefaultWidth = false, dismissOnBackPress = true, dismissOnClickOutside = true
        ),
        onDismissRequest = {
            dismiss(
                openPhases = openPhases,
                player = player,
                savePhasesOfPlayer = savePhasesOfPlayer,
                closeDialog = closeDialog
            )

        },
        confirmButton = {
            TextButton(onClick = {
                dismiss(
                    openPhases = openPhases,
                    player = player,
                    savePhasesOfPlayer = savePhasesOfPlayer,
                    closeDialog = closeDialog
                )
            }) {
                Text(text = stringResource(id = R.string.confirm))
            }
        },
        dismissButton = {},
        title = { Text(text = stringResource(id = R.string.phasesOf) + " " + player.name) },
        text = {
            LazyVerticalGrid(modifier = Modifier.wrapContentSize(),
                columns = GridCells.Adaptive(200.dp),
                content = {
                /*
                * This LazyVerticalGrid contains only two items, the first 5 phases and the last 5.
                * This way the LazyVerticalGrid will split the phases in either one consecutive block
                * or two block next to each other.
                * Each block count up from top to bottom.
                */
                    items(2) { partIdx ->
                        Column(verticalArrangement = Arrangement.SpaceEvenly) {
                            for (i in 0..<5) {
                                val j = i + 5 * (partIdx)
                                Row(verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable {
                                        openPhases[j] = !openPhases[j]
                                    }) {
                                    Checkbox(checked = !openPhases[j],
                                        onCheckedChange = { openPhases[j] = !it })

                                    val phases = when (gameType) {
                                        GameType.Standard -> R.array.phases
                                        GameType.Flip -> R.array.phasesFlip
                                        GameType.Masters -> R.array.phasesMasters
                                        else -> R.array.phases // I choose not to display an error. It will default to the standard phases. The error will be displayed as the game type "invalid"
                                    }

                                    Text(text = stringArrayResource(id = phases )[j])
                                }
                            }
                        }
                    }
                })
        })

}

fun dismiss(
    openPhases: List<Boolean>,
    player: PlayerModel,
    savePhasesOfPlayer: (Long, Long, List<Boolean>) -> Unit,
    closeDialog: () -> Unit
) {

    savePhasesOfPlayer(player.playerId, player.gameId, openPhases)
    closeDialog()
}


@Preview(device = "spec:width=411dp,height=891dp")
@Preview(device = "spec:width=1280dp,height=800dp,dpi=240")
@Preview(device = Devices.NEXUS_5)
@Composable
fun PhasesComponentPreview() {
    PhasesComponent(
        player = PlayerModel(
            1L,
            1L,
            "Player1",
            listOf(PointHistoryItem(256L, 1L)),
            256L,
            listOf(true, true, true, true, true, true, true, true, true, true),
            showMarker = false
        ),
        gameType = GameType.defaultGameType,
        closeDialog = {},
        savePhasesOfPlayer = { _, _, _ ->}
    )
}