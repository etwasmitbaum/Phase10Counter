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
data class Highscore(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id")
    var id: Long = 0,
    @ColumnInfo(name = "playerName") val playerName: String,
    @ColumnInfo(name = "points") val points: Long,
    @ColumnInfo("timestamp")
    var timestamp: Long = System.currentTimeMillis()
)

@Dao
interface HighscoreDao {
    @Query("SELECT * FROM Highscore ORDER BY points ASC")
    fun getAllHighscores(): Flow<List<Highscore>>

    @Query("SELECT * FROM Highscore WHERE id IS :highscoreId")
    fun getHighscore(highscoreId: Long): Highscore

    @Insert
    suspend fun insertHighscore(highscore: Highscore)

    @Delete
    suspend fun deleteHighscore(highscore: Highscore)
}