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
data class Player(
    @PrimaryKey(autoGenerate = true) @ColumnInfo("id") val id: Long = 0,
    @ColumnInfo("name") val name: String,
    @ColumnInfo("game_id") val gameID: Long,
    @ColumnInfo(name = "phases") var phases: String = "1, 2, 3, 4, 5, 6, 7, 8, 9, 10"
) {}

@Dao
interface PlayerDao {
    @Query("SELECT * FROM Player ORDER BY id ASC")
    fun getAllPlayers(): Flow<List<Player>>

    @Query("SELECT * FROM Player WHERE game_id IS (:gameID) ORDER BY id ASC")
    fun getAllPlayersFromGame(gameID: Long): Flow<List<Player>>

    @Query("SELECT * FROM Player WHERE id IS (:playerId) ORDER BY id ASC")
    fun getPlayer(playerId: Long): Player

    @Query("SELECT * FROM Game WHERE id IS (:gameIDofPlayer)")
    suspend fun getGameFromPlayerID(gameIDofPlayer: Long): Game

    @Update
    suspend fun updatePlayer(player: Player)

    @Insert
    suspend fun insertPlayer(player: Player)

    @Delete
    suspend fun deletePlayer(player: Player)
}