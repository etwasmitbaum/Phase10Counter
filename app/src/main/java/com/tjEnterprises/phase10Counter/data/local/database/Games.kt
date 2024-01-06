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

package com.tjEnterprises.phase10Counter.data.local.database

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Entity
data class Game(
    @ColumnInfo("name") val name: String
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("game_id")
    var gameId: Long = 0

    @ColumnInfo("timestampCreated")
    var timestampCreated: Long = System.currentTimeMillis()

    @ColumnInfo("timestampModified")
    var timestampModified: Long = System.currentTimeMillis()
}

@Dao
interface GameDao {
    @Query("SELECT * FROM Game ORDER BY game_id DESC")
    fun getAllGames(): Flow<List<Game>>

    @Query("SELECT * FROM Game WHERE game_id IS :gameId")
    suspend fun getGameFromId(gameId: Long): Game

    @Query("SELECT * FROM Game WHERE game_id IS :gameId")
    fun getGameFromIdAsFlow(gameId: Long): Flow<Game>

    @Query("SELECT COUNT(*) FROM Game")
    suspend fun getGamesCount(): Long

    @Insert
    suspend fun insertGame(game: Game): Long

    @Update
    suspend fun updateGame(game: Game)

    @Delete
    suspend fun deleteGame(game: Game)
}
