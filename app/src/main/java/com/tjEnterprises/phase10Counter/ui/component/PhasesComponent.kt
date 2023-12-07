package com.tjEnterprises.phase10Counter.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.tjEnterprises.phase10Counter.data.local.database.Player

@Composable
// well i know this is no mvvm here, but for this ONE fixed function it seems a bit overkill
// to create a view-model
fun PhasesComponent(
    player: Player,
    closeDialog: () -> Unit,
    savePhasesOfPlayer: (Player) -> Unit,
    modifier: Modifier = Modifier
) {

    val checkedList = remember { mutableStateListOf<Boolean>() }

    // extract all open phases
    val openPhasesOfPlayer = "\\d+".toRegex().findAll(player.phases).map { it.value.toInt() }

    for (i in 0..9) {
        // if phase found from string, do not check box
        if (openPhasesOfPlayer.find { it == (i + 1) } != null) {
            checkedList.add(i, false)
        } else {
            checkedList.add(i, true)
        }
    }
    // TODO set alert dialog width

    AlertDialog(modifier = modifier, properties = DialogProperties(usePlatformDefaultWidth = false), onDismissRequest = {
        dismiss(
            checkedList = checkedList,
            player = player,
            savePhasesOfPlayer = savePhasesOfPlayer,
            closeDialog = closeDialog
        )

    }, confirmButton = {
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
    }, dismissButton = {}, title = { Text(text = "Phases") }, text = {
        // TODO fiddle with adaptive parameter so text fits nicely
        LazyHorizontalGrid(rows = GridCells.Adaptive(50.dp),content = {
            itemsIndexed(checkedList) { idx, isChecked ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isChecked, onCheckedChange = { checkedList[idx] = it })
                    Text(text = "Phase $idx")   // TODO print actual phases
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
        println(checked)
        if (!checked) {
            var phase = idx + 1;
            phasesString = phasesString.plus("$phase, ")
            println(phasesString)
        }
    }
    // remove last ", " and assign + save new phases
    phasesString = phasesString.dropLast(2)
    player.phases = phasesString
    savePhasesOfPlayer(player)

    closeDialog()
}


@Preview(showBackground = true)
@Composable
fun PhasesComponentPreview() {
    PhasesComponent(Player(0L, "Player 1", 0L), closeDialog = {}, savePhasesOfPlayer = {})
}