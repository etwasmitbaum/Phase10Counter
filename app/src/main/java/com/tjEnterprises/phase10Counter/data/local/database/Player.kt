package com.tjEnterprises.phase10Counter.data.local.database

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Entity(
    foreignKeys = [ForeignKey(
        entity = Game::class,
        parentColumns = ["game_id"],
        childColumns = ["game_id"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )]
)
data class Player(
    @ColumnInfo("game_id") val gameID: Long,
    @ColumnInfo("name") val name: String,
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("player_id")
    val playerId: Long = 0
) {}

@Dao
interface PlayerDao {
    @Query("SELECT * FROM Player ORDER BY player_id ASC")
    fun getAllPlayers(): Flow<List<Player>>

    @Query("SELECT * FROM Player WHERE game_id IS (:gameID) ORDER BY player_id ASC")
    fun getAllPlayersFromGame(gameID: Long): Flow<List<Player>>

    @Query("SELECT * FROM Game WHERE game_id IS (:gameIDofPlayer)")
    suspend fun getGameFromPlayerID(gameIDofPlayer: Long): Game

    @Query("SELECT * FROM Player WHERE player_id IS (:playerId)")
    suspend fun getPlayerFromId(playerId: Long): Player

    @Update
    suspend fun updatePlayer(player: Player)

    @Insert
    suspend fun insertPlayer(player: Player): Long

    @Delete
    suspend fun deletePlayer(player: Player)
}