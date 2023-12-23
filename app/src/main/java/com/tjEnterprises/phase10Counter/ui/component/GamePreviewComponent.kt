package com.tjEnterprises.phase10Counter.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
import com.tjEnterprises.phase10Counter.data.local.database.Game
import com.tjEnterprises.phase10Counter.data.local.database.Player
import com.tjEnterprises.phase10Counter.ui.navigation.NavigationDestination

@Composable
fun GamePreviewComponent(
    game: Game,
    players: List<Player>,
    navigateToGame: (String) -> Unit,
    deleteGame: (Game) -> Unit,
    resetGame: (Game) -> Unit,
    modifier: Modifier = Modifier
) {
    var bExpanded by remember { mutableStateOf(false) }

        Card(
            modifier = modifier.fillMaxWidth(),
            border = BorderStroke(4.dp, Color.Blue)
        ) {
            Column(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
                Text( text = game.name,
                    fontSize = 30.sp,
                    modifier = Modifier.clickable {
                        navigateToGame(NavigationDestination.GAMESCREEN + "/" + game.id) }
                        .fillMaxWidth()
                )
                var iExpandCount = 0
                players.forEach {
                    // show max 3 players, when not expanded
                    if (bExpanded or (iExpandCount < 2 )) {
                        Row( modifier = Modifier.clickable {
                            if (!bExpanded) bExpanded = true }
                            .fillMaxWidth()
                        ) {
                            Text(text = it.name,)
                            // ToDo: Aktuelle Punktzahl ausgeben
                            /*if (bExpanded) {
                                Text(text = it.id.toString(),
                                )
                            }*/
                        }
                    }
                    iExpandCount++
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
                        modifier = Modifier.clickable { bExpanded = !bExpanded }
                            .fillMaxWidth()
                    )
                }
            }
        }
    if (bExpanded) {
        Row {
            Button(
                onClick = { deleteGame(game) },
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(text = "Delete")
            }
            Button(
                onClick = { resetGame(game) },
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(text = "ResetGame")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GamePreviewComponentPreview() {
    GamePreviewComponent(game = Game(1L, "Game 1"), players = listOf(
        Player(0L, "Player1", 1L, "1, 2, 3, 4, 5, 6, 7, 8, 9, 10"),
        Player(0L, "Player2", 1L, "1, 2, 3, 4, 5, 6, 7, 8, 9, 10"),
        Player(0L, "Player3", 1L, "1, 2, 3, 4, 5, 6, 7, 8, 9, 10")
    ), navigateToGame = {}
    , deleteGame = {}
    , resetGame = {})
}