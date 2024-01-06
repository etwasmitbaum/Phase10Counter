package com.tjEnterprises.phase10Counter.ui.selectGame

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
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

    // Somehow this box is needed for "widthIn" to work
    BoxWithConstraints {
        Card(
            modifier = modifier, border = BorderStroke(1.dp, MaterialTheme.colorScheme.scrim)
        ) {
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.CenterHorizontally),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ClickableText(modifier = Modifier.fillMaxWidth(), style = TextStyle(textAlign = TextAlign.Center), text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontSize = 30.sp, color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    ) {
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
                        .padding(top = 4.dp, start = 4.dp, bottom = 4.dp)
                        .fillMaxWidth(),
                    style = TextStyle(
                        textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.primary
                    )
                )

                if (bExpanded) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        IconButton(onClick = { deleteGame(game.gameId) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = stringResource(
                                    id = R.string.delete
                                )
                            )
                        }

                        IconButton(onClick = { resetGame(game.gameId) }) {
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
            ), PlayerModel(
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