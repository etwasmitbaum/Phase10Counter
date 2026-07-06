package com.tjEnterprises.phase10Counter.ui.theme

import androidx.compose.material3.ListItemColors
import androidx.compose.runtime.Composable
import com.alorma.compose.settings.ui.SettingsTileDefaults

class P10SettingsColor {

    companion object {
        @Composable
        fun colors(): ListItemColors {
            val colors = SettingsTileDefaults.colors()
            return colors
        }
    }
}