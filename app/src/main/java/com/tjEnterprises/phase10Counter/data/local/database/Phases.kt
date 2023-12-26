package com.tjEnterprises.phase10Counter.data.local.database

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Insert
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
    ), ForeignKey(
        entity = Player::class,
        parentColumns = ["player_id"],
        childColumns = ["player_id"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )], primaryKeys = ["game_id", "player_id", "phase"]
)
data class Phases(
    @ColumnInfo(name = "player_id") var playerId: Long,
    @ColumnInfo(name = "game_id") var gameId: Long,
    @ColumnInfo(name = "phase") var phaseNr: Byte
) {
    @ColumnInfo("open")
    var open: Boolean = true

    @ColumnInfo("timestampModified")
    var timestampModified: Long = System.currentTimeMillis()
}

@Dao
interface PhasesDao {
    @Query("SELECT * FROM Phases WHERE player_id IS :playerId ORDER BY phase ASC")
    fun getPhasesOfPlayer(playerId: Long): Flow<List<Phases>>

    @Update
    suspend fun updatePhase(phases: Phases)

    @Insert
    suspend fun insertPhase(phases: Phases)

    @Delete
    suspend fun deletePhase(phases: Phases)
}