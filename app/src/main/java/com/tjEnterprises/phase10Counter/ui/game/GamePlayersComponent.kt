package com.tjEnterprises.phase10Counter.ui.game

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import com.tjEnterprises.phase10Counter.R
import com.tjEnterprises.phase10Counter.data.local.models.GameType
import com.tjEnterprises.phase10Counter.data.local.models.PlayerModel
import com.tjEnterprises.phase10Counter.data.local.models.PointHistoryItem

@Composable
// Max width of 400dp expected
fun OnePlayerView(
    modifier: Modifier = Modifier,
    player: PlayerModel,
    gameType: GameType.Type,
    savePhasesOfPlayer: (Long, Long, List<Boolean>) -> Unit,
    addPointHistoryEntry: (Long, Long, Long) -> Unit,
    deletePointHistoryItem: (PointHistoryItem) -> Unit,
    updatePointHistoryItem: (PointHistoryItem) -> Unit,
    scrollToNextPosition: () -> Unit
) {
    var text by rememberSaveable() {
        mutableStateOf("")
    }

    val openDialog = remember {
        mutableStateOf(false)
    }

    when {
        openDialog.value -> {
            PhasesComponent(
                player = player,
                gameType = gameType,
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
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
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
                    .widthIn(1.dp, 128.dp)
                    .onFocusChanged {
                        if (!it.isFocused && text.isNotBlank() && text.isDigitsOnly()) {
                            addPointHistoryEntry(text.toLong(), player.gameId, player.playerId)
                            text = ""
                        }
                        if (it.isFocused) {
                            scrollToNextPosition()
                        }
                    })
            PointHistoryDropDown(
                pointHistory = player.pointHistory,
                sumOfPoints = player.pointSum,
                deletePointHistoryItem = deletePointHistoryItem,
                updatePointHistoryItem = updatePointHistoryItem,
                modifier = Modifier.padding(start = 12.dp)
            )
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

// Previews
@Preview(showBackground = true)
@Preview(showBackground = true, widthDp = 350)
@Preview(showBackground = true, widthDp = 350, locale = "de")
@Preview(showBackground = true, widthDp = 350, locale = "pl")
@Composable
fun OnePlayerPreview() {
    OnePlayerView(player = PlayerModel(
        1L,
        1L,
        "Player1",
        listOf(PointHistoryItem(256L, 1L), PointHistoryItem(254L, 2L)),
        2560L,
        listOf(true, true, true, true, true, true, true, true, true, true)
    ),
        gameType = GameType.defaultGameType,
        addPointHistoryEntry = { _, _, _ -> },
        savePhasesOfPlayer = { _, _, _ -> },
        scrollToNextPosition = {},
        deletePointHistoryItem = {},
        updatePointHistoryItem = {})
}

@Preview(showBackground = true)
@Composable
fun OnePlayerPreview2() {
    OnePlayerView(player = PlayerModel(
        1L,
        1L,
        "Player1",
        listOf(PointHistoryItem(256L, 1L), PointHistoryItem(254L, 2L)),
        2560L,
        listOf(false, false, false, false, false, false, false, false, false, false)
    ),
        gameType = GameType.defaultGameType,
        addPointHistoryEntry = { _, _, _ -> },
        savePhasesOfPlayer = { _, _, _ -> },
        scrollToNextPosition = {},
        deletePointHistoryItem = {},
        updatePointHistoryItem = {})
}