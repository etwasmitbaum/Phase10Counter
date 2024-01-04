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

package com.tjEnterprises.phase10Counter.data.di

import com.tjEnterprises.phase10Counter.data.local.database.Game
import com.tjEnterprises.phase10Counter.data.local.database.Highscore
import com.tjEnterprises.phase10Counter.data.local.database.Phases
import com.tjEnterprises.phase10Counter.data.local.database.PointHistory
import com.tjEnterprises.phase10Counter.data.local.models.GameModel
import com.tjEnterprises.phase10Counter.data.local.models.PlayerModel
import com.tjEnterprises.phase10Counter.data.local.models.SettingsModel
import com.tjEnterprises.phase10Counter.data.local.repositories.DatabaseRepository
import com.tjEnterprises.phase10Counter.data.local.repositories.SettingsRepository
import com.tjEnterprises.phase10Counter.data.network.repositories.UpdateCheckerRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Singleton
    @Binds
    fun bindsDatabaseRepository(
        databaseRepository: DatabaseRepository.DefaultDatabaseRepository
    ): DatabaseRepository

    @Singleton
    @Binds
    fun bindsSettingsRepository(settingsRepository: SettingsRepository.SettingsRepositoryImpl): SettingsRepository

    @Singleton
    @Binds
    fun bindsUpdateCheckerRepository(updateCheckerRepository: UpdateCheckerRepository.UpdateCheckerRepositoryImpl) : UpdateCheckerRepository
}

class FakeDatabaseRepository @Inject constructor(
) : DatabaseRepository {

    override val games: Flow<List<GameModel>> = flowOf()
    override val highscores: Flow<List<Highscore>> = flowOf()
    override suspend fun insertPlayer(playerName: String, gameId: Long): Long {
        TODO("Not yet implemented")
    }

    override fun getPlayersFlowFromGame(gameId: Long): Flow<List<PlayerModel>> {
        TODO("Not yet implemented")
    }

    override suspend fun getPlayersFromGame(gameId: Long): List<PlayerModel> {
        TODO("Not yet implemented")
    }

    override suspend fun deletePlayer(playerId: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun updatePlayerPhases(
        playerId: Long,
        gameId: Long,
        openPhases: List<Boolean>
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun getPhasesOfPlayer(playerId: Long): List<Phases> {
        TODO("Not yet implemented")
    }

    override fun getGameFlowFromId(gameId: Long): Flow<GameModel> {
        TODO("Not yet implemented")
    }

    override suspend fun getGameFromId(gameId: Long): GameModel {
        TODO("Not yet implemented")
    }

    override suspend fun insertGame(gameName: String): Long {
        TODO("Not yet implemented")
    }

    override suspend fun deleteGame(game: Game) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteGame(gameId: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun updateGameModifiedTimestamp(gameId: Long) {
        TODO("Not yet implemented")
    }

    override fun getPointHistoryOfGameAsFlow(gameId: Long): Flow<List<PointHistory>> {
        TODO("Not yet implemented")
    }

    override suspend fun getPointHistoryOfPlayer(playerId: Long): List<PointHistory> {
        TODO("Not yet implemented")
    }

    override suspend fun insertPointHistory(point: Long, gameId: Long, playerId: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun deletePointHistoryOfPlayer(playerId: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun insertHighscore(playerName: String, point: Long, timeStamp: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteHighScore(highscoreId: Long) {
        TODO("Not yet implemented")
    }

}

class fakeSettingsRepository @Inject constructor() : SettingsRepository {
    override val settingsModelFlow: Flow<SettingsModel> = flowOf()
    override suspend fun updateCheckForUpdates(checkForUpdates: Boolean) {
        TODO("Not yet implemented")
    }

    override suspend fun updateUseDynamicColors(useDynamicColors: Boolean) {
        TODO("Not yet implemented")
    }

    override suspend fun updateUseSystemTheme(useSystemTheme: Boolean) {
        TODO("Not yet implemented")
    }

    override suspend fun updateUseDarkTheme(useDarkTheme: Boolean) {
        TODO("Not yet implemented")
    }
}

class UpdateCheckerRepository @Inject constructor() : UpdateCheckerRepository {
    override suspend fun getLatestReleaseVersionNumber(): Int {
        TODO("Not yet implemented")
    }

}