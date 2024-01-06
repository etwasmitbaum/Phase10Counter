package com.tjEnterprises.phase10Counter.data.local.models

data class SettingsModel(
    val checkForUpdates: Boolean = true,
    val useDynamicColors: Boolean = true,
    val useDarkTheme: Boolean = false,
    val useSystemTheme: Boolean = true,
    val dontChangeUiOnWideScreen: Boolean = false
)