package com.tjEnterprises.phase10Counter.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
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
            SettingsScreen(
                modifier = modifier,
                settings = (settingsUiState as SettingsUiState.SettingsSuccess).settings,
                openDrawer = openDrawer
            )
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
    openDrawer: () -> Unit
) {
    DefaultScaffold(title = title, openDrawer = openDrawer) { scaffoldModifier ->

        Column(modifier = scaffoldModifier.then(modifier)) {

            // TODO move this to own composable so it is reusable
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = stringResource(id = R.string.check_for_updates_switch))
                Checkbox(checked = settings.checkForUpdates, onCheckedChange = {})
            }
        }
    }
}