package com.tjEnterprises.phase10Counter.data.player

import androidx.room.*
import com.tjEnterprises.phase10Counter.Player

@Dao
interface PlayerDataDao {
    @Query("SELECT * FROM PlayerData WHERE id IS (:id) ORDER BY id ASC")
    fun getSinglePlayer(id: Int): PlayerData

    @Query("SELECT * FROM PlayerData ORDER BY id ASC")
    fun getAllPlayers(): MutableList<Player> = ArrayList()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPlayerData(pData: PlayerData)

    @Update
    fun updatePlayerData(pData: PlayerData)

    @Delete
    fun deletePlayer(pData: PlayerData)
}