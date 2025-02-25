package com.tjEnterprises.phase10Counter.ui.highscores

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tjEnterprises.phase10Counter.data.local.database.Highscore
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun HighscoreComponent(modifier: Modifier = Modifier, highscore: Highscore){

    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val topBotPadding = 4.dp

    HorizontalDivider()
    Row(
        modifier = Modifier
            .height(IntrinsicSize.Min)
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = highscore.playerName,
            modifier = Modifier
                .weight(0.333f)
                .padding(vertical = topBotPadding),
            textAlign = TextAlign.Center
        )
        VerticalDivider()
        Text(
            text = highscore.points.toString(),
            modifier = Modifier
                .weight(0.333f)
                .padding(vertical = topBotPadding),
            textAlign = TextAlign.Center
        )
        VerticalDivider()
        Text(
            text = sdf.format(highscore.timestamp),
            modifier = Modifier
                .weight(0.333f)
                .padding(vertical = topBotPadding),
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HighscoreComponentPreview() {
    HighscoreComponent(highscore = Highscore(playerName = "Player 1", points = 256L))
}