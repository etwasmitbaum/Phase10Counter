package com.tjEnterprises.phase10Counter.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tjEnterprises.phase10Counter.data.player.PlayerData
import com.tjEnterprises.phase10Counter.data.player.PlayerDataDao

@Database(entities = [PlayerData::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun PlayerDataDao(): PlayerDataDao
}
