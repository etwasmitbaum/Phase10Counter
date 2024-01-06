package com.tjEnterprises.phase10Counter.ui.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import com.tjEnterprises.phase10Counter.R
import com.tjEnterprises.phase10Counter.data.local.models.PlayerModel

@Composable
// Max width of 400dp expected
fun OnePlayerView(
    modifier: Modifier = Modifier,
    player: PlayerModel,
    listOfPoints: List<Long>,
    savePhasesOfPlayer: (Long, Long, List<Boolean>) -> Unit,
    addPointHistoryEntry: (Long, Long, Long) -> Unit,
    scrollToNextPosition: () -> Unit
) {
    var text by remember {
        mutableStateOf("")
    }

    val openDialog = remember {
        mutableStateOf(false)
    }

    when {
        openDialog.value -> {
            PhasesComponent(
                player = player,
                closeDialog = { openDialog.value = false },
                savePhasesOfPlayer = savePhasesOfPlayer
            )
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier
    ) {
        Text(
            text = player.name,
            fontSize = 16.sp,
            modifier = Modifier
                .padding(bottom = 8.dp)
                .widthIn(min = 0.dp, max = 400.dp)
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = { openDialog.value = true }, modifier = Modifier.padding(end = 8.dp)) {
                Text(text = stringResource(id = R.string.phases))
            }
            TextField(value = text,
                onValueChange = { text = it },
                label = { Text(stringResource(id = R.string.points)) },
                maxLines = 1,
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
                ),
                modifier = Modifier
                    .widthIn(1.dp, 150.dp)
                    .onFocusChanged {
                        if (!it.isFocused && text != "" && !(text.contains('.') || text.contains(','))) {
                            addPointHistoryEntry(text.toLong(), player.gameId, player.playerId)
                            text = ""
                        }
                        if (it.isFocused) {
                            scrollToNextPosition()
                        }
                    })
            PointHistoryDropDown(listOfPoints, player.pointSum)
        }

        var phasesString = ""
        player.phasesOpen.forEachIndexed { idx, open ->
            if (open) {
                val phase = idx + 1
                phasesString = phasesString.plus("$phase, ")
            }
        }
        // remove last ", " and assign + save new phases
        phasesString = phasesString.dropLast(2)
        if (phasesString == "") {
            phasesString = stringResource(id = R.string.none)
        }

        Text(
            stringResource(id = R.string.openPhases) + " $phasesString",
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PointHistoryDropDown(
    pointHistory: List<Long>, sumOfPoints: Long, modifier: Modifier = Modifier
) {

    // state of the menu
    var expanded by remember {
        mutableStateOf(false)
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .widthIn(1.dp, Dp.Infinity)
            .wrapContentSize()
            .padding(start = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
            modifier = modifier
        ) {
            Text(
                text = sumOfPoints.toString()
            )   // total points
            IconButton(onClick = {
                expanded = true
            }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = stringResource(id = R.string.showPointHistory)
                )
            }
        }

        // drop down menu
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            },
            modifier = Modifier
                .widthIn(min = 1.dp, max = 128.dp)
                .wrapContentSize(),
            properties = PopupProperties(usePlatformDefaultWidth = false)
        ) {
            val lastElement = pointHistory.lastIndex
            // adding items
            pointHistory.forEachIndexed { idx, item ->
                Text(
                    text = item.toString(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    textAlign = TextAlign.Center
                )
                // don't place a divider at the bottom
                if (idx != lastElement) {
                    Divider()
                }
            }
        }
    }
}


// Previews
@Preview(showBackground = true)
@Composable
fun OnePlayerPreview() {
    OnePlayerView(player = PlayerModel(
        1L,
        1L,
        "Player1",
        listOf(256L, 254L),
        256L,
        listOf(true, true, true, true, true, true, true, true, true, true)
    ), listOfPoints = listOf(
        70L, 100L
    ), addPointHistoryEntry = { _, _, _ -> }, savePhasesOfPlayer = { _, _, _ -> }, scrollToNextPosition = {})
}

@Preview(showBackground = true)
@Composable
fun OnePlayerPreview2() {
    OnePlayerView(player = PlayerModel(
        1L,
        1L,
        "Player1",
        listOf(256L),
        256L,
        listOf(false, false, false, false, false, false, false, false, false, false)
    ), listOfPoints = listOf(
        70L, 100L
    ), addPointHistoryEntry = { _, _, _ -> }, savePhasesOfPlayer = { _, _, _ -> }, scrollToNextPosition = {})
}

@Preview(showBackground = true)
@Composable
fun PointHistoryDropDownPreview() {
    PointHistoryDropDown(listOf(70L, 720L), 790L)
}