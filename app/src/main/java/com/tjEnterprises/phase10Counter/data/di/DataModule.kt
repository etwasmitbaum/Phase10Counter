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

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import com.tjEnterprises.phase10Counter.data.DatabaseRepository
import com.tjEnterprises.phase10Counter.data.local.database.Game
import com.tjEnterprises.phase10Counter.data.local.database.Player
import com.tjEnterprises.phase10Counter.data.local.database.PointHistory
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
}

class FakeDatabaseRepository @Inject constructor(
    override val players: Flow<List<Player>>,
    override val pointHistory: Flow<List<PointHistory>>,
) : DatabaseRepository {
    override val games: Flow<List<Game>> = flowOf(fakeDatabases)

    override suspend fun insertPlayer(player: Player) {
        TODO("Not yet implemented")
    }

    override suspend fun getPlayerFromGame(gameID: Long): Flow<List<Player>> {
        TODO("Not yet implemented")
    }

    override suspend fun deletePlayer(player: Player) {
        TODO("Not yet implemented")
    }

    override suspend fun updatePlayer(player: Player) {
        TODO("Not yet implemented")
    }

    override suspend fun changePlayerName(player: Player) {
        TODO("Not yet implemented")
    }

    override suspend fun changePlayerPhases(player: Player) {
        TODO("Not yet implemented")
    }

    override suspend fun insertGame(game: Game): Long {
        TODO("Not yet implemented")
    }

    override suspend fun removeGame(game: Game) {
        TODO("Not yet implemented")
    }

    override suspend fun updateGameModifiedTimestamp(game: Game) {
        TODO("Not yet implemented")
    }

    override suspend fun getPointHistory(): Flow<List<PointHistory>> {
        TODO("Not yet implemented")
    }

    override suspend fun insertPointHistory(pointHistory: PointHistory) {
        TODO("Not yet implemented")
    }

    override suspend fun removePointHistory(pointHistory: PointHistory) {
        TODO("Not yet implemented")
    }
}

val fakeDatabases = listOf(Game(name = "One"), Game(name = "Two"), Game(name = "Three"))
