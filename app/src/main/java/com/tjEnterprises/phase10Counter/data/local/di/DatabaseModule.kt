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

package com.tjEnterprises.phase10Counter.data.local.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tjEnterprises.phase10Counter.data.local.database.AppDatabase
import com.tjEnterprises.phase10Counter.data.local.database.GameDao
import com.tjEnterprises.phase10Counter.data.local.database.HighscoreDao
import com.tjEnterprises.phase10Counter.data.local.database.Migration2To3
import com.tjEnterprises.phase10Counter.data.local.database.Migration3To4
import com.tjEnterprises.phase10Counter.data.local.database.Migration4To5
import com.tjEnterprises.phase10Counter.data.local.database.MigrationHelper
import com.tjEnterprises.phase10Counter.data.local.database.PhasesDao
import com.tjEnterprises.phase10Counter.data.local.database.PlayerDao
import com.tjEnterprises.phase10Counter.data.local.database.PointHistoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    fun provideGameDao(appDatabase: AppDatabase): GameDao {
        return appDatabase.GameDao()
    }

    @Provides
    fun providePlayerDao(appDatabase: AppDatabase): PlayerDao {
        return appDatabase.PlayerDao()
    }

    @Provides
    fun providePointHistoryDao(appDatabase: AppDatabase): PointHistoryDao {
        return appDatabase.PoinHistoryDao()
    }

    @Provides
    fun provideHighscoreDao(appDatabase: AppDatabase): HighscoreDao {
        return appDatabase.HighscoreDao()
    }

    @Provides
    fun providePhasesDao(appDatabase: AppDatabase): PhasesDao {
        return appDatabase.PhasesDao()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext, AppDatabase::class.java, AppDatabase.getName()
        ).addMigrations(
            MigrationHelper.MIGRATION_1_2,
            Migration2To3(context = appContext),
            Migration3To4(context = appContext),
            Migration4To5()
        ).setJournalMode(
            RoomDatabase.JournalMode.TRUNCATE // set to TRUNCATE, so all is always stored in a single file to easier make backups
        ).build()
    }
}
