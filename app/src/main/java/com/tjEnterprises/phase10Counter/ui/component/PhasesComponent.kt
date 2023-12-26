package com.tjEnterprises.phase10Counter.ui.component

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
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.tjEnterprises.phase10Counter.R
import com.tjEnterprises.phase10Counter.data.local.database.Player

@Composable
// well i know this is no mvvm here, but for this ONE fixed function it seems a bit overkill
// to create a view-model
fun PhasesComponent(
    modifier: Modifier = Modifier,
    player: Player,
    closeDialog: () -> Unit,
    savePhasesOfPlayer: (Player) -> Unit
) {
    val checkedList = remember { mutableStateListOf<Boolean>() }

    // extract all open phases
    val openPhasesOfPlayer = "\\d+".toRegex().findAll(player.name).map { it.value.toInt() }

    for (i in 0..9) {
        // if phase found from string, do not check box
        if (openPhasesOfPlayer.find { it == (i + 1) } != null) {
            checkedList.add(i, false)
        } else {
            checkedList.add(i, true)
        }
    }

    AlertDialog(modifier = modifier
        .fillMaxWidth(0.85f)
        .wrapContentSize(),
        properties = DialogProperties(
            usePlatformDefaultWidth = false, dismissOnBackPress = true, dismissOnClickOutside = true
        ),
        onDismissRequest = {
            dismiss(
                checkedList = checkedList,
                player = player,
                savePhasesOfPlayer = savePhasesOfPlayer,
                closeDialog = closeDialog
            )

        },
        confirmButton = {
            TextButton(onClick = {
                dismiss(
                    checkedList = checkedList,
                    player = player,
                    savePhasesOfPlayer = savePhasesOfPlayer,
                    closeDialog = closeDialog
                )
            }) {
                Text(text = "Confirm")
            }
        },
        dismissButton = {},
        title = { Text(text = "Phases") },
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
                                        checkedList[j] = !checkedList[j]
                                    }) {
                                    Checkbox(checked = checkedList[j],
                                        onCheckedChange = { checkedList[j] = it })
                                    Text(text = stringArrayResource(id = R.array.phases)[j])
                                }
                            }
                        }
                    }
                })
        })

}

fun dismiss(
    checkedList: List<Boolean>,
    player: Player,
    savePhasesOfPlayer: (Player) -> Unit,
    closeDialog: () -> Unit
) {
    // for every NON checked box, add the phase to the string
    var phasesString = ""
    checkedList.forEachIndexed { idx, checked ->
        if (!checked) {
            val phase = idx + 1;
            phasesString = phasesString.plus("$phase, ")
        }
    }
    // remove last ", " and assign + save new phases
    phasesString = phasesString.dropLast(2)
    //player.phases = phasesString
    savePhasesOfPlayer(player)

    closeDialog()
}


@Preview(device = Devices.PHONE)
@Preview(device = Devices.TABLET)
@Preview(device = Devices.NEXUS_5)
@Composable
fun PhasesComponentPreview() {
    PhasesComponent(player = Player(0L, "Player 1", 0L), closeDialog = {}, savePhasesOfPlayer = {})
}