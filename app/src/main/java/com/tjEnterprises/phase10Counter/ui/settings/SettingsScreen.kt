package com.tjEnterprises.phase10Counter.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.alorma.compose.settings.storage.base.rememberBooleanSettingState
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.alorma.compose.settings.ui.SettingsSwitch
import com.tjEnterprises.phase10Counter.R
import com.tjEnterprises.phase10Counter.data.local.models.SettingsModel
import com.tjEnterprises.phase10Counter.ui.SettingsUiState
import com.tjEnterprises.phase10Counter.ui.component.DefaultScaffold

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
            SettingsScreen(modifier = modifier,
                settings = (settingsUiState as SettingsUiState.SettingsSuccess).settings,
                openDrawer = openDrawer,
                updateCheckForUpdates = { viewModel.updateCheckForUpdates(it) },
                navigateToAboutLibraries = navigateToAboutLibraries
            )
        }

        is SettingsUiState.SettingsLoading -> {
            SettingsScreen(modifier = modifier,
                settings = SettingsModel(),
                title = "Loading Settings",
                openDrawer = openDrawer,
                updateCheckForUpdates = {},
                navigateToAboutLibraries = {}
            )
        }

        is SettingsUiState.SettingsError -> {
            SettingsScreen(
                modifier = modifier,
                settings = SettingsModel(),
                title = "Error Settings",
                openDrawer = openDrawer,
                updateCheckForUpdates = {},
                navigateToAboutLibraries = {}
            )
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
    navigateToAboutLibraries: () -> Unit
) {

    DefaultScaffold(title = title, openDrawer = openDrawer) { scaffoldModifier ->
        Column(modifier = scaffoldModifier.then(modifier)) {
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
            Divider()

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
        settings = SettingsModel(checkForUpdates = true),
        openDrawer = { },
        updateCheckForUpdates = {}, navigateToAboutLibraries = {})
}