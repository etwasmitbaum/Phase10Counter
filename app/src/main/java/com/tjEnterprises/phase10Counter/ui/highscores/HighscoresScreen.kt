package com.tjEnterprises.phase10Counter.ui.highscores

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tjEnterprises.phase10Counter.R
import com.tjEnterprises.phase10Counter.data.local.database.Highscore
import com.tjEnterprises.phase10Counter.ui.HighscoresUiState
import com.tjEnterprises.phase10Counter.ui.component.DefaultScaffold
import com.tjEnterprises.phase10Counter.ui.component.HighscoreComponent
import com.tjEnterprises.phase10Counter.ui.component.VerticalDivider

@Composable
fun Highscores(
    modifier: Modifier = Modifier,
    viewModel: HighscoresViewModel = hiltViewModel(),
    openDrawer: () -> Unit
) {
    val highscoresUiState by viewModel.highscoresUiState.collectAsState()

    when (highscoresUiState) {
        is HighscoresUiState.HighscoresSuccess -> {
            Highscores(
                modifier = modifier,
                title = stringResource(id = R.string.highscores),
                highscores = (highscoresUiState as HighscoresUiState.HighscoresSuccess).highscores,
                openDrawer = openDrawer
            )
        }

        is HighscoresUiState.HighscoresError -> {
            DefaultScaffold(title = stringResource(id = R.string.highscoresError), openDrawer = openDrawer) {}
        }

        is HighscoresUiState.HighscoresLoading -> {
            DefaultScaffold(title = stringResource(id = R.string.highscoresLoading), openDrawer = openDrawer) {}
        }
    }
}

@Composable
internal fun Highscores(
    modifier: Modifier, title: String, highscores: List<Highscore>, openDrawer: () -> Unit
) {
    DefaultScaffold(title = title, openDrawer = openDrawer) { scaffoldModifier ->
        Column(modifier = scaffoldModifier.then(modifier)) {
            Row(
                modifier = Modifier.height(IntrinsicSize.Min)
            ) {
                Text(
                    text = stringResource(id = R.string.name),
                    modifier = Modifier
                        .weight(0.333f)
                        .padding(bottom = 4.dp),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
                VerticalDivider()
                Text(
                    text = stringResource(id = R.string.points),
                    modifier = Modifier.weight(0.333f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
                VerticalDivider()
                Text(
                    text = stringResource(id = R.string.date),
                    modifier = Modifier.weight(0.333f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
            }
            //Divider()
            highscores.forEach { highscore ->
                HighscoreComponent(highscore = highscore)
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun HighscoresPreview() {
    Highscores(
        modifier = Modifier, title = stringResource(id = R.string.highscores), highscores = listOf(
            Highscore(playerName = "Player 1", points = 100L),
            Highscore(playerName = "Player 2", points = 37824L),
            Highscore(playerName = "Player 3", points = 10L),
            Highscore(playerName = "Player 4", points = 560L)
        )
    ) {

    }
}