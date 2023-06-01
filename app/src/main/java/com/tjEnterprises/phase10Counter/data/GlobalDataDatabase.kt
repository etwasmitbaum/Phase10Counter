package com.tjEnterprises.phase10Counter.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tjEnterprises.phase10Counter.data.globalHighscores.GlobalHighscores
import com.tjEnterprises.phase10Counter.data.globalHighscores.GlobalHighscoresDao

@Database(
    entities = [GlobalHighscores::class],
    version = 1,
    exportSchema = true
)

@TypeConverters(Converters::class)
abstract class GlobalDataDatabase : RoomDatabase() {
    abstract fun GlobalHighscoresDao(): GlobalHighscoresDao

    companion object {
        @Volatile
        private var INSTANCE: GlobalDataDatabase? = null

        fun getInstance(context: Context): GlobalDataDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context): GlobalDataDatabase {
            return Room.databaseBuilder(
                context,
                GlobalDataDatabase::class.java, "GlobalDataDatabase"
            ).fallbackToDestructiveMigration().allowMainThreadQueries().build()
        }
    }
}
