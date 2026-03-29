package com.tjEnterprises.phase10Counter.ui.editGame

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
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
import com.tjEnterprises.phase10Counter.data.local.database.Player
import com.tjEnterprises.phase10Counter.data.local.models.GameType
import com.tjEnterprises.phase10Counter.data.local.models.PlayerModel
import com.tjEnterprises.phase10Counter.data.local.models.PointHistoryItem
import com.tjEnterprises.phase10Counter.ui.game.PhasesComponent
import com.tjEnterprises.phase10Counter.ui.game.PointHistoryDropDown

@Composable
fun EditPlayerComponent(
    modifier: Modifier = Modifier,
    player: PlayerModel,
    gameType: GameType.Type,
    addPointHistoryEntry: (point: Long, gameId: Long, playerId: Long) -> Unit,
    savePhasesOfPlayer: (gameId: Long, playerId: Long, openPhases: List<Boolean>) -> Unit,
    deletePointHistoryItem: (PointHistoryItem) -> Unit,
    updatePointHistoryItem: (PointHistoryItem) -> Unit,
    updatePlayer: (Player) -> Unit,
    deletePlayer: (playerId: Long) -> Unit,
    scrollToNextPosition: () -> Unit
    ) {
    var pointsText by rememberSaveable {
        mutableStateOf("")
    }

    val openEditPhaseDialog = remember {
        mutableStateOf(false)
    }

    val openEditPlayerNameDialog = remember {
        mutableStateOf(false)
    }

    when {
        openEditPhaseDialog.value -> {
            PhasesComponent(
                player = player,
                gameType = gameType,
                closeDialog = { openEditPhaseDialog.value = false },
                savePhasesOfPlayer = savePhasesOfPlayer
            )
        }
    }

    when {
        openEditPlayerNameDialog.value -> {
            PlayerNameComponent(
                player = player,
                closeDialog = { openEditPlayerNameDialog.value = false },
                updatePlayer = updatePlayer
            )
        }
    }

    OutlinedCard(modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(bottom = 8.dp)
            ) {
                Text(
                    text = player.name,
                    fontSize = 16.sp,
                    modifier = Modifier.widthIn(min = 0.dp, max = 360.dp)
                )

                IconButton(onClick = { openEditPlayerNameDialog.value = true }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(onClick = { openEditPhaseDialog.value = true }, modifier = Modifier.padding(end = 8.dp)) {
                    Text(text = stringResource(id = R.string.phases))
                }
                TextField(value = pointsText,
                    onValueChange = { pointsText = it },
                    label = { Text(stringResource(id = R.string.points)) },
                    maxLines = 1,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
                    ),
                    modifier = Modifier
                        .widthIn(1.dp, 128.dp)
                        .onFocusChanged {
                            if (!it.isFocused && pointsText.isNotBlank() && pointsText.isDigitsOnly()) {
                                addPointHistoryEntry(pointsText.toLong(), player.gameId, player.playerId)
                                pointsText = ""
                            }
                            if (it.isFocused) {
                                scrollToNextPosition()
                            }
                        }
                )
                PointHistoryDropDown(
                    pointHistory = player.pointHistory,
                    sumOfPoints = player.pointSum,
                    deletePointHistoryItem = deletePointHistoryItem,
                    updatePointHistoryItem = updatePointHistoryItem,
                    modifier = Modifier.padding(start = 12.dp)
                )
                IconButton(onClick = { deletePlayer(player.playerId) }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null
                    )
                }
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
}

@Preview(showBackground = true, widthDp = 350)
@Composable
fun EditPlayerComponentPreview() {
    EditPlayerComponent(
        player = PlayerModel(
            1L,
            1L,
            "Player1",
            listOf(PointHistoryItem(256L, 1L), PointHistoryItem(254L, 2L)),
            2560L,
            listOf(true, true, true, true, true, true, true, true, true, true),
            showMarker = true
        ),
        gameType = GameType.defaultGameType,
        addPointHistoryEntry = { _, _, _ -> },
        savePhasesOfPlayer = { _, _, _ -> },
        scrollToNextPosition = {},
        deletePointHistoryItem = {},
        updatePointHistoryItem = {},
        updatePlayer = {},
        deletePlayer = {}
    )
}