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

import com.tjEnterprises.phase10Counter.data.local.GameModel
import com.tjEnterprises.phase10Counter.data.local.PlayerModel
import com.tjEnterprises.phase10Counter.data.local.database.Game
import com.tjEnterprises.phase10Counter.data.local.database.GameDao
import com.tjEnterprises.phase10Counter.data.local.database.Phases
import com.tjEnterprises.phase10Counter.data.local.database.PhasesDao
import com.tjEnterprises.phase10Counter.data.local.database.Player
import com.tjEnterprises.phase10Counter.data.local.database.PlayerDao
import com.tjEnterprises.phase10Counter.data.local.database.PoinHistoryDao
import com.tjEnterprises.phase10Counter.data.local.database.PointHistory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

interface DatabaseRepository {
    val games: Flow<List<GameModel>>

    suspend fun insertPlayer(player: Player): Long
    suspend fun getPlayerFromGame(gameId: Long): Flow<List<PlayerModel>>
    suspend fun deletePlayer(player: Player)
    suspend fun updatePlayer(player: Player)
    suspend fun changePlayerName(player: Player)
    suspend fun updatePlayerPhases(playerId: Long, gameId: Long, openPhases: List<Boolean>)
    suspend fun insertPhasesForPlayer(playerId: Long, gameId: Long)
    suspend fun insertGame(game: Game): Long
    suspend fun getGameFromId(gameID: Long): Game
    suspend fun removeGame(game: Game)
    suspend fun updateGameModifiedTimestamp(game: Game)
    suspend fun getPointHistoryOfGame(gameId: Long): Flow<List<PointHistory>>
    suspend fun insertPointHistory(pointHistory: PointHistory)
    suspend fun removePointHistory(pointHistory: PointHistory)

    class DefaultDatabaseRepository @Inject constructor(
        private val gameDao: GameDao,
        private val playerDao: PlayerDao,
        private val pointHistoryDao: PoinHistoryDao,
        private val phasesDao: PhasesDao
    ) : DatabaseRepository {

        override var games: Flow<List<GameModel>> = combine(gameDao.getAllGames(),
            playerDao.getAllPlayers(), pointHistoryDao.getPointHistory(), phasesDao.getPhases()
        ) { games, players, pointHistory, phases ->
            val gameModels: MutableList<GameModel> = mutableListOf()

            games.forEach { game ->
                val gameId = game.gameId
                val gameName = game.name
                val gameCreated = game.timestampCreated
                val gameModified = game.timestampModified
                val playerModels: MutableList<PlayerModel> = mutableListOf()

                players.filter{ it.gameID == gameId }.forEach { player ->
                    val pointHistoryPlayer: MutableList<Long> = mutableListOf()
                    var pointSum = 0L
                    val phasesOpen: MutableList<Boolean> = mutableListOf()

                    pointHistory.filter { it.playerId == player.playerId }.forEach {
                        pointSum += it.point
                        pointHistoryPlayer.add(it.pointId)
                    }

                    phases.filter { it.playerId == player.playerId }.forEach {
                        phasesOpen.add(it.open)
                    }
                    playerModels.add(
                        PlayerModel(
                            player.playerId,
                            gameId,
                            player.name,
                            pointHistoryPlayer,
                            pointSum,
                            phasesOpen
                        )
                    )
                }
                gameModels.add(GameModel(gameId, gameName, gameCreated, gameModified, playerModels))
            }
            gameModels
        }

        override suspend fun insertPlayer(player: Player): Long {
            return playerDao.insertPlayer(player)
        }

        override suspend fun getPlayerFromGame(gameId: Long): Flow<List<PlayerModel>> {
            val players: Flow<List<PlayerModel>> = combine(
                playerDao.getAllPlayersFromGame(gameId),
                pointHistoryDao.getPointHistoryOfGame(gameId),
                phasesDao.getPhasesOfGame(gameId = gameId)
            ) { playersList, pointHistoryList, phasesList ->
                val playerModels: MutableList<PlayerModel> = mutableListOf()
                playersList.forEach { player ->
                    val pointHistoryPlayer: MutableList<Long> = mutableListOf()
                    var pointSum = 0L
                    val phasesOpen: MutableList<Boolean> = mutableListOf()

                    pointHistoryList.filter { it.playerId == player.playerId }.forEach {
                        pointSum += it.point
                        pointHistoryPlayer.add(it.pointId)
                    }

                    phasesList.filter { it.playerId == player.playerId }.forEach {
                        phasesOpen.add(it.open)
                    }
                    playerModels.add(
                        PlayerModel(
                            player.playerId,
                            gameId,
                            player.name,
                            pointHistoryPlayer,
                            pointSum,
                            phasesOpen
                        )
                    )
                }
                playerModels
            }
            return players
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

        override suspend fun updatePlayerPhases(
            playerId: Long, gameId: Long, openPhases: List<Boolean>
        ) {
            openPhases.forEachIndexed { idx, open ->
                val phase = Phases(
                    playerId = playerId, gameId = gameId, phaseNr = idx.toByte(), open = open
                )
                phase.timestampModified = System.currentTimeMillis()
                phasesDao.updatePhase(phase)
            }
        }

        override suspend fun insertPhasesForPlayer(playerId: Long, gameId: Long) {
            for (i in 0..9) {
                phasesDao.insertPhase(Phases(playerId, gameId, i.toByte()))
            }
        }

        override suspend fun insertGame(game: Game): Long {
            return gameDao.insertGame(game)
        }

        override suspend fun getGameFromId(gameID: Long): Game {
            return gameDao.getGameFromId(gameID)
        }

        override suspend fun removeGame(game: Game) {
            gameDao.deleteGame(game)
        }

        override suspend fun updateGameModifiedTimestamp(game: Game) {
            game.timestampModified = System.currentTimeMillis()
            gameDao.updateGame(game)
        }

        override suspend fun getPointHistoryOfGame(gameId: Long): Flow<List<PointHistory>> {
            return pointHistoryDao.getPointHistoryOfGame(gameId)
        }

        override suspend fun insertPointHistory(pointHistory: PointHistory) {
            pointHistoryDao.insertPoint(pointHistory)
            updateGameModifiedTimestamp(gameDao.getGameFromId(pointHistory.gameId))
        }

        override suspend fun removePointHistory(pointHistory: PointHistory) {
            pointHistoryDao.deletePoint(pointHistory)
        }
    }
}
