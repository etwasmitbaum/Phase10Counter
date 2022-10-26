package com.tjEnterprises.phase10Counter.data.highscores

import android.os.Parcelable
import androidx.room.*
import com.tjEnterprises.phase10Counter.data.player.PlayerData

@Dao
interface HighscoresDao {
    @Query("SELECT * FROM Highscores ORDER BY punkte ASC")
    fun getHighscoreList(): List<Highscores>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHighscore(highscores: Highscores)

    @Delete
    fun deleteHighscore(highscores: Highscores)
}