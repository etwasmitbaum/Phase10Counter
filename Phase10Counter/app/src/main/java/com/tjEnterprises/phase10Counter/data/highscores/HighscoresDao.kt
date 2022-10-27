package com.tjEnterprises.phase10Counter.data.highscores

import androidx.room.*

@Dao
interface HighscoresDao {
    @Query("SELECT * FROM Highscores ORDER BY punkte ASC")
    fun getHighscoreList(): List<Highscores>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHighscore(highscores: Highscores)

    @Delete
    fun deleteHighscore(highscores: Highscores)
}