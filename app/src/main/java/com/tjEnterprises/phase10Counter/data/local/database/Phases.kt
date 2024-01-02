package com.tjEnterprises.phase10Counter.data.local.database

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Entity(
    tableName = "Phases",
    foreignKeys = [ForeignKey(
        entity = Game::class,
        parentColumns = ["game_id"],
        childColumns = ["game_id"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    ), ForeignKey(
        entity = Player::class,
        parentColumns = ["player_id"],
        childColumns = ["player_id"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )],
    primaryKeys = ["game_id", "player_id", "phase"],
    indices = [Index(value = ["game_id"]), Index(value = ["player_id"])]
)
data class Phases(
    @ColumnInfo(name = "player_id") var playerId: Long,
    @ColumnInfo(name = "game_id") var gameId: Long,
    @ColumnInfo(name = "phase") var phaseNr: Byte,
    @ColumnInfo("open") var open: Boolean = true
) {
    @ColumnInfo("timestampModified")
    var timestampModified: Long = System.currentTimeMillis()
}

@Dao
interface PhasesDao {
    @Query("SELECT * FROM Phases WHERE player_id IS :playerId ORDER BY phase ASC")
    fun getPhasesOfPlayerAsFlow(playerId: Long): Flow<List<Phases>>

    @Query("SELECT * FROM Phases WHERE player_id IS :playerId ORDER BY phase ASC")
    fun getPhasesOfPlayer(playerId: Long): List<Phases>

    @Query("SELECT * FROM Phases WHERE game_id IS :gameId ORDER BY player_id ASC")
    fun getPhasesOfGame(gameId: Long): Flow<List<Phases>>

    @Query("SELECT * FROM Phases ORDER BY player_id ASC")
    fun getPhases(): Flow<List<Phases>>

    @Update
    suspend fun updatePhase(phases: Phases)

    @Insert
    suspend fun insertPhase(phases: Phases)

    @Delete
    suspend fun deletePhase(phases: Phases)
}