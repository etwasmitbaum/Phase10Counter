package com.tjEnterprises.phase10Counter.ui.highscores

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tjEnterprises.phase10Counter.R
import com.tjEnterprises.phase10Counter.data.local.database.Highscore
import com.tjEnterprises.phase10Counter.ui.HighscoresUiState
import com.tjEnterprises.phase10Counter.ui.component.DefaultScaffoldNavigation

@Composable
fun Highscores(
    modifier: Modifier = Modifier,
    viewModel: HighscoresViewModel = hiltViewModel(),
    openDrawer: () -> Unit
) {
    val dontChangeUiWideScreen by viewModel.dontChangeUiWideScreen.collectAsState()
    val highscoresUiState by viewModel.highscoresUiState.collectAsState()

    when (highscoresUiState) {
        is HighscoresUiState.HighscoresSuccess -> {
            Highscores(
                modifier = modifier,
                dontChangeUiWideScreen = dontChangeUiWideScreen,
                title = stringResource(id = R.string.highscores),
                highscores = (highscoresUiState as HighscoresUiState.HighscoresSuccess).highscores,
                openDrawer = openDrawer
            )
        }

        is HighscoresUiState.HighscoresError -> {
            DefaultScaffoldNavigation(
                title = stringResource(id = R.string.highscoresError),
                openDrawer = openDrawer,
            ) {}
        }

        is HighscoresUiState.HighscoresLoading -> {
            DefaultScaffoldNavigation(
                title = stringResource(id = R.string.highscoresLoading),
                openDrawer = openDrawer,
            ) {}
        }
    }
}

@Composable
internal fun Highscores(
    modifier: Modifier,
    title: String,
    highscores: List<Highscore>,
    dontChangeUiWideScreen: Boolean,
    openDrawer: () -> Unit
) {
    DefaultScaffoldNavigation(
        title = title, openDrawer = openDrawer, dontChangeUiWideScreen = dontChangeUiWideScreen
    ) { scaffoldModifier ->
        LazyColumn(modifier = scaffoldModifier.then(modifier)) {
            item {
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
            }


            items(highscores, key = {highscore -> highscore.id}) { highscore ->
                HighscoreComponent(highscore = highscore)
            }

            item {
                if (highscores.isEmpty()) {
                    HorizontalDivider()
                    Text(
                        text = stringResource(id = R.string.noHighscoresYet),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        textAlign = TextAlign.Center
                    )

                }
            }

            item {
                HorizontalDivider()
                Text(
                    text = stringResource(id = R.string.highscoresHint),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    textAlign = TextAlign.Center
                )
            }


        }

    }
}

@Preview(showBackground = true, heightDp = 300)
@Composable
fun HighscoresPreview() {
    Highscores(
        modifier = Modifier,
        dontChangeUiWideScreen = false,
        title = stringResource(id = R.string.highscores),
        highscores = listOf(
            Highscore(playerName = "Player 1", points = 100L, id = 1L),
            Highscore(playerName = "Player 2", points = 37824L, id = 2L),
            Highscore(playerName = "Player 3", points = 10L, id = 3L),
            Highscore(playerName = "Player 4", points = 560L, id = 4L)
        )
    ) {

    }
}

@Preview(showBackground = true, heightDp = 300)
@Composable
fun HighscoresPreviewNoScores() {
    Highscores(
        modifier = Modifier, dontChangeUiWideScreen = false, title = stringResource(id = R.string.highscores), highscores = listOf()
    ) {

    }
}