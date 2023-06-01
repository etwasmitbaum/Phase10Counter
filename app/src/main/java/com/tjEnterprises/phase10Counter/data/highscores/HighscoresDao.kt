package com.tjEnterprises.phase10Counter.data.highscores

import androidx.room.*

@Dao
@Deprecated("This is only kept, to not change the existing Player Database. All Highscores will be stored in \"GlobalHighscores\". This is part of the backup and restore functionality")
interface HighscoresDao {
    @Query("SELECT * FROM Highscores ORDER BY punkte ASC")
    fun getHighscoreList(): List<Highscores>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHighscore(highscores: Highscores)

    @Delete
    fun deleteHighscore(highscores: Highscores)
}