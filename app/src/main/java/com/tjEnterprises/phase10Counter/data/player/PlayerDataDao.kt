package com.tjEnterprises.phase10Counter.data.player

import androidx.room.*

@Dao
interface PlayerDataDao {
    @Query("SELECT * FROM PlayerData WHERE id IS (:id) ORDER BY id ASC")
    fun getSinglePlayer(id: Int): PlayerData

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPlayerData(pData: PlayerData)

    @Delete
    fun deletePlayer(pData: PlayerData)
}