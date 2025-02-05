package com.tjEnterprises.phase10Counter.ui.addGame

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.tjEnterprises.phase10Counter.R
import com.tjEnterprises.phase10Counter.data.local.models.GameType
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

    AddGameScreenBase(
        openDrawer = openDrawer,
        addGame = { gameName, gameType, names ->
            viewModel.addGame(gameName, gameType, names)
        },
        newCreatedGameID = newCreatedGameID,
        tempPlayerNames = viewModel.tempPlayerNames,
        dontChangeUiWideScreen = dontChangeUiWideScreen,
        removeTempPlayerName = { viewModel.removeTempPlayerName(it) },
        navigateToGame = navigateToGame,
        resetNewCreatedGameID = { viewModel.resetNewCreatedGameID() },
        defaultGameType = GameType.defaultGameType,
        updateChecker = { UpdateCheckerComponent(it) },
        modifier = modifier
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AddGameScreenBase(
    modifier: Modifier = Modifier,
    openDrawer: () -> Unit,
    navigateToGame: (String) -> Unit,
    addGame: (String, GameType.Type, List<String>) -> Unit,
    resetNewCreatedGameID: () -> Unit,
    newCreatedGameID: Long,
    dontChangeUiWideScreen: Boolean,
    tempPlayerNames: SnapshotStateList<String>,
    removeTempPlayerName: (Int) -> Unit,
    defaultGameType: GameType.Type,
    updateChecker: @Composable (Modifier) -> Unit = {}
) {
    var textPlayer by rememberSaveable { mutableStateOf("") }
    var textGame by rememberSaveable { mutableStateOf("") }
    var selectedGameType by rememberSaveable(stateSaver = GameType.GameTypeSaver) {
        mutableStateOf(
            defaultGameType
        )
    }
    var expandedGameDropdown by remember { mutableStateOf(false) }

    // when the gameID is not -1L (default) the side effect will cause a navigation to the newly created game
    // there are no other circumstances, where newCreatedGameID will change its value from -1L
    if (newCreatedGameID != -1L) {
        LaunchedEffect(key1 = newCreatedGameID, block = {
            resetNewCreatedGameID()     // reset gameId, else will be stuck in endless in navigating to new game
            textGame = ""
            textPlayer = ""
            selectedGameType = defaultGameType
            tempPlayerNames.clear()
            expandedGameDropdown = false
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
                    AddGameScreenLandscape(
                        textGame = textGame,
                        onTextGameChange = { textGame = it },
                        expandedGameDropdown = expandedGameDropdown,
                        onExpandedGameDropdownChanged = { expandedGameDropdown = it },
                        selectedGameType = selectedGameType,
                        onSelectedGameTypeChanged = { selectedGameType = it },
                        textPlayer = textPlayer,
                        onTextPlayerChanged = { textPlayer = it },
                        tempPlayerNames = tempPlayerNames,
                        addGame = addGame
                    )
                } else {
                    AddGameScreenPortrait(
                        textGame = textGame,
                        onTextGameChange = { textGame = it },
                        expandedGameDropdown = expandedGameDropdown,
                        onExpandedGameDropdownChanged = { expandedGameDropdown = it },
                        selectedGameType = selectedGameType,
                        onSelectedGameTypeChanged = { selectedGameType = it },
                        textPlayer = textPlayer,
                        onTextPlayerChanged = { textPlayer = it },
                        tempPlayerNames = tempPlayerNames,
                        addGame = addGame
                    )
                }
            }

            PlayersList(
                modifier = modifier.padding(top = 8.dp),
                tempPlayerNames = tempPlayerNames,
                removeTempPlayerName = removeTempPlayerName
            )

        }
    }
}

@Composable
internal fun AddGameScreenPortrait(
    textGame: String,
    onTextGameChange: (String) -> Unit,
    expandedGameDropdown: Boolean,
    onExpandedGameDropdownChanged: (Boolean) -> Unit,
    selectedGameType: GameType.Type,
    onSelectedGameTypeChanged: (GameType.Type) -> Unit,
    textPlayer: String,
    onTextPlayerChanged: (String) -> Unit,
    tempPlayerNames: SnapshotStateList<String>,
    addGame: (String, GameType.Type, List<String>) -> Unit
) {

    val context = LocalContext.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()
    ) {
        TextField(value = textGame,
            onValueChange = { onTextGameChange(it) },
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

        GameDropDownMenu(
            expandedGameDropdown = expandedGameDropdown,
            onExpandedGameDropdownChanged = onExpandedGameDropdownChanged,
            selectedGameType = selectedGameType,
            onSelectedGameTypeChanged = onSelectedGameTypeChanged,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        TextField(value = textPlayer,
            onValueChange = { onTextPlayerChanged(it) },
            label = { Text(stringResource(id = R.string.playerName)) },
            maxLines = 1,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = {
                if (addPlayerToList(textPlayer, tempPlayerNames, context)) {
                    onTextPlayerChanged("")
                }
            }),
            modifier = Modifier
                .widthIn(1.dp, 150.dp)
                .padding(bottom = 4.dp)
        )
        Button(onClick = {
            if (addPlayerToList(textPlayer, tempPlayerNames, context)) {
                onTextPlayerChanged("")
            }
        }) {
            Text(text = stringResource(id = R.string.addPlayer))
        }

        Button(onClick = {
            if (textGame.isNotBlank()) {
                if (tempPlayerNames.size >= 2) {
                    addGame(textGame, selectedGameType, tempPlayerNames)
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
    }
}

@Composable
internal fun AddGameScreenLandscape(
    textGame: String,
    onTextGameChange: (String) -> Unit,
    expandedGameDropdown: Boolean,
    onExpandedGameDropdownChanged: (Boolean) -> Unit,
    selectedGameType: GameType.Type,
    onSelectedGameTypeChanged: (GameType.Type) -> Unit,
    textPlayer: String,
    onTextPlayerChanged: (String) -> Unit,
    tempPlayerNames: SnapshotStateList<String>,
    addGame: (String, GameType.Type, List<String>) -> Unit
) {

    val context = LocalContext.current

    // wrap max width column to center the ConstraintLayout
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        ConstraintLayout {
            val (gameTextField, gameDropdown, playerTextField, addPlayerButton, startButton) = createRefs()

            TextField(value = textGame,
                onValueChange = { onTextGameChange(it) },
                label = { Text(stringResource(id = R.string.gameName)) },
                maxLines = 1,
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
                ),
                modifier = Modifier
                    .constrainAs(gameTextField) {
                        top.linkTo(parent.top, 8.dp)
                        start.linkTo(parent.start, 8.dp)
                    }
                    .widthIn(1.dp, 150.dp))

            GameDropDownMenu(expandedGameDropdown = expandedGameDropdown,
                onExpandedGameDropdownChanged = onExpandedGameDropdownChanged,
                selectedGameType = selectedGameType,
                onSelectedGameTypeChanged = onSelectedGameTypeChanged,
                modifier = Modifier.constrainAs(gameDropdown) {
                    top.linkTo(gameTextField.bottom, 4.dp)
                    start.linkTo(gameTextField.start)
                    end.linkTo(gameTextField.end)
                })

            TextField(value = textPlayer,
                onValueChange = { onTextPlayerChanged(it) },
                label = { Text(stringResource(id = R.string.playerName)) },
                maxLines = 1,
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = {
                    if (addPlayerToList(textPlayer, tempPlayerNames, context)) {
                        onTextPlayerChanged("")
                    }
                }),
                modifier = Modifier
                    .constrainAs(playerTextField) {
                        top.linkTo(gameTextField.top)
                        bottom.linkTo(gameTextField.bottom)
                        start.linkTo(gameTextField.end, 8.dp)
                    }
                    .widthIn(1.dp, 150.dp))

            // Add player
            Button(onClick = {
                if (addPlayerToList(textPlayer, tempPlayerNames, context)) {
                    onTextPlayerChanged("")
                }
            },
                content = { Text(text = stringResource(id = R.string.addPlayer)) },
                modifier = Modifier.constrainAs(addPlayerButton) {
                        top.linkTo(playerTextField.top)
                        bottom.linkTo(playerTextField.bottom)
                        start.linkTo(playerTextField.end, 8.dp)
                        end.linkTo(parent.end, 8.dp)
                    })

            // Start game
            Button(onClick = {
                if (textGame.isNotBlank()) {
                    if (tempPlayerNames.size >= 2) {
                        addGame(textGame, selectedGameType, tempPlayerNames)
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

            }, content = {
                Text(text = stringResource(id = R.string.start))
            }, modifier = Modifier.constrainAs(startButton) {
                top.linkTo(gameDropdown.top)
                bottom.linkTo(gameDropdown.bottom)
                start.linkTo(addPlayerButton.start)
                end.linkTo(addPlayerButton.end)
            })
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun GameDropDownMenu(
    expandedGameDropdown: Boolean,
    onExpandedGameDropdownChanged: (Boolean) -> Unit,
    selectedGameType: GameType.Type,
    onSelectedGameTypeChanged: (GameType.Type) -> Unit,
    modifier: Modifier = Modifier,
) {
    ExposedDropdownMenuBox(
        expanded = expandedGameDropdown,
        onExpandedChange = { onExpandedGameDropdownChanged(it) },
        modifier = Modifier
            .then(modifier)
            .widthIn(1.dp, 150.dp)
    ) {

        OutlinedTextField(
            value = stringResource(id = selectedGameType.resourceId),
            onValueChange = { },
            readOnly = true,
            label = { Text(stringResource(id = R.string.gameType)) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGameDropdown)
            },
            modifier = Modifier
                .menuAnchor(
                    MenuAnchorType.SecondaryEditable, enabled = true
                )
                .fillMaxWidth()
        )

        ExposedDropdownMenu(expanded = expandedGameDropdown,
            onDismissRequest = { onExpandedGameDropdownChanged(false) }) {
            GameType.availableGameTypes.forEach { item: GameType.Type ->
                DropdownMenuItem(text = { Text(text = stringResource(id = item.resourceId)) },
                    onClick = {
                        onSelectedGameTypeChanged(item)
                        onExpandedGameDropdownChanged(false)
                    })
            }
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
@Preview(showBackground = true, widthDp = 650, heightDp = 500)
@Preview(device = Devices.PIXEL_4A)
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

    AddGameScreenBase(openDrawer = { },
        navigateToGame = {},
        addGame = { _, _, _ -> },
        resetNewCreatedGameID = { },
        newCreatedGameID = -1L,
        dontChangeUiWideScreen = false,
        tempPlayerNames = tempPlayerNames,
        removeTempPlayerName = {},
        defaultGameType = GameType.defaultGameType,
        updateChecker = {})
}