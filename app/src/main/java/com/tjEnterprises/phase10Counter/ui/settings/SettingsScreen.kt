package com.tjEnterprises.phase10Counter.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
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
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.alorma.compose.settings.ui.SettingsSwitch
import com.tjEnterprises.phase10Counter.BuildConfig
import com.tjEnterprises.phase10Counter.R
import com.tjEnterprises.phase10Counter.data.local.models.SettingsModel
import com.tjEnterprises.phase10Counter.ui.SettingsUiState
import com.tjEnterprises.phase10Counter.ui.component.DefaultScaffold
import com.tjEnterprises.phase10Counter.ui.updateChecker.UpdateCheckerComponent

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
    openDrawer: () -> Unit,
    navigateToAboutLibraries: () -> Unit
) {
    val settingsUiState by viewModel.settingsUiState.collectAsState()

    when (settingsUiState) {
        is SettingsUiState.SettingsSuccess -> {
            SettingsScreen(
                modifier = modifier,
                settings = (settingsUiState as SettingsUiState.SettingsSuccess).settings,
                openDrawer = openDrawer,
                updateCheckForUpdates = { viewModel.updateCheckForUpdates(it) },
                updateUseDynamicColors = { viewModel.updateUseDynamicColors(it) },
                updateUseSystemTheme = { viewModel.updateUseSystemTheme(it) },
                updateUseDarkTheme = { viewModel.updateUseDarkTheme(it) },
                navigateToAboutLibraries = navigateToAboutLibraries,
                updateChecker = { UpdateCheckerComponent(it) }
            )
        }

        is SettingsUiState.SettingsLoading -> {
            SettingsScreen(modifier = modifier,
                settings = SettingsModel(),
                title = "Loading Settings",
                openDrawer = openDrawer,
                updateCheckForUpdates = {},
                updateUseDynamicColors = {},
                updateUseSystemTheme = {},
                updateUseDarkTheme = {},
                navigateToAboutLibraries = {})
        }

        is SettingsUiState.SettingsError -> {
            SettingsScreen(modifier = modifier,
                settings = SettingsModel(),
                title = "Error Settings",
                openDrawer = openDrawer,
                updateCheckForUpdates = {},
                updateUseDynamicColors = {},
                updateUseSystemTheme = {},
                updateUseDarkTheme = {},
                navigateToAboutLibraries = {})
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
    navigateToAboutLibraries: () -> Unit,
    updateChecker: @Composable (Modifier) -> Unit = {}
) {

    DefaultScaffold(title = title, openDrawer = openDrawer) { scaffoldModifier ->

        Column(modifier = scaffoldModifier.then(modifier)) {

            updateChecker(Modifier)

            // Auto check for Updates only for GitHub and Debug builds
            if (BuildConfig.BUILD_TYPE != "release") {
                SettingsSwitch(title = { Text(text = stringResource(id = R.string.check_for_updates_switch)) },
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

            // Enable Dynamic Colors (android 12+)
            SettingsSwitch(title = { Text(text = stringResource(id = R.string.enableDynamicColors)) },
                subtitle = {
                    Text(
                        text = stringResource(id = R.string.requiresAndroid12plus)
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

            // Use System Theme
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

            // Use Dark Theme
            SettingsSwitch(title = { Text(text = stringResource(id = R.string.darkTheme)) },
                state = rememberBooleanSettingState(settings.useDarkTheme),
                onCheckedChange = { newValue -> updateUseDarkTheme(newValue) },
                enabled = !settings.useSystemTheme,
                icon = {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.alpha(0f)   // make icon transparent so it is in line with the other settings
                    )
                })

            Divider()

            // Show all opensource licences
            SettingsMenuLink(title = { Text(text = stringResource(id = R.string.all_opensource_license)) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = null,
                        modifier = Modifier.alpha(0f)   // make icon transparent so it is in line with the other settings
                    )
                }) {
                navigateToAboutLibraries()
            }

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
        updateUseDarkTheme = {},
        navigateToAboutLibraries = {})
}