package com.tjEnterprises.phase10Counter.data

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tjEnterprises.phase10Counter.data.highscores.Highscores
import com.tjEnterprises.phase10Counter.data.highscores.HighscoresDao
import com.tjEnterprises.phase10Counter.data.player.PlayerData
import com.tjEnterprises.phase10Counter.data.player.PlayerDataDao

@Database(entities = [PlayerData::class, Highscores::class], version = 2, exportSchema = true, autoMigrations = [AutoMigration (from = 1, to = 2)])
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun PlayerDataDao(): PlayerDataDao
    abstract fun HighscoresDao(): HighscoresDao
}
