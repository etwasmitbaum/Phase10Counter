package com.tjEnterprises.phase10Counter.data.local.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.tjEnterprises.phase10Counter.data.local.models.SettingsModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

interface SettingsRepository {
    val settingsModelFlow: Flow<SettingsModel>

    suspend fun updateCheckForUpdates(checkForUpdates: Boolean)
    suspend fun updateEnableMaterialUDesign(enableMaterialUDesign: Boolean)

    @Singleton
    class SettingsRepositoryImpl @Inject constructor(
        private val dataStore: DataStore<Preferences>
    ) : SettingsRepository {

        private object SettingsKeys {
            val CHECK_FOR_UPDATES = booleanPreferencesKey("checkForUpdates")
            val MATERIAL_U_DESIGN = booleanPreferencesKey("materialUDesign")
        }

        override val settingsModelFlow: Flow<SettingsModel>
            get() = dataStore.data.catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }.map { preferences ->

                val defaultSetting = SettingsModel()

                val checkForUpdates = preferences[SettingsKeys.CHECK_FOR_UPDATES] ?: defaultSetting.checkForUpdates
                val materialUDesign = preferences[SettingsKeys.MATERIAL_U_DESIGN] ?: defaultSetting.enableMaterialUDesign
                SettingsModel(checkForUpdates = checkForUpdates, enableMaterialUDesign = materialUDesign)
            }

        override suspend fun updateCheckForUpdates(checkForUpdates: Boolean) {
            dataStore.edit { preferences ->
                preferences[SettingsKeys.CHECK_FOR_UPDATES] = checkForUpdates
            }
        }

        override suspend fun updateEnableMaterialUDesign(enableMaterialUDesign: Boolean) {
            dataStore.edit { preferences ->
                preferences[SettingsKeys.MATERIAL_U_DESIGN] = enableMaterialUDesign
            }
        }
    }
}