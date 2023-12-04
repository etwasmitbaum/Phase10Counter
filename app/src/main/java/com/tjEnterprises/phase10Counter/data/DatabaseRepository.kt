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

package com.tjEnterprises.phase10Counter.data

import com.tjEnterprises.phase10Counter.data.local.database.Game
import com.tjEnterprises.phase10Counter.data.local.database.GameDao
import com.tjEnterprises.phase10Counter.data.local.database.Player
import com.tjEnterprises.phase10Counter.data.local.database.PlayerDao
import com.tjEnterprises.phase10Counter.data.local.database.PoinHistoryDao
import com.tjEnterprises.phase10Counter.data.local.database.PointHistory
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface DatabaseRepository {
    val games: Flow<List<Game>>
    val players: Flow<List<Player>>
    val pointHistory: Flow<List<PointHistory>>

    suspend fun insertPlayer(player: Player)
    suspend fun getPlayerFromGame(gameID: Long): Flow<List<Player>>
    suspend fun deletePlayer(player: Player)
    suspend fun updatePlayer(player: Player)
    suspend fun changePlayerName(player: Player)
    suspend fun changePlayerPhases(player: Player)
    suspend fun insertGame(game: Game): Long
    suspend fun removeGame(game: Game)
    suspend fun updateGameModifiedTimestamp(game: Game)
    suspend fun getPointHistory(): Flow<List<PointHistory>>
    suspend fun insertPointHistory(pointHistory: PointHistory)
    suspend fun removePointHistory(pointHistory: PointHistory)

    class DefaultDatabaseRepository @Inject constructor(
        private val gameDao: GameDao,
        private val playerDao: PlayerDao,
        private val pointHistoryDao: PoinHistoryDao
    ) : DatabaseRepository {

        override var games: Flow<List<Game>> = gameDao.getAllGames()
        override var players: Flow<List<Player>> = playerDao.getAllPlayers()
        override val pointHistory: Flow<List<PointHistory>> =
            pointHistoryDao.getPointHistory()


        override suspend fun insertPlayer(player: Player) {
            playerDao.insertPlayer(player)
        }

        override suspend fun getPlayerFromGame(gameID: Long): Flow<List<Player>> {
            return playerDao.getAllPlayersFromGame(gameID)
        }

        override suspend fun deletePlayer(player: Player) {
            playerDao.deletePlayer(player)
        }

        override suspend fun updatePlayer(player: Player) {
            playerDao.updatePlayer(player)
            updateGameModifiedTimestamp(playerDao.getGameFromPlayerID(player.id))
        }

        override suspend fun changePlayerName(player: Player) {
            updatePlayer(player)
        }

        override suspend fun changePlayerPhases(player: Player) {
            updatePlayer(player)
        }

        override suspend fun insertGame(game: Game) : Long {
            return gameDao.insertGame(game)
        }

        override suspend fun removeGame(game: Game) {
            gameDao.deleteGame(game)
        }

        override suspend fun updateGameModifiedTimestamp(game: Game) {
            game.timestampModified = System.currentTimeMillis()
            gameDao.updateGame(game)
        }

        override suspend fun getPointHistory(): Flow<List<PointHistory>> {
            return pointHistoryDao.getPointHistory()
        }

        override suspend fun insertPointHistory(pointHistory: PointHistory) {
            pointHistoryDao.insertPoint(pointHistory)
            updateGameModifiedTimestamp(pointHistoryDao.getGameByPlayerId(pointHistory.playerID))
        }

        override suspend fun removePointHistory(pointHistory: PointHistory) {
            pointHistoryDao.deletePoint(pointHistory)
        }
    }
}
