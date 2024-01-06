package com.tjEnterprises.phase10Counter.data.local.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.tjEnterprises.phase10Counter.BuildConfig
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
    suspend fun updateUseDynamicColors(useDynamicColors: Boolean)
    suspend fun updateUseSystemTheme(useSystemTheme: Boolean)
    suspend fun updateUseDarkTheme(useDarkTheme: Boolean)
    suspend fun updateDontChangeUiWideScreen(dontChangeUiWideScreen: Boolean)

    @Singleton
    class SettingsRepositoryImpl @Inject constructor(
        private val dataStore: DataStore<Preferences>
    ) : SettingsRepository {

        private object SettingsKeys {
            val CHECK_FOR_UPDATES = booleanPreferencesKey("checkForUpdates")
            val USE_DYNAMIC_COLORS = booleanPreferencesKey("useDynamicColors")
            val USE_DARK_THEME = booleanPreferencesKey("useDarkTheme")
            val USE_SYSTEM_THEME = booleanPreferencesKey("useSystemTheme")
            val DONT_CHANGE_UI_WIDE_SCREEN = booleanPreferencesKey("dontChangeUiOnWideScreen")
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

                // Don't check for updates on F-Droid release
                val checkForUpdates =
                    if (BuildConfig.BUILD_TYPE != "release") preferences[SettingsKeys.CHECK_FOR_UPDATES]
                        ?: defaultSetting.checkForUpdates else false

                val useDynamicColors =
                    preferences[SettingsKeys.USE_DYNAMIC_COLORS] ?: defaultSetting.useDynamicColors
                val useDarkTheme =
                    preferences[SettingsKeys.USE_DARK_THEME] ?: defaultSetting.useDarkTheme
                val useSystemTheme =
                    preferences[SettingsKeys.USE_SYSTEM_THEME] ?: defaultSetting.useSystemTheme
                val dontChangeUiWideScreen = preferences[SettingsKeys.DONT_CHANGE_UI_WIDE_SCREEN]
                    ?: defaultSetting.dontChangeUiOnWideScreen

                SettingsModel(
                    checkForUpdates = checkForUpdates,
                    useDynamicColors = useDynamicColors,
                    useDarkTheme = useDarkTheme,
                    useSystemTheme = useSystemTheme,
                    dontChangeUiOnWideScreen = dontChangeUiWideScreen
                )
            }

        private suspend fun writeBooleanToDataStore(key: Preferences.Key<Boolean>, value: Boolean) {
            dataStore.edit { preferences ->
                preferences[key] = value
            }
        }

        override suspend fun updateCheckForUpdates(checkForUpdates: Boolean) {
            writeBooleanToDataStore(SettingsKeys.CHECK_FOR_UPDATES, checkForUpdates)
        }

        override suspend fun updateUseDynamicColors(useDynamicColors: Boolean) {
            writeBooleanToDataStore(SettingsKeys.USE_DYNAMIC_COLORS, useDynamicColors)
        }

        override suspend fun updateUseSystemTheme(useSystemTheme: Boolean) {
            writeBooleanToDataStore(SettingsKeys.USE_SYSTEM_THEME, useSystemTheme)
        }

        override suspend fun updateUseDarkTheme(useDarkTheme: Boolean) {
            writeBooleanToDataStore(SettingsKeys.USE_DARK_THEME, useDarkTheme)
        }

        override suspend fun updateDontChangeUiWideScreen(dontChangeUiWideScreen: Boolean) {
            writeBooleanToDataStore(SettingsKeys.DONT_CHANGE_UI_WIDE_SCREEN, dontChangeUiWideScreen)
        }

    }
}