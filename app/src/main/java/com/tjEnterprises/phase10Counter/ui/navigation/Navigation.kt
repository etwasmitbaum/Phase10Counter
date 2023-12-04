/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tjEnterprises.phase10Counter.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.tjEnterprises.phase10Counter.ui.addGame.AddGameScreen
import com.tjEnterprises.phase10Counter.ui.game.GameScreen
import com.tjEnterprises.phase10Counter.ui.selectGame.SelectGame

@Composable
fun MainNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    openDrawer: () -> Unit = {},
    navigationActions: NavigationActions
) {
    NavHost(
        navController = navController,
        startDestination = NavigationDestination.ADD_GAMESCREEN,
        modifier = modifier,
        contentAlignment = Alignment.TopStart // this is needed, else a weird bouncing animation in introduced. see https://issuetracker.google.com/issues/295536728
    ) {
        composable(route = NavigationDestination.ADD_GAMESCREEN) {
            AddGameScreen(
                openDrawer = openDrawer, navigateToGame = navigationActions.navigateToGame
            )
        }
        selectGameGraph(openDrawer = openDrawer, navigationActions = navigationActions)
    }
}

fun NavGraphBuilder.selectGameGraph(openDrawer: () -> Unit, navigationActions: NavigationActions) {

    navigation(
        startDestination = NavigationDestination.SELECT_GAME,
        route = NavigationDestination.GAME_ROUTE
    ) {

        composable(NavigationDestination.SELECT_GAME) {
            SelectGame(openDrawer = openDrawer, navigateToGame = navigationActions.navigateToGame)
        }

        composable(
            "${NavigationDestination.GAMESCREEN}/{gameId}",
            arguments = listOf(navArgument("gameId") { type = NavType.LongType })
        ) { backStackEntry ->
            var gameId = backStackEntry.arguments?.getLong("gameId") ?: 1
            // getLong returns 0L if key is not mapped
            if (gameId == 0L) {
                gameId = 1L
            }
            GameScreen(gameId = gameId, openDrawer = openDrawer)
        }
    }
}