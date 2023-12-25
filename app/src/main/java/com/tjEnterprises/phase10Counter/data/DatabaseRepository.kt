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
import com.tjEnterprises.phase10Counter.data.local.database.PointHistoryDao
import com.tjEnterprises.phase10Counter.data.local.database.PointHistory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface DatabaseRepository {
    val games: Flow<List<Game>>
    val players: Flow<List<Player>>
    val pointHistory: Flow<List<PointHistory>>

    suspend fun insertPlayer(player: Player)
    suspend fun getPlayer(playerId: Long) : Player
    suspend fun getPlayerFromGame(gameID: Long): Flow<List<PlayerModel>>
    suspend fun deletePlayer(player: Player)
    suspend fun updatePlayer(player: Player)
    suspend fun changePlayerName(player: Player)
    suspend fun changePlayerPhase(playerId: Long, phase: Long, state: Boolean)
    suspend fun insertGame(game: Game): Long
    suspend fun getGameFromId(gameID: Long): GameModel
    suspend fun getAllGames(): Flow<List<GameModel>>
    suspend fun removeGame(game: Game)
    suspend fun updateGameModifiedTimestamp(game: Game)
    suspend fun getPointHistory(): Flow<List<PointHistory>>
    suspend fun getPointHistoryFromPlayerId(playerId: Long): Flow<List<PointHistoryModel>>
    suspend fun insertPointHistory(pointHistory: PointHistory)
    suspend fun addPointHistory(playerId: Long, point: Long)
    suspend fun removePointHistory(pointHistory: PointHistory)

    class DefaultDatabaseRepository @Inject constructor(
        private val gameDao: GameDao,
        private val playerDao: PlayerDao,
        private val pointHistoryDao: PointHistoryDao
    ) : DatabaseRepository {

        override var games: Flow<List<Game>> = gameDao.getAllGames()
        override var players: Flow<List<Player>> = playerDao.getAllPlayers()
        override val pointHistory: Flow<List<PointHistory>> =
            pointHistoryDao.getPointHistory()


        override suspend fun insertPlayer(player: Player) {
            playerDao.insertPlayer(player)
        }

        override suspend fun getPlayer(playerId: Long) : Player {
            return playerDao.getPlayer(playerId)
        }

        override suspend fun getPlayerFromGame(gameID: Long): Flow<List<PlayerModel>> {
            val players =  playerDao.getAllPlayersFromGame(gameID).map { players ->
                players.map { player ->
                    val pointHistory = getPointHistoryFromPlayerId(player.id).first()
                    //val totalPoints = pointHistory.sumBy { it.points }

                    var totalPoints = 0L
                    pointHistory.forEach {
                        totalPoints += it.point
                    }

                    PlayerModel(
                        id = player.id,
                        name =  player.name,
                        points = totalPoints,
                        phases = loadPhases(player.id, player.phases).first(),
                        pointHistory = pointHistory
                    )
                }
            }

            return players
        }

        private suspend fun loadPhases(playerId: Long, sPhases: String): Flow<List<PhasesModel>> {
            val phasesList = mutableListOf<PhasesModel>()
            for (i in 0..9) {
                val phase : Long = (i + 1).toLong()
                var sPhase = phase.toString()
                if (phase.toInt() == 1) {
                    sPhase = "$sPhase,"
                }
                val isChecked = sPhases.contains(sPhase)
                phasesList.add(PhasesModel(phase, isChecked, playerId))
            }
            return flow {
                emit(phasesList)
            }

        }

        override suspend fun deletePlayer(player: Player) {
            playerDao.deletePlayer(player)
        }

        override suspend fun updatePlayer(player: Player) {
            playerDao.updatePlayer(player)
            updateGameModifiedTimestamp(playerDao.getGameFromPlayerID(player.gameID))
        }

        override suspend fun changePlayerName(player: Player) {
            updatePlayer(player)
        }

        override suspend fun changePlayerPhase(playerId: Long, phase: Long, state: Boolean) {
            val player = getPlayer(playerId)
            var sPhase: String = phase.toString()
            var sReplace: String = ""

            if (phase.toInt() == 1) {
                sPhase = "$sPhase,"
                sReplace = ","
            }

            if (!state && player.phases.contains(sPhase)) {
                // remove phase from string
                player.phases = player.phases.replace(sPhase,sReplace)

                // ToDo: Remove duplicate ","

                updatePlayer(player)
            }
            else if (state && !player.phases.contains(sPhase)) {
                // add to string
                player.phases += ", $sPhase"

                updatePlayer(player)
            }
        }

        override suspend fun insertGame(game: Game) : Long {
            return gameDao.insertGame(game)
        }

        override suspend fun getGameFromId(gameID: Long): GameModel {
            val game = gameDao.getGameFromId(gameID)
            return GameModel(
                id = game.id,
                created = game.timestampCreated,
                modified = game.timestampModified,
                name =  game.name,
                players = getPlayerFromGame(game.id).first()
            )
        }

        override suspend fun getAllGames() : Flow<List<GameModel>> {
            return gameDao.getAllGames().map { games ->
                games.map { game ->
                    GameModel(
                        id = game.id,
                        created = game.timestampCreated,
                        modified = game.timestampModified,
                        name =  game.name,
                        players = getPlayerFromGame(game.id).first()
                    )
                }
            }
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

        override suspend fun getPointHistoryFromPlayerId(playerId: Long): Flow<List<PointHistoryModel>> {
            return pointHistoryDao.getAllPointsFromPlayer(playerId).map { pointHistory ->
                pointHistory.map { point ->
                    PointHistoryModel(
                        id = point.id,
                        playerId = point.playerID,
                        point = point.point,
                        created = point.timestampCreated
                    )
                }
            }
        }

        override suspend fun insertPointHistory(pointHistory: PointHistory) {
            pointHistoryDao.insertPoint(pointHistory)
            updateGameModifiedTimestamp(pointHistoryDao.getGameByPlayerId(pointHistory.playerID))
        }

        override suspend fun addPointHistory(playerId: Long, point: Long) {
            val pointHistory = PointHistory(
                point = point,
                playerID = playerId
            )
            insertPointHistory(pointHistory)
        }

        override suspend fun removePointHistory(pointHistory: PointHistory) {
            pointHistoryDao.deletePoint(pointHistory)
        }
    }
}
