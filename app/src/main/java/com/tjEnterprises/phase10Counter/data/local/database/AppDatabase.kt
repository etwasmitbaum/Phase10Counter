/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tjEnterprises.phase10Counter.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Game::class, Player::class, PointHistory::class, Highscore::class, Phases::class],
    version = 5,
    exportSchema = true
    )

abstract class AppDatabase : RoomDatabase() {
    abstract fun GameDao(): GameDao
    abstract fun PlayerDao(): PlayerDao
    abstract fun PoinHistoryDao(): PointHistoryDao
    abstract fun PhasesDao(): PhasesDao
    abstract fun HighscoreDao(): HighscoreDao

    companion object {
        fun getName(): String{
            return "Database"
        }
    }
}
