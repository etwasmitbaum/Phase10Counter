package com.tjEnterprises.phase10Counter.ui.base

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tjEnterprises.phase10Counter.R
import com.tjEnterprises.phase10Counter.ui.navigation.MainNavigation
import com.tjEnterprises.phase10Counter.ui.navigation.NavigationActions
import com.tjEnterprises.phase10Counter.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch

@Composable
fun AppBaseScreen(
    modifier: Modifier = Modifier,
    initialDrawerValue: DrawerValue = DrawerValue.Closed,
    viewModel: AppBaseScreenViewModel = hiltViewModel()
) {
    val gamesCount by viewModel.gamesCount.collectAsState()

    AppBaseScreen(
        modifier = modifier, initialDrawerValue = initialDrawerValue, gamesCount = gamesCount
    )
}

@Composable
internal fun AppBaseScreen(
    modifier: Modifier = Modifier,
    initialDrawerValue: DrawerValue = DrawerValue.Closed,
    gamesCount: Long
) {
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    val navigationActions = NavigationActions(navController = navController)

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: NavigationDestination.ADD_GAMESCREEN

    val drawerState = rememberDrawerState(initialValue = initialDrawerValue)
    val scrollState = rememberScrollState()

    ModalNavigationDrawer(drawerState = drawerState, drawerContent = {

        ModalDrawerSheet(
            windowInsets = WindowInsets.safeDrawing,
            drawerState = drawerState,
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(state = scrollState)
        ) {
            // Close Navigation Button
            IconButton(onClick = { scope.launch { drawerState.close() } }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(id = R.string.closeNavigationDrawer)
                )
            }
            HorizontalDivider()

            // Add new Game
            NavigationDrawerItem(label = {
                Text(text = stringResource(id = R.string.title_addNewGame))
            }, selected = currentRoute == NavigationDestination.ADD_GAMESCREEN, onClick = {
                navigationActions.navigateToAddGameScreen()
                scope.launch { drawerState.close() }
            })

            // Select Game
            NavigationDrawerItem(label = { Text(text = stringResource(id = R.string.title_selectGame)) },
                selected = currentRoute == NavigationDestination.SELECT_GAME,
                onClick = {
                    navigationActions.navigateToGameSelect()
                    scope.launch { drawerState.close() }
                })
            HorizontalDivider()

            // Highscores
            NavigationDrawerItem(label = { Text(text = stringResource(id = R.string.highscores)) },
                selected = currentRoute == NavigationDestination.HIGHSCORES,
                onClick = {
                    navigationActions.navigateToHighscores()
                    scope.launch { drawerState.close() }
                })


            // Push settings to bottom
            Spacer(modifier = Modifier.weight(1f))
            HorizontalDivider()

            // Settings
            NavigationDrawerItem(label = { Text(text = stringResource(id = R.string.settings)) },
                selected = currentRoute == NavigationDestination.SETTINGS,
                icon = {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = stringResource(
                            id = R.string.settings
                        )
                    )
                },
                onClick = {
                    navigationActions.navigateToSettings()
                    scope.launch { drawerState.close() }
                })
            HorizontalDivider()

            // About Screen
            NavigationDrawerItem(label = { Text(text = stringResource(id = R.string.about)) },
                selected = currentRoute == NavigationDestination.ABOUT_SCREEN,
                icon = {
                    Icon(
                        imageVector = Icons.Default.Info, contentDescription = stringResource(
                            id = R.string.about
                        )
                    )
                },
                onClick = {
                    navigationActions.navigateToAboutScreen()
                    scope.launch { drawerState.close() }
                })
        }

    }) {
        MainNavigation(
            navController = navController,
            openDrawer = { scope.launch { drawerState.open() } },
            navigationActions = navigationActions,
            startDestination = if (gamesCount == 0L) NavigationDestination.ADD_GAMESCREEN else NavigationDestination.GAME_ROUTE
        )
    }
}

// preview not working because it needs a viewModel from the default Route