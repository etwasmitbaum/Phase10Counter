package com.tjEnterprises.phase10Counter.data.globalHighscores

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface GlobalHighscoresDao {
    @Query("SELECT * FROM GlobalHighscores ORDER BY punkte ASC")
    fun getHighscoreList(): List<GlobalHighscores>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHighscore(highscores: GlobalHighscores)

    @Delete
    fun deleteHighscore(highscores: GlobalHighscores)
}