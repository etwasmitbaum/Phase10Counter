package com.tjEnterprises.phase10Counter.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { navigateToGame(NavigationDestination.GAMESCREEN + "/" + game.id) },
        border = BorderStroke(4.dp, Color.Blue)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = game.name, fontSize = 30.sp)
            players.forEach { Text(text = it.name) }
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
    ), navigateToGame = {})
}