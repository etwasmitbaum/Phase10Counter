package com.tjEnterprises.phase10Counter.ui.settings

import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.alorma.compose.settings.storage.base.rememberBooleanSettingState
import com.alorma.compose.settings.ui.SettingsSwitch
import com.tjEnterprises.phase10Counter.BuildConfig
import com.tjEnterprises.phase10Counter.R
import com.tjEnterprises.phase10Counter.data.local.models.SettingsModel
import com.tjEnterprises.phase10Counter.ui.SettingsUiState
import com.tjEnterprises.phase10Counter.ui.component.DefaultScaffoldNavigation
import com.tjEnterprises.phase10Counter.ui.updateChecker.UpdateCheckerComponent

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
    openDrawer: () -> Unit,
) {
    val settingsUiState by viewModel.settingsUiState.collectAsState()

    when (settingsUiState) {
        is SettingsUiState.SettingsSuccess -> {
            SettingsScreen(modifier = modifier,
                settings = (settingsUiState as SettingsUiState.SettingsSuccess).settings,
                openDrawer = openDrawer,
                updateCheckForUpdates = { viewModel.updateCheckForUpdates(it) },
                updateUseDynamicColors = { viewModel.updateUseDynamicColors(it) },
                updateUseSystemTheme = { viewModel.updateUseSystemTheme(it) },
                updateUseDarkTheme = { viewModel.updateUseDarkTheme(it) },
                updateChecker = { UpdateCheckerComponent(it) })
        }

        is SettingsUiState.SettingsLoading -> {
            DefaultScaffoldNavigation(title = stringResource(id = R.string.settingsLoading), openDrawer = openDrawer) {}
        }

        is SettingsUiState.SettingsError -> {
            DefaultScaffoldNavigation(title = stringResource(id = R.string.settingsError), openDrawer = openDrawer) {}
        }
    }
}

@Composable
internal fun SettingsScreen(
    modifier: Modifier,
    settings: SettingsModel,
    title: String = stringResource(id = R.string.settings),
    openDrawer: () -> Unit,
    updateCheckForUpdates: (Boolean) -> Unit,
    updateUseDynamicColors: (Boolean) -> Unit,
    updateUseSystemTheme: (Boolean) -> Unit,
    updateUseDarkTheme: (Boolean) -> Unit,
    updateChecker: @Composable (Modifier) -> Unit = {}
) {

    DefaultScaffoldNavigation(title = title, openDrawer = openDrawer) { scaffoldModifier ->

        Column(modifier = scaffoldModifier.then(modifier)) {

            updateChecker(Modifier)

            // Auto check for Updates only for GitHub and Debug builds
            if (BuildConfig.BUILD_TYPE != "release") {
                SettingsSwitch(title = { Text(text = stringResource(id = R.string.autoCheckForUpdates)) },
                    state = rememberBooleanSettingState(settings.checkForUpdates),
                    onCheckedChange = { newValue -> updateCheckForUpdates(newValue) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.alpha(0f)   // make icon transparent so it is in line with the other settings
                        )
                    })
            }

            Divider()

            // Enable Dynamic Colors (Android 12+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                SettingsSwitch(title = { Text(text = stringResource(id = R.string.enableDynamicColors)) },
                    subtitle = {
                        Text(
                            text = stringResource(id = R.string.useSystemColors)
                        )
                    },
                    state = rememberBooleanSettingState(settings.useDynamicColors),
                    onCheckedChange = { newValue -> updateUseDynamicColors(newValue) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.alpha(0f)   // make icon transparent so it is in line with the other settings
                        )
                    })
            }

            // Use System Theme (Android 10+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                SettingsSwitch(title = { Text(text = stringResource(id = R.string.followSystemTheme)) },
                    state = rememberBooleanSettingState(settings.useSystemTheme),
                    onCheckedChange = { newValue -> updateUseSystemTheme(newValue) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.alpha(0f)   // make icon transparent so it is in line with the other settings
                        )
                    })
            }

            // Use Dark Theme
            SettingsSwitch(title = { Text(text = stringResource(id = R.string.darkTheme)) },
                state = rememberBooleanSettingState(settings.useDarkTheme),
                onCheckedChange = { newValue -> updateUseDarkTheme(newValue) },
                enabled = !settings.useSystemTheme,
                icon = {
                    // TODO Add Dark/Light Theme Icon
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.alpha(0f)   // make icon transparent so it is in line with the other settings
                    )
                })

            Divider()
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen(modifier = Modifier,
        settings = SettingsModel(useSystemTheme = false, useDarkTheme = true),
        openDrawer = {},
        updateCheckForUpdates = {},
        updateUseDynamicColors = {},
        updateUseSystemTheme = {},
        updateUseDarkTheme = {})
}