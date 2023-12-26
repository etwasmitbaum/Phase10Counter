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
import com.tjEnterprises.phase10Counter.data.local.database.AppDatabase
import com.tjEnterprises.phase10Counter.data.local.database.GameDao
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
    fun providePlayerDao(appDatabase: AppDatabase): PlayerDao{
        return appDatabase.PlayerDao()
    }

    @Provides
    fun providePointHistoryDao(appDatabase: AppDatabase): PointHistoryDao{
        return appDatabase.PointHistoryDao()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "GameDatabase"
        ).build()
    }
}
