package com.tjEnterprises.phase10Counter.ui.selectGame

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tjEnterprises.phase10Counter.R
import com.tjEnterprises.phase10Counter.data.local.models.GameModel
import com.tjEnterprises.phase10Counter.data.local.models.PlayerModel
import com.tjEnterprises.phase10Counter.data.local.models.PointHistoryItem
import com.tjEnterprises.phase10Counter.ui.navigation.NavigationDestination

@Composable
fun GamePreviewComponent(
    game: GameModel,
    navigateToGame: (String) -> Unit,
    deleteGame: (Long) -> Unit,
    resetGame: (Long) -> Unit,
    modifier: Modifier = Modifier,
    expand: Boolean = false,
) {
    var detailsExpanded by rememberSaveable { mutableStateOf(expand) }
    val openResetGameDialog = remember {
        mutableStateOf(false)
    }
    val openDeleteGameDialog = remember {
        mutableStateOf(false)
    }

    // Open Dialog when needed
    when {
        openDeleteGameDialog.value -> {
            DeleteGameDialog(showDialog = openDeleteGameDialog,
                deleteGame = { deleteGame(game.gameId) })
        }

        openResetGameDialog.value -> {
            ResetGameDialog(showDialog = openResetGameDialog,
                resetGame = { resetGame(game.gameId) })
        }
    }


    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.scrim)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .align(Alignment.CenterHorizontally),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(style = TextStyle(
                textAlign = TextAlign.Center,
                fontSize = 30.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
                text = game.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp)
                    .clip(shape = RoundedCornerShape(30)) // Clip for rounded corner ripple#
                    .clickable { navigateToGame(NavigationDestination.GAMESCREEN + "/" + game.gameId) })

            // Print player names in expanded version or small
            if (detailsExpanded) {
                Column {
                    game.players.forEach { player ->
                        Text(
                            text = player.name + ": " + player.pointSum.toString() + " " + stringResource(
                                id = R.string.points
                            )
                        )
                    }
                }

            } else {
                var playersText = ""
                game.players.forEach { player ->
                    playersText = playersText.plus(player.name + ", ")
                }
                playersText = playersText.dropLast(2)
                Text(text = playersText)
            }


            Text(text = if (detailsExpanded) stringResource(id = R.string.hideDetails) else stringResource(
                id = R.string.showDetails
            ),
                style = TextStyle(
                    textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 4.dp)
                    .clip(shape = RoundedCornerShape(50)) // Clip for rounded corner ripple
                    .clickable { detailsExpanded = !detailsExpanded })


            // When expanded show buttons, to delete, reset or start a game
            if (detailsExpanded) {
                Row(
                    modifier = Modifier.fillMaxWidth().offset(y = (-4).dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    IconButton(onClick = { openDeleteGameDialog.value = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete, contentDescription = stringResource(
                                id = R.string.delete
                            )
                        )
                    }

                    IconButton(onClick = { openResetGameDialog.value = true }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = stringResource(
                                id = R.string.reset
                            )
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(onClick = { navigateToGame(NavigationDestination.GAMESCREEN + "/" + game.gameId) }) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = stringResource(
                                id = R.string.start
                            )
                        )
                    }
                }
            }

        }
    }


}

@Preview(showBackground = true, widthDp = 500)
@Composable
fun GamePreviewComponentPreview(expand: Boolean = false) {
    GamePreviewComponent(game = GameModel(
        1L, "Game 1", 0L, 0L,
        listOf(
            PlayerModel(
                1L,
                1L,
                "Player12345",
                listOf(PointHistoryItem(256L, 1L)),
                256L,
                listOf(true, true, true, true, true, true, true, true, true, true)
            ), PlayerModel(
                2L,
                1L,
                "P2",
                listOf(PointHistoryItem(256L, 1L)),
                256L,
                listOf(true, true, true, true, true, true, true, true, true, true)
            ), PlayerModel(
                3L,
                1L,
                "Player3",
                listOf(PointHistoryItem(256L, 1L)),
                256L,
                listOf(true, true, true, true, true, true, true, true, true, true)
            )
        ),
    ), navigateToGame = {}, deleteGame = {}, resetGame = {}, expand = expand
    )
}

@Preview(showBackground = true, heightDp = 300)
@Composable
fun GamePreviewComponentPreviewWithVeryLongNames(expand: Boolean = false) {
    GamePreviewComponent(game = GameModel(
        1L, "VeryLongGameNameINeedToTest", 0L, 0L,
        listOf(
            PlayerModel(
                1L,
                1L,
                "VeryLongPlayerINeedToTestIDon'tKnowHowLongThisShouldBe",
                listOf(PointHistoryItem(256L, 1L)),
                256L,
                listOf(true, true, true, true, true, true, true, true, true, true)
            ), PlayerModel(
                2L,
                1L,
                "VeryLongPlayerINeedToTestIDon'tKnowHowLongThisShouldBe2",
                listOf(PointHistoryItem(256L, 1L)),
                256L,
                listOf(true, true, true, true, true, true, true, true, true, true)
            )
        ),
    ), navigateToGame = {}, deleteGame = {}, resetGame = {}, expand = expand
    )
}

@Preview(showBackground = true, locale = "DE", fontScale = 2f)
@Composable
fun NormalViewExpandedBigAndLongTextSize() {
    GamePreviewComponentPreviewWithVeryLongNames(expand = false)
}

@Preview(showBackground = true, heightDp = 300, locale = "DE")
@Composable
fun NormalViewExpanded() {
    GamePreviewComponentPreview(expand = true)
}

@Preview(showBackground = true, heightDp = 300, locale = "DE", fontScale = 2f)
@Composable
fun NormalViewExpandedBigTextSize() {
    GamePreviewComponentPreview(expand = true)
}

@Preview(showBackground = true, heightDp = 300, widthDp = 200)
@Composable
fun SmallScreenExpanded() {
    GamePreviewComponentPreview(expand = true)
}

@Preview(showBackground = true, heightDp = 300, locale = "DE")
@Composable
fun ExpandedLongNames() {
    GamePreviewComponentPreviewWithVeryLongNames(expand = true)
}

@Preview(showBackground = true, heightDp = 500, widthDp = 200)
@Composable
fun ExpandedLongNamesSmallScreen() {
    GamePreviewComponentPreviewWithVeryLongNames(expand = true)
}

@Preview(showBackground = true, heightDp = 300, widthDp = 200, locale = "DE")
@Composable
fun LongNamesSmallScreen() {
    GamePreviewComponentPreviewWithVeryLongNames(expand = false)
}