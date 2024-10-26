package com.tjEnterprises.phase10Counter.ui.addGame

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tjEnterprises.phase10Counter.R
import com.tjEnterprises.phase10Counter.ui.component.DefaultScaffoldNavigation
import com.tjEnterprises.phase10Counter.ui.navigation.NavigationDestination
import com.tjEnterprises.phase10Counter.ui.updateChecker.UpdateCheckerComponent

@Composable
fun AddGameScreen(
    modifier: Modifier = Modifier,
    navigateToGame: (String) -> Unit,
    openDrawer: () -> Unit,
    viewModel: AddGameViewModel = hiltViewModel()
) {
    val dontChangeUiWideScreen by viewModel.dontChangeUiWideScreen.collectAsState()
    val newCreatedGameID by viewModel.newCreatedGameId.collectAsState()

    AddGameScreen(
        openDrawer = openDrawer,
        addGame = { gameName, names ->
            viewModel.addGame(gameName, names)
        },
        newCreatedGameID = newCreatedGameID,
        tempPlayerNames = viewModel.tempPlayerNames,
        dontChangeUiWideScreen = dontChangeUiWideScreen,
        removeTempPlayerName = { viewModel.removeTempPlayerName(it) },
        navigateToGame = navigateToGame,
        resetNewCreatedGameID = { viewModel.resetNewCreatedGameID() },
        updateChecker = { UpdateCheckerComponent(it) },
        modifier = modifier
    )

}

@Composable
internal fun AddGameScreen(
    modifier: Modifier = Modifier,
    openDrawer: () -> Unit,
    navigateToGame: (String) -> Unit,
    addGame: (String, List<String>) -> Unit,
    resetNewCreatedGameID: () -> Unit,
    newCreatedGameID: Long,
    dontChangeUiWideScreen: Boolean,
    tempPlayerNames: SnapshotStateList<String>,
    removeTempPlayerName: (Int) -> Unit,
    updateChecker: @Composable (Modifier) -> Unit = {}
) {
    var textPlayer by rememberSaveable { mutableStateOf("") }
    var textGame by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current

    // when the gameID is not -1L (default) the side effect will cause a navigation to the newly created game
    // there are no other circumstances, where newCreatedGameID will change its value from -1L
    if (newCreatedGameID != -1L) {
        LaunchedEffect(key1 = newCreatedGameID, block = {
            resetNewCreatedGameID()     // reset gameId, else will be stuck in endless in navigating to new game
            textGame = ""
            textPlayer = ""
            tempPlayerNames.clear()
            navigateToGame(NavigationDestination.GAMESCREEN + "/" + newCreatedGameID)
        })
    }

    DefaultScaffoldNavigation(
        title = stringResource(id = R.string.title_addNewGame),
        openDrawer = openDrawer,
        dontChangeUiWideScreen = dontChangeUiWideScreen
    ) { scaffoldModifier ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = scaffoldModifier
                .then(modifier)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            updateChecker(Modifier.padding(bottom = 10.dp))
            BoxWithConstraints {
                // Put TextFields and button in a row
                if (maxWidth > 400.dp && !dontChangeUiWideScreen) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TextField(value = textGame,
                            onValueChange = { textGame = it },
                            label = { Text(stringResource(id = R.string.gameName)) },
                            maxLines = 1,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
                            ),
                            modifier = Modifier
                                .widthIn(1.dp, 150.dp)
                                .padding(horizontal = 8.dp)

                        )
                        TextField(value = textPlayer,
                            onValueChange = { textPlayer = it },
                            label = { Text(stringResource(id = R.string.playerName)) },
                            maxLines = 1,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(onNext = {
                                if (addPlayerToList(textPlayer, tempPlayerNames, context)) {
                                    textPlayer = ""
                                }
                            }),
                            modifier = Modifier.widthIn(1.dp, 150.dp)
                        )
                        Button(modifier = Modifier.padding(horizontal = 8.dp), onClick = {
                            if (addPlayerToList(textPlayer, tempPlayerNames, context)) {
                                textPlayer = ""
                            }
                        }) {
                            Text(text = stringResource(id = R.string.addPlayer))
                        }
                    }
                }
                // Stack TextFields and button above each other
                else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        TextField(value = textGame,
                            onValueChange = { textGame = it },
                            label = { Text(stringResource(id = R.string.gameName)) },
                            maxLines = 1,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
                            ),
                            modifier = Modifier
                                .widthIn(1.dp, 150.dp)
                                .padding(bottom = 8.dp)

                        )
                        TextField(value = textPlayer,
                            onValueChange = { textPlayer = it },
                            label = { Text(stringResource(id = R.string.playerName)) },
                            maxLines = 1,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(onNext = {
                                if (addPlayerToList(textPlayer, tempPlayerNames, context)) {
                                    textPlayer = ""
                                }
                            }),
                            modifier = Modifier
                                .widthIn(1.dp, 150.dp)
                                .padding(bottom = 4.dp)
                        )
                        Button(onClick = {
                            if (addPlayerToList(textPlayer, tempPlayerNames, context)) {
                                textPlayer = ""
                            }
                        }) {
                            Text(text = stringResource(id = R.string.addPlayer))
                        }
                    }
                }
            }

            Button(onClick = {
                if (textGame.isNotBlank()) {
                    if (tempPlayerNames.size >= 2) {
                        addGame(textGame, tempPlayerNames)
                    } else {
                        Toast.makeText(
                            context,
                            context.getString(R.string.atLeast2PlayersRequired),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        context, context.getString(R.string.gameMustHaveAName), Toast.LENGTH_SHORT
                    ).show()
                }

            }, modifier = Modifier.align(Alignment.End)) {
                Text(text = stringResource(id = R.string.start))
            }
            PlayersList(
                modifier = modifier.padding(top = 8.dp),
                tempPlayerNames = tempPlayerNames,
                removeTempPlayerName = removeTempPlayerName
            )

        }
    }
}

// returns true, if player was saved successfully
fun addPlayerToList(
    textFieldText: String, tempPlayerNames: SnapshotStateList<String>, context: Context
): Boolean {
    return if (textFieldText.isNotBlank()) {
        tempPlayerNames.add(0, textFieldText)
        true
    } else {
        Toast.makeText(context, context.getString(R.string.playerMustHaveAName), Toast.LENGTH_SHORT)
            .show()
        false
    }
}


@Preview(showBackground = true, widthDp = 400)
@Preview(showBackground = true, widthDp = 500, heightDp = 500)
@Composable
fun AddGameScreenPreview() {
    // gotta test very long names
    val tempPlayerNames = remember {
        mutableStateListOf<String>(
            "Player 1",
            "Player 2",
            "Player 3",
            "Plaaaaaaaaaaaaaaaaayyyyyyyyyyyyyyyyeeeeeeeeeeeeeeeerrrrrrrrrrrrrrrrr",
            "Plaaaaaaaaaaaaaaaaayyyyyyyyyyyyyyyyeeeeee"
        )
    }
    AddGameScreen(openDrawer = { },
        navigateToGame = {},
        addGame = { _, _ -> },
        resetNewCreatedGameID = { },
        newCreatedGameID = -1L,
        dontChangeUiWideScreen = false,
        tempPlayerNames = tempPlayerNames,
        removeTempPlayerName = {},
        updateChecker = {})
}