package com.tjEnterprises.phase10Counter.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tjEnterprises.phase10Counter.R
import com.tjEnterprises.phase10Counter.data.local.models.GameModel
import com.tjEnterprises.phase10Counter.data.local.models.PlayerModel
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
    var bExpanded by remember { mutableStateOf(expand) }

    Card(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 4.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.scrim)
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.CenterHorizontally),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ClickableText(text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontSize = 30.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)) {
                    append(
                        game.name
                    )
                }
            }, onClick = {
                navigateToGame(NavigationDestination.GAMESCREEN + "/" + game.gameId)
            })

            if (bExpanded) {
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

            val sExpandText =
                if (bExpanded) stringResource(id = R.string.hideDetails) else stringResource(id = R.string.showDetails)

            ClickableText(
                text = buildAnnotatedString {
                    append(sExpandText)
                },
                onClick = { bExpanded = !bExpanded },
                modifier = Modifier
                    .padding(top = 4.dp, start = 4.dp)
                    .fillMaxWidth(),
                style = TextStyle(
                    textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.primary
                )
            )

            if (bExpanded) {
                val btnModifier = Modifier.padding(bottom = 4.dp, end = 4.dp)
                // TODO Make dropdown menu for delete, reset
                // TODO Possible ass "rename game" to dropdown
                Column {
                    Row (horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = { navigateToGame(NavigationDestination.GAMESCREEN + "/" + game.gameId) }
                        ) {
                            Text(text = stringResource(id = R.string.start))
                        }
                    }

                    Row(horizontalArrangement = Arrangement.Start, modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = { deleteGame(game.gameId) }, modifier = btnModifier
                        ) {
                            Text(text = stringResource(id = R.string.delete))
                        }
                        Button(
                            onClick = { resetGame(game.gameId) }, modifier = btnModifier
                        ) {
                            Text(text = stringResource(id = R.string.reset))
                        }
                    }
                }

            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun GamePreviewComponentPreview(expand: Boolean = false) {
    GamePreviewComponent(game = GameModel(
        1L, "Game 1", 0L, 0L,
        listOf(
            PlayerModel(
                1L,
                1L,
                "Player12345",
                listOf(256L),
                256L,
                listOf(true, true, true, true, true, true, true, true, true, true)
            ), PlayerModel(
                2L,
                1L,
                "P2",
                listOf(256L),
                256L,
                listOf(true, true, true, true, true, true, true, true, true, true)
            ), PlayerModel(
                3L,
                1L,
                "Player3",
                listOf(256L),
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
                listOf(256L),
                256L,
                listOf(true, true, true, true, true, true, true, true, true, true)
            ),
            PlayerModel(
                2L,
                1L,
                "VeryLongPlayerINeedToTestIDon'tKnowHowLongThisShouldBe2",
                listOf(256L),
                256L,
                listOf(true, true, true, true, true, true, true, true, true, true)
            )
        ),
    ), navigateToGame = {}, deleteGame = {}, resetGame = {}, expand = expand
    )
}

@Preview(showBackground = true, heightDp = 300, locale = "DE")
@Composable
fun NormalViewExpanded() {
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

@Preview(showBackground = true, heightDp = 300, widthDp = 200)
@Composable
fun ExpandedLongNamesSmallScreen() {
    GamePreviewComponentPreviewWithVeryLongNames(expand = true)
}

@Preview(showBackground = true, heightDp = 300, widthDp = 200, locale = "DE")
@Composable
fun LongNamesSmallScreen() {
    GamePreviewComponentPreviewWithVeryLongNames(expand = false)
}