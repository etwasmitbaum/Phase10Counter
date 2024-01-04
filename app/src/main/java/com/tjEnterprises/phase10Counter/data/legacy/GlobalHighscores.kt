package com.tjEnterprises.phase10Counter.data.legacy

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import java.util.Date

@Entity
data class GlobalHighscores(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "playerName") val playerName: String,
    @ColumnInfo(name = "punkte") val punkte: Int,
    @ColumnInfo(name = "date") val date: Date
)

@Dao
interface GlobalHighscoresDao {
    @Query("SELECT * FROM GlobalHighscores ORDER BY id ASC")
    fun getHighscoreList(): List<GlobalHighscores>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHighscore(highscores: GlobalHighscores)

    @Delete
    fun deleteHighscore(highscores: GlobalHighscores)
}