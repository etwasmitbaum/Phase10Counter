package com.tjEnterprises.phase10Counter.ui.navigation

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

class NavigationActions(navController: NavHostController) {
    val navigateToGameSelect: () -> Unit = {
        navController.navigate(NavigationDestination.SELECT_GAME) {
            popUpTo(navController.graph.findStartDestination().id) {
                inclusive = false
            }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
        }
    }

    val navigateToGame: (String) -> Unit = {route ->
        navController.navigate(route) {
            launchSingleTop = true
            restoreState = true
        }
    }

    val navigateToAnyScreen: (String) -> Unit = {route ->
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                inclusive = false
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    val navigateToAboutLibraries: () -> Unit = {
        navController.navigate(NavigationDestination.ABOUT_LIBRARIES) {
            launchSingleTop = true
            restoreState = true
        }
    }

    val navigateToSettings: () -> Unit = {
        navController.navigate(NavigationDestination.SETTINGS) {
            launchSingleTop = true
            restoreState = true
        }
    }
}