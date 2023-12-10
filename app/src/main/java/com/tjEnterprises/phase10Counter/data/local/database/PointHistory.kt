package com.tjEnterprises.phase10Counter.data.local.database

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity
data class PointHistory(
    @ColumnInfo(name = "point") var point: Long,
    @ColumnInfo(name = "player_id") var playerID: Long,
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id")
    var id: Long = 0

    @ColumnInfo("timestampCreated")
    var timestampCreated: Long = System.currentTimeMillis()
}

@Dao
interface PoinHistoryDao {
    @Query("SELECT * FROM PointHistory WHERE player_id IS (:playerID) ORDER BY id DESC")
    suspend fun getAllPointsFromPlayer(playerID: Long): List<PointHistory>

    @Query("SELECT * FROM PointHistory ORDER BY id DESC")
    fun getPointHistory(): Flow<List<PointHistory>>

    @Query("SELECT Game.* FROM Game INNER JOIN Player ON Game.id = Player.game_id WHERE Player.id = :playerId")
    suspend fun getGameByPlayerId(playerId: Long): Game

    @Insert
    suspend fun insertPoint(pointHistory: PointHistory)

    @Delete
    suspend fun deletePoint(pointHistory: PointHistory)
}