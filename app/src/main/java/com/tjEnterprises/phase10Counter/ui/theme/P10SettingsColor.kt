package com.tjEnterprises.phase10Counter.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.alorma.compose.settings.ui.base.internal.SettingsTileColors
import com.alorma.compose.settings.ui.base.internal.SettingsTileDefaults

class P10SettingsColor {

    companion object {
        @Composable
        fun colors(): SettingsTileColors {
            val colors =
                SettingsTileDefaults.colors(titleColor = MaterialTheme.colorScheme.onSurface)
            return colors
        }
    }
}