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

package com.tjEnterprises.phase10Counter.testdi

import com.tjEnterprises.phase10Counter.data.di.DataModule
import com.tjEnterprises.phase10Counter.data.di.FakeDatabaseRepository
import com.tjEnterprises.phase10Counter.data.local.repositories.DatabaseRepository
import com.tjEnterprises.phase10Counter.data.local.repositories.SettingsRepository
import com.tjEnterprises.phase10Counter.data.network.repositories.UpdateCheckerRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DataModule::class]
)
interface FakeDataModule {

    @Binds
    abstract fun bindRepository(
        fakeRepository: FakeDatabaseRepository
    ): DatabaseRepository

    @Binds
    fun bindsSettingsRepository(settingsRepository: SettingsRepository.SettingsRepositoryImpl): SettingsRepository

    @Binds
    fun bindsUpdateCheckerRepository(updateCheckerRepository: UpdateCheckerRepository.UpdateCheckerRepositoryImpl) : UpdateCheckerRepository

}
