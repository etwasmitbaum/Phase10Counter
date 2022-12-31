package com.tjEnterprises.phase10Counter.data.pointHistory

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PointHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPoint(pointHistory: PointHistory)

    @Query("SELECT point FROM PointHistory WHERE player_id IS (:playerNR) ORDER BY id DESC")
    fun getPointHistoryFromNewToOld(playerNR: Int): List<Int>

    @Query("DELETE FROM PointHistory")
    fun deletePointHistory()
}