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

package com.tjEnterprises.phase10Counter.ui.database


import com.tjEnterprises.phase10Counter.data.local.repositories.DatabaseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class DatabaseViewModelTest {
    @Test
    fun uiState_initiallyLoading() = runTest {
        val viewModel = DatabaseViewModel(FakeDatabaseRepository())
        assertEquals(viewModel.uiState.first(), DatabaseUiState.Loading)
    }

    @Test
    fun uiState_onItemSaved_isDisplayed() = runTest {
        val viewModel = DatabaseViewModel(FakeDatabaseRepository())
        assertEquals(viewModel.uiState.first(), DatabaseUiState.Loading)
    }
}

private class FakeDatabaseRepository : DatabaseRepository {

    private val data = mutableListOf<String>()

    override val databases: Flow<List<String>>
        get() = flow { emit(data.toList()) }

    override suspend fun add(name: String) {
        data.add(0, name)
    }
}
