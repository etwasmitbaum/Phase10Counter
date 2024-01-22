package com.tjEnterprises.phase10Counter.data.local.models

import com.tjEnterprises.phase10Counter.BuildConfig

data class SettingsModel(
    val checkForUpdates: Boolean = if (BuildConfig.BUILD_TYPE != "release") true else false,
    val useDynamicColors: Boolean = true,
    val useDarkTheme: Boolean = false,
    val useSystemTheme: Boolean = true,
    val dontChangeUiOnWideScreen: Boolean = false
)