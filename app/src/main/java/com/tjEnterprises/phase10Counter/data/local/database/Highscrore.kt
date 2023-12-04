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
data class Highscrore(
    @ColumnInfo(name = "playerName") val playerName: String,
    @ColumnInfo(name = "points") val points: Long
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id")
    var id: Long = 0

    @ColumnInfo("timestamp")
    var timestamp: Long = System.currentTimeMillis()
}

@Dao
interface HighscoreDao {
    @Query("SELECT * FROM Highscrore ORDER BY points ASC")
    fun getAllHighscores(): Flow<List<Highscrore>>

    @Insert
    suspend fun insertHighscore(highscrore: Highscrore)

    @Delete
    suspend fun deleteHighscore(highscrore: Highscrore)
}