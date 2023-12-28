package com.tjEnterprises.phase10Counter.ui.base

import androidx.activity.compose.BackHandler
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tjEnterprises.phase10Counter.R
import com.tjEnterprises.phase10Counter.ui.navigation.MainNavigation
import com.tjEnterprises.phase10Counter.ui.navigation.NavigationActions
import com.tjEnterprises.phase10Counter.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch

@Composable
fun AppBaseScreen(
    modifier: Modifier = Modifier, initialDrawerValue: DrawerValue = DrawerValue.Closed
) {
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    val navigationActions = NavigationActions(navController = navController)


    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: NavigationDestination.ADD_GAMESCREEN


    val drawerState = rememberDrawerState(initialValue = initialDrawerValue)

    ModalNavigationDrawer(modifier = modifier, drawerState = drawerState, drawerContent = {
        if( drawerState.isOpen ) {
            BackHandler {
                scope.launch { drawerState.close() }
            }
        }

        ModalDrawerSheet {
            IconButton(onClick = { scope.launch { drawerState.close() } }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = stringResource(id = R.string.closeNavigationDrawer))
            }
            Divider()

            NavigationDrawerItem(label = {
                Text(text = stringResource(id = R.string.title_addNewGame))
            }, selected = currentRoute == NavigationDestination.ADD_GAMESCREEN, onClick = {
                navigationActions.navigateToAnyScreen(NavigationDestination.ADD_GAMESCREEN)
                scope.launch { drawerState.close() }
            })

            NavigationDrawerItem(label = { Text(text = stringResource(id = R.string.title_selectGame)) },
                selected = currentRoute == NavigationDestination.SELECT_GAME,
                onClick = {
                    navigationActions.navigateToGameSelect()
                    scope.launch { drawerState.close() }
                })
            Divider()

            NavigationDrawerItem(label = { Text(text = stringResource(id = R.string.settings)) },
                selected = currentRoute == NavigationDestination.SETTINGS,
                onClick = {
                    navigationActions.navigateToAnyScreen(NavigationDestination.SETTINGS)
                    scope.launch { drawerState.close() }
                })
            Divider()
        }

    }) {
        MainNavigation(
            navController = navController,
            openDrawer = { scope.launch { drawerState.open() } },
            navigationActions = navigationActions
        )
    }
}

// preview not working because it cant provide a navcontroller