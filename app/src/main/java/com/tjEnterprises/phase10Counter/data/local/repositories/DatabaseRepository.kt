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

package com.tjEnterprises.phase10Counter.data.local.repositories

import com.tjEnterprises.phase10Counter.data.local.database.Game
import com.tjEnterprises.phase10Counter.data.local.database.GameDao
import com.tjEnterprises.phase10Counter.data.local.database.Highscore
import com.tjEnterprises.phase10Counter.data.local.database.HighscoreDao
import com.tjEnterprises.phase10Counter.data.local.database.Phases
import com.tjEnterprises.phase10Counter.data.local.database.PhasesDao
import com.tjEnterprises.phase10Counter.data.local.database.Player
import com.tjEnterprises.phase10Counter.data.local.database.PlayerDao
import com.tjEnterprises.phase10Counter.data.local.database.PointHistory
import com.tjEnterprises.phase10Counter.data.local.database.PointHistoryDao
import com.tjEnterprises.phase10Counter.data.local.models.GameModel
import com.tjEnterprises.phase10Counter.data.local.models.PlayerModel
import com.tjEnterprises.phase10Counter.data.local.models.PointHistoryItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

interface DatabaseRepository {
    val games: Flow<List<GameModel>>
    val highscores: Flow<List<Highscore>>

    suspend fun insertPlayer(playerName: String, gameId: Long): Long
    fun getPlayersFlowFromGame(gameId: Long): Flow<List<PlayerModel>>
    suspend fun getPlayersFromGame(gameId: Long): List<PlayerModel>
    suspend fun deletePlayer(playerId: Long)

    suspend fun updatePlayerPhases(playerId: Long, gameId: Long, openPhases: List<Boolean>)
    suspend fun getPhasesOfPlayer(playerId: Long): List<Phases>

    fun getGameFlowFromId(gameId: Long): Flow<GameModel>
    suspend fun getGameFromId(gameId: Long): GameModel
    suspend fun insertGame(gameName: String): Long
    suspend fun deleteGame(game: Game)
    suspend fun deleteGame(gameId: Long)
    suspend fun updateGameModifiedTimestamp(gameId: Long)

    fun getPointHistoryOfGameAsFlow(gameId: Long): Flow<List<PointHistory>>
    suspend fun getGamesCount(): Long
    suspend fun getPointHistoryOfPlayer(playerId: Long): List<PointHistory>
    suspend fun insertPointHistory(point: Long, gameId: Long, playerId: Long)
    suspend fun updatePointHistoryEntry(pointHistoryItem: PointHistoryItem)
    suspend fun deletePointHistoryOfPlayer(playerId: Long)
    suspend fun deletePointHistoryEntry(pointHistoryItem: PointHistoryItem)

    suspend fun insertHighscore(playerName: String, point: Long, timeStamp: Long = -1L)
    suspend fun deleteHighScore(highscoreId: Long)

    class DefaultDatabaseRepository @Inject constructor(
        private val gameDao: GameDao,
        private val playerDao: PlayerDao,
        private val pointHistoryDao: PointHistoryDao,
        private val phasesDao: PhasesDao,
        private val highscoreDao: HighscoreDao,
    ) : DatabaseRepository {

        override val games: Flow<List<GameModel>> = combine(
            gameDao.getAllGames(),
            playerDao.getAllPlayers(),
            pointHistoryDao.getPointHistory(),
            phasesDao.getPhases()
        ) { games, players, pointHistory, phases ->
            val gameModels: MutableList<GameModel> = mutableListOf()

            games.forEach { game ->
                val gameId = game.gameId
                val gameName = game.name
                val gameCreated = game.timestampCreated
                val gameModified = game.timestampModified
                val playerModels: MutableList<PlayerModel> = mutableListOf()

                players.filter { it.gameID == gameId }.forEach { player ->
                    val pointHistoryPlayer: MutableList<PointHistoryItem> = mutableListOf()
                    var pointSum = 0L
                    val phasesOpen: MutableList<Boolean> = mutableListOf()

                    pointHistory.filter { it.playerId == player.playerId }.forEach {
                        pointSum += it.point
                        pointHistoryPlayer.add(PointHistoryItem(it.point, it.pointId))
                    }

                    phases.filter { it.playerId == player.playerId }.forEach {
                        phasesOpen.add(it.open)
                    }
                    playerModels.add(
                        PlayerModel(
                            playerId = player.playerId,
                            gameId = gameId,
                            name = player.name,
                            pointHistory = pointHistoryPlayer,
                            pointSum = pointSum,
                            phasesOpen = phasesOpen
                        )
                    )
                }
                gameModels.add(GameModel(gameId, gameName, gameCreated, gameModified, playerModels))
            }
            gameModels
        }

        override val highscores = highscoreDao.getAllHighscores()

        override suspend fun insertPlayer(playerName: String, gameId: Long): Long {
            val playerId = playerDao.insertPlayer(Player(gameID = gameId, name = playerName))
            insertPhasesForPlayer(playerId = playerId, gameId = gameId)

            return playerId
        }

        override fun getPlayersFlowFromGame(gameId: Long): Flow<List<PlayerModel>> {
            val players: Flow<List<PlayerModel>> = combine(
                playerDao.getAllPlayersFromGameAsFlow(gameId),
                pointHistoryDao.getPointHistoryOfGame(gameId),
                phasesDao.getPhasesOfGame(gameId = gameId)
            ) { playersList, pointHistoryList, phasesList ->
                val playerModels: MutableList<PlayerModel> = mutableListOf()
                playersList.forEach { player ->
                    val pointHistoryPlayer: MutableList<PointHistoryItem> = mutableListOf()
                    var pointSum = 0L
                    val phasesOpen: MutableList<Boolean> = mutableListOf()

                    pointHistoryList.filter { it.playerId == player.playerId }.forEach {
                        pointSum += it.point
                        pointHistoryPlayer.add(PointHistoryItem(it.point, it.pointId))
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

        override suspend fun getPlayersFromGame(gameId: Long): List<PlayerModel> {
            val players = playerDao.getAllPlayersFromGame(gameId)
            val playerModels: MutableList<PlayerModel> = mutableListOf()

            players.forEach { player ->
                val pointHistory = pointHistoryDao.getPointHistoryOfPlayer(player.playerId)
                val pointHistoryPlayer: MutableList<PointHistoryItem> = mutableListOf()
                var sum = 0L
                pointHistory.forEach { pointHistoryEntry ->
                    pointHistoryPlayer.add(PointHistoryItem(pointHistoryEntry.point, pointHistoryEntry.pointId))
                    sum += pointHistoryEntry.point
                }

                val phases = getPhasesOfPlayer(playerId = player.playerId)
                val phasesOfPlayerOpen: MutableList<Boolean> = mutableListOf()
                phases.forEach { phase ->
                    phasesOfPlayerOpen.add(phase.open)
                }

                playerModels.add(
                    PlayerModel(
                        playerId = player.playerId,
                        gameId = gameId,
                        name = player.name,
                        pointHistory = pointHistoryPlayer,
                        pointSum = sum,
                        phasesOpen = phasesOfPlayerOpen
                )
                )
            }

            return playerModels
        }

        override suspend fun deletePlayer(playerId: Long) {
            playerDao.deletePlayer(playerDao.getPlayerFromId(playerId = playerId))
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

        private suspend fun insertPhasesForPlayer(playerId: Long, gameId: Long) {
            for (i in 0..9) {
                phasesDao.insertPhase(Phases(playerId, gameId, i.toByte()))
            }
        }

        override suspend fun getPhasesOfPlayer(playerId: Long): List<Phases> {
            return phasesDao.getPhasesOfPlayer(playerId)
        }

        override fun getGameFlowFromId(gameId: Long): Flow<GameModel> {
                val gameModel: Flow<GameModel> = combine(
                    gameDao.getGameFromIdAsFlow(gameId),
                    getPlayersFlowFromGame(gameId)
                ) { game, playersFromGame ->
                    // if you reset the game and then quickly delete it,
                    // a nullPointerException is thrown
                    try {
                        val gameModels = GameModel(
                            gameId = gameId,
                            name = game.name,
                            created = game.timestampCreated,
                            modified = game.timestampModified,
                            players = playersFromGame
                        )
                        gameModels
                    } catch (npe: NullPointerException){
                        npe.printStackTrace()
                        GameModel(-1L, "Error Game", 0L, 0L, emptyList())
                    }
                }
                return gameModel
        }

        override suspend fun getGameFromId(gameId: Long): GameModel {
            val game = gameDao.getGameFromId(gameId)
            return GameModel(
                gameId = game.gameId,
                name = game.name,
                created = game.timestampCreated,
                modified = game.timestampModified,
                players = getPlayersFromGame(gameId)
            )
        }

        override suspend fun insertGame(gameName: String): Long {
            return gameDao.insertGame(Game(name = gameName))
        }

        override suspend fun deleteGame(game: Game) {
            gameDao.deleteGame(game)
        }

        override suspend fun deleteGame(gameId: Long) {
            deleteGame(gameDao.getGameFromId(gameId))
        }

        override suspend fun updateGameModifiedTimestamp(gameId: Long) {
            val game = gameDao.getGameFromId(gameId)
            game.timestampModified = System.currentTimeMillis()
            gameDao.updateGame(game)
        }

        override fun getPointHistoryOfGameAsFlow(gameId: Long): Flow<List<PointHistory>> {
            return pointHistoryDao.getPointHistoryOfGame(gameId)
        }

        override suspend fun getGamesCount(): Long {
            return gameDao.getGamesCount()
        }

        override suspend fun getPointHistoryOfPlayer(playerId: Long): List<PointHistory> {
            return pointHistoryDao.getPointHistoryOfPlayer(playerId)
        }

        override suspend fun insertPointHistory(point: Long, gameId: Long, playerId: Long) {
            pointHistoryDao.insertPoint(
                PointHistory(
                    point = point, playerId = playerId, gameId = gameId
                )
            )
            updateGameModifiedTimestamp(gameId)
        }

        override suspend fun updatePointHistoryEntry(pointHistoryItem: PointHistoryItem) {
            val newPointHistory = pointHistoryDao.getPointHistoryFromId(pointHistoryItem.pointId)
            newPointHistory.point = pointHistoryItem.point
            pointHistoryDao.updatePoint(newPointHistory)
        }

        override suspend fun deletePointHistoryOfPlayer(playerId: Long) {
            pointHistoryDao.getPointHistoryOfPlayer(playerId = playerId).forEach {
                pointHistoryDao.deletePoint(it)
            }
        }

        override suspend fun deletePointHistoryEntry(pointHistoryItem: PointHistoryItem) {
            pointHistoryDao.deletePoint(pointHistoryDao.getPointHistoryFromId(pointHistoryItem.pointId))
        }

        override suspend fun insertHighscore(playerName: String, point: Long, timeStamp: Long) {
            if (timeStamp != -1L) {
                highscoreDao.insertHighscore(Highscore(playerName = playerName, points = point, timestamp = timeStamp))
            } else {
                highscoreDao.insertHighscore(Highscore(playerName = playerName, points = point))
            }

        }

        override suspend fun deleteHighScore(highscoreId: Long) {
            highscoreDao.deleteHighscore(highscoreDao.getHighscore(highscoreId))
        }

    }
}
