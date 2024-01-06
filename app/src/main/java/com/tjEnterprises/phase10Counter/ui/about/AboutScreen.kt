package com.tjEnterprises.phase10Counter.ui.about

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.tjEnterprises.phase10Counter.BuildConfig
import com.tjEnterprises.phase10Counter.R
import com.tjEnterprises.phase10Counter.ui.component.DefaultScaffoldNavigation

@Composable
fun AboutScreen(
    openDrawer: () -> Unit, navigateToAboutLibraries: () -> Unit, navigateToAppLicence: () -> Unit, viewModel: AboutScreenViewModel = hiltViewModel()
) {
    val dontChangeUiWideScreen by viewModel.dontChangeUiWideScreen.collectAsState()

    AboutScreen(
        openDrawer = openDrawer,
        navigateToAboutLibraries = navigateToAboutLibraries,
        navigateToAppLicence = navigateToAppLicence,
        dontChangeUiWideScreen = dontChangeUiWideScreen
    )
}


@Composable
fun AboutScreen(
    openDrawer: () -> Unit,
    navigateToAboutLibraries: () -> Unit,
    navigateToAppLicence: () -> Unit,
    dontChangeUiWideScreen: Boolean
) {
    DefaultScaffoldNavigation(
        title = stringResource(id = R.string.about),
        openDrawer = openDrawer,
        dontChangeUiWideScreen = dontChangeUiWideScreen
    ) { scaffoldModifier ->

        val uriHandler = LocalUriHandler.current
        val scrollState = rememberScrollState()

        Column(modifier = scaffoldModifier.verticalScroll(scrollState)) {

            // Open Link to GitHub
            SettingsMenuLink(title = { Text(text = stringResource(id = R.string.githubRepository)) },
                subtitle = { Text(text = "https://github.com/etwasmitbaum/Phase10Counter") },
                action = {
                    IconButton(onClick = { uriHandler.openUri("https://github.com/etwasmitbaum/Phase10Counter") }) {
                        Icon(
                            // TODO Change icon to "external app"
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = null,
                        )
                    }

                },
                icon = {
                    Icon(
                        // TODO Add GitHub Icon
                        imageVector = Icons.Default.Clear,
                        contentDescription = null,
                        modifier = Modifier.alpha(0f)   // make icon transparent so it is in line with the other settings
                    )
                }) {
                uriHandler.openUri("https://github.com/etwasmitbaum/Phase10Counter")
            }
            Divider()

            // App Licence
            SettingsMenuLink(title = { Text(text = stringResource(id = R.string.app_license)) },
                subtitle = { Text(text = stringResource(id = R.string.GPLv3License)) },
                action = {
                    IconButton(onClick = { navigateToAppLicence() }) {
                        Icon(
                            // TODO Change icon to "external app"
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = null,
                        )
                    }

                },
                icon = {
                    Icon(
                        // TODO Add GitHub Icon
                        imageVector = Icons.Default.Clear,
                        contentDescription = null,
                        modifier = Modifier.alpha(0f)   // make icon transparent so it is in line with the other settings
                    )
                }) {
                navigateToAppLicence()
            }
            Divider()

            // Show all opensource licences
            SettingsMenuLink(title = { Text(text = stringResource(id = R.string.allOpenSourceLicenses)) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = null,
                        modifier = Modifier.alpha(0f)   // make icon transparent so it is in line with the other settings
                    )
                },
                action = {
                    IconButton(onClick = { navigateToAboutLibraries() }) {
                        Icon(
                            // TODO Change icon to "external app"
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = null,
                        )
                    }

                }) {
                navigateToAboutLibraries()
            }
            Divider()

            // App Version
            SettingsMenuLink(title = { Text(text = stringResource(id = R.string.version)) },
                subtitle = { Text(text = BuildConfig.VERSION_NAME) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = null,
                        modifier = Modifier.alpha(0f)   // make icon transparent so it is in line with the other settings
                    )
                }) {}
            Divider()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AboutScreenPreview() {
    AboutScreen(openDrawer = {}, navigateToAboutLibraries = {}, navigateToAppLicence = {}, dontChangeUiWideScreen = false)
}