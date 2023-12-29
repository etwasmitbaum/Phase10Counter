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

            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
        }
    }

    val navigateToAnyScreen: (String) -> Unit = {route ->
        navController.navigate(route) {
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

    val navigateToAboutLibraries: () -> Unit = {
        navController.navigate(NavigationDestination.ABOUT_LIBRARIES) {
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
}