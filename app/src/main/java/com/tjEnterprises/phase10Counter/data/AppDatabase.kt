package com.tjEnterprises.phase10Counter.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tjEnterprises.phase10Counter.data.highscores.Highscores
import com.tjEnterprises.phase10Counter.data.highscores.HighscoresDao
import com.tjEnterprises.phase10Counter.data.player.PlayerData
import com.tjEnterprises.phase10Counter.data.player.PlayerDataDao
import com.tjEnterprises.phase10Counter.data.pointHistory.PointHistory
import com.tjEnterprises.phase10Counter.data.pointHistory.PointHistoryDao

@Database(
    entities = [PlayerData::class, Highscores::class, PointHistory::class],
    version = 3,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun PlayerDataDao(): PlayerDataDao
    abstract fun HighscoresDao(): HighscoresDao
    abstract fun PointHistoryDao(): PointHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java, "Database"
            ).fallbackToDestructiveMigration().allowMainThreadQueries().addMigrations(
                MigrationHelper.MIGRATION_1_2, MigrationHelper.MIGRATION_2_3
            ).build()
        }
    }
}
