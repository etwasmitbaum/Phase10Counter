package com.tjEnterprises.phase10Counter.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.alorma.compose.settings.storage.base.rememberBooleanSettingState
import com.alorma.compose.settings.ui.SettingsCheckbox
import com.alorma.compose.settings.ui.SettingsGroup
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
) {
    val settingsUiState by viewModel.settingsUiState.collectAsState()

    when (settingsUiState) {
        is SettingsUiState.SettingsSuccess -> {

            println((settingsUiState as SettingsUiState.SettingsSuccess).settings.checkForUpdates)

            SettingsScreen(modifier = modifier,
                settings = (settingsUiState as SettingsUiState.SettingsSuccess).settings,
                openDrawer = openDrawer,
                updateCheckForUpdates = { viewModel.updateCheckForUpdates(it) })
        }

        is SettingsUiState.SettingsLoading -> {

        }

        is SettingsUiState.SettingsError -> {

        }
    }
}

@Composable
internal fun SettingsScreen(
    modifier: Modifier,
    settings: SettingsModel,
    title: String = stringResource(id = R.string.settings),
    openDrawer: () -> Unit,
    updateCheckForUpdates: (Boolean) -> Unit
) {
    DefaultScaffold(title = title, openDrawer = openDrawer) { scaffoldModifier ->
        Column(modifier = scaffoldModifier.then(modifier)) {
            SettingsSwitch(title = { Text( text = stringResource(id = R.string.check_for_updates_switch)) },
                state = rememberBooleanSettingState(settings.checkForUpdates),
                onCheckedChange = { newValue -> updateCheckForUpdates(newValue) })
            Divider()
            
            SettingsCheckbox(title = { Text( text = "test title") }, subtitle = { Text(text = "test subtile")},
                state = rememberBooleanSettingState(settings.checkForUpdates),
                onCheckedChange = { newValue -> updateCheckForUpdates(newValue) })
            Divider()
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview(){
    SettingsScreen(modifier = Modifier, settings = SettingsModel(checkForUpdates = true), openDrawer = {  }, updateCheckForUpdates = {})
}