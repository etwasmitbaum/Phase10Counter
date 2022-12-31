package com.tjEnterprises.phase10Counter.data

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tjEnterprises.phase10Counter.data.highscores.Highscores
import com.tjEnterprises.phase10Counter.data.highscores.HighscoresDao
import com.tjEnterprises.phase10Counter.data.player.PlayerData
import com.tjEnterprises.phase10Counter.data.player.PlayerDataDao
import com.tjEnterprises.phase10Counter.data.pointHistory.PointHistory
import com.tjEnterprises.phase10Counter.data.pointHistory.PointHistoryDao

@Database(entities = [PlayerData::class, Highscores::class, PointHistory::class], version = 3, exportSchema = true, autoMigrations = [AutoMigration (from = 1, to = 2), AutoMigration(from = 2, to = 3)])
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun PlayerDataDao(): PlayerDataDao
    abstract fun HighscoresDao(): HighscoresDao
    abstract fun PointHistoryDao(): PointHistoryDao
}
