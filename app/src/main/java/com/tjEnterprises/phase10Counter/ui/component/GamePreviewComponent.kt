package com.tjEnterprises.phase10Counter.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tjEnterprises.phase10Counter.data.local.GameModel
import com.tjEnterprises.phase10Counter.data.local.PlayerModel
import com.tjEnterprises.phase10Counter.ui.navigation.NavigationDestination
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment

@Composable
fun GamePreviewComponent(
    game: GameModel,
    navigateToGame: (String) -> Unit,
    deleteGame: (Long) -> Unit,
    resetGame: (Long) -> Unit,
    modifier: Modifier = Modifier,
    expand: Boolean = false
) {
    var bExpanded by remember { mutableStateOf(expand) }

    Card(
        modifier = modifier.fillMaxWidth(),
        border = BorderStroke(4.dp, Color.Blue)
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            Text(text = game.name,
                fontSize = 30.sp,
                modifier = Modifier
                    .clickable {
                        navigateToGame(NavigationDestination.GAMESCREEN + "/" + game.gameId)
                    }
                    .fillMaxWidth()
            )
            game.players.forEachIndexed { idx, player ->
                // show max 2 players, when not expanded
                if (bExpanded or (idx < 2)) {
                    Row(modifier = Modifier
                        .clickable {
                            if (!bExpanded) bExpanded = true
                        }
                        .fillMaxWidth()
                    ) {
                        Text(text = player.name)
                        if (bExpanded) {
                            Text(text = ": " + player.pointSum.toString() + " Points")
                        }
                    }
                }
            }

            var sExpandText = "Expand"
            if (bExpanded) {
                sExpandText = "Collapse"
            }

            Column(
                modifier = Modifier.padding(6.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                            append(sExpandText)
                        }
                    },
                    textAlign = TextAlign.Left,
                    modifier = Modifier
                        .clickable { bExpanded = !bExpanded }
                        .fillMaxWidth()
                )
            }
        }

        if (bExpanded) {
            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = { deleteGame(game.gameId) },
                    modifier = Modifier.padding(bottom = 16.dp, end = 4.dp)
                ) {
                    Text(text = "Delete")
                }
                Button(
                    onClick = { resetGame(game.gameId) },
                    modifier = Modifier.padding(bottom = 16.dp, end = 4.dp)
                ) {
                    Text(text = "ResetGame")
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
                "Player1",
                listOf(256L),
                256L,
                listOf(true, true, true, true, true, true, true, true, true, true)
            ),
            PlayerModel(
                1L,
                1L,
                "Player1",
                listOf(256L),
                256L,
                listOf(true, true, true, true, true, true, true, true, true, true)
            ),
            PlayerModel(
                1L,
                1L,
                "Player1",
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
fun GamePreviewComponentPreviewExpanded() {
    GamePreviewComponentPreview(expand = true)
}
