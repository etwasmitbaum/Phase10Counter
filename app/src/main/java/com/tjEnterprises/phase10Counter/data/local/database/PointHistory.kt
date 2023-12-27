package com.tjEnterprises.phase10Counter.data.local.database

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
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
    )]
)
data class PointHistory(
    @ColumnInfo(name = "point") var point: Long,
    @ColumnInfo(name = "player_id") var playerId: Long,
    @ColumnInfo(name = "game_id") var gameId: Long
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("pointId")
    var pointId: Long = 0

    @ColumnInfo("timestampCreated")
    var timestampCreated: Long = System.currentTimeMillis()
}

@Dao
interface PoinHistoryDao {
    @Query("SELECT * FROM PointHistory ORDER BY timestampCreated DESC")
    fun getPointHistory(): Flow<List<PointHistory>>

    @Query("SELECT * FROM PointHistory WHERE game_id IS :gameId ORDER BY timestampCreated DESC")
    fun getPointHistoryOfGame(gameId: Long): Flow<List<PointHistory>>

    @Query("SELECT * FROM PointHistory WHERE pointId IS :pointHistoryId")
    suspend fun getPointHistoryFromId(pointHistoryId: Long): PointHistory

    @Query("SELECT * FROM PointHistory WHERE player_id IS :playerId")
    suspend fun getPointHistoryOfPlayer(playerId: Long): List<PointHistory>

    @Insert
    suspend fun insertPoint(pointHistory: PointHistory)

    @Delete
    suspend fun deletePoint(pointHistory: PointHistory)
}