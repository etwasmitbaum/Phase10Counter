package com.tjEnterprises.phase10Counter.data.local.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class MigrationHelper {
    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE `Highscores` (`id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, `playerName` TEXT NOT NULL, `punkte` INTEGER NOT NULL, `date` INTEGER NOT NULL)")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE `PointHistory` (`id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, `player_id` INTEGER NOT NULL, `point` INTEGER NOT NULL)")
            }
        }

        val MIGRATION_3_4 = object : Migration(startVersion = 3, endVersion = 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                TODO("Implement DB migration")
            }
        }
    }
}
