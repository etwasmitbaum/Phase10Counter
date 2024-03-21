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

package com.tjEnterprises.phase10Counter.data

import kotlinx.coroutines.test.runTest
import org.junit.Test

/**
 * Unit tests for [DefaultDatabaseRepository].
 */
class DefaultDatabaseRepositoryTest {

    @Test
    fun databases_newItemSaved_itemIsReturned() = runTest {
        //val repository = DefaultDatabaseRepository(FakeDatabaseDao())

        //repository.add("Repository")

        //assertEquals(repository.databases.first().size, 1)
    }

}
/*
private class FakeDatabaseDao : DatabaseDao {

    private val data = mutableListOf<Database>()

    override fun getDatabases(): Flow<List<Database>> = flow {
        emit(data)
    }

    override suspend fun insertDatabase(item: Database) {
        data.add(0, item)
    }
}
*/
