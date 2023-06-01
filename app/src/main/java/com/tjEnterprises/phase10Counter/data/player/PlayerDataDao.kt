package com.tjEnterprises.phase10Counter.data.player

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PlayerDataDao {
    @Query("SELECT * FROM PlayerData WHERE id IS (:id) ORDER BY id ASC")
    fun getSinglePlayer(id: Int): PlayerData

    @Query("Select COUNT(id) FROM PlayerData ORDER BY id ASC")
    fun getPlayerCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPlayerData(pData: PlayerData)

    @Delete
    fun deletePlayer(pData: PlayerData)
}