package com.tjEnterprises.phase10Counter.ui.settings

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.alorma.compose.settings.ui.SettingsSwitch
import com.tjEnterprises.phase10Counter.BuildConfig
import com.tjEnterprises.phase10Counter.R
import com.tjEnterprises.phase10Counter.data.local.models.SettingsModel
import com.tjEnterprises.phase10Counter.ui.SettingsUiState
import com.tjEnterprises.phase10Counter.ui.component.DefaultScaffoldNavigation
import com.tjEnterprises.phase10Counter.ui.theme.P10SettingsColor
import com.tjEnterprises.phase10Counter.ui.updateChecker.UpdateCheckerComponent
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
    openDrawer: () -> Unit,
) {
    val settingsUiState by viewModel.settingsUiState.collectAsState()
    val copyError by viewModel.copyError.collectAsState()

    when (settingsUiState) {
        is SettingsUiState.SettingsSuccess -> {
            SettingsScreen(modifier = modifier,
                settings = (settingsUiState as SettingsUiState.SettingsSuccess).settings,
                openDrawer = openDrawer,
                updateCheckForUpdates = { viewModel.updateCheckForUpdates(it) },
                updateUseDynamicColors = { viewModel.updateUseDynamicColors(it) },
                updateUseSystemTheme = { viewModel.updateUseSystemTheme(it) },
                updateUseDarkTheme = { viewModel.updateUseDarkTheme(it) },
                doBackup = { context, pickedUri, progress ->
                    viewModel.backUpDatabase(
                        context, pickedUri, progress
                    )
                },
                doRestore = { context, pickedUri, progress ->
                    viewModel.restoreDatabase(
                        context, pickedUri, progress
                    )
                },
                copyError = copyError,
                updateDontChangeUiWideScreen = { viewModel.updateDontChangeUiWideScreen(it) },
                updateChecker = { UpdateCheckerComponent(it) })
        }

        is SettingsUiState.SettingsLoading -> {
            DefaultScaffoldNavigation(
                title = stringResource(id = R.string.settingsLoading), openDrawer = openDrawer
            ) {}
        }

        is SettingsUiState.SettingsError -> {
            DefaultScaffoldNavigation(
                title = stringResource(id = R.string.settingsError), openDrawer = openDrawer
            ) {}
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
    doBackup: (Context, Uri, MutableFloatState) -> Unit,
    doRestore: (Context, Uri, MutableFloatState) -> Unit,
    copyError: Boolean,
    updateDontChangeUiWideScreen: (Boolean) -> Unit,
    updateChecker: @Composable (Modifier) -> Unit = {}
) {
    val copyProgress = remember { mutableFloatStateOf(0f) }
    val context = LocalContext.current
    val showCopyDialog = remember { mutableStateOf(false) }
    val wasBackup = remember { mutableIntStateOf(WasCopyRestore.WAS_NEITHER) }

    val backupARL = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val pickedURI = result.data?.data
            showCopyDialog.value = true
            wasBackup.intValue = WasCopyRestore.WAS_BACKUP
            doBackup(context, pickedURI!!, copyProgress)
        } else { /* The activity was canceled. */
        }
    }

    val restoreARL = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val pickedURI = result.data?.data
            showCopyDialog.value = true
            wasBackup.intValue = WasCopyRestore.WAS_RESTORE
            doRestore(context, pickedURI!!, copyProgress)
        } else { /* The activity was canceled. */
        }
    }

    when {
        showCopyDialog.value -> {
            CopyDialog(progress = copyProgress.floatValue, showDialog = showCopyDialog)
        }

        wasBackup.intValue == WasCopyRestore.WAS_RESTORE -> {
            Toast.makeText(
                LocalContext.current,
                stringResource(id = R.string.appRestartMayBeRequired),
                Toast.LENGTH_SHORT
            ).show()
        }

        copyError -> {
            if (wasBackup.intValue == WasCopyRestore.WAS_RESTORE) {
                Toast.makeText(
                    context, stringResource(id = R.string.errorWhileRestoring), Toast.LENGTH_SHORT
                ).show()

            } else if (wasBackup.intValue == WasCopyRestore.WAS_BACKUP) {
                Toast.makeText(
                    context, stringResource(id = R.string.errorCreatingBackup), Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    DefaultScaffoldNavigation(
        title = title,
        openDrawer = openDrawer,
        dontChangeUiWideScreen = settings.dontChangeUiOnWideScreen
    ) { scaffoldModifier ->

        val scrollState = rememberScrollState()
        Column(
            modifier = scaffoldModifier
                .then(modifier)
                .verticalScroll(scrollState)
        ) {

            updateChecker(Modifier)

            // Auto check for Updates only for GitHub and Debug builds
            if (BuildConfig.BUILD_TYPE != "release") {
                HorizontalDivider()
                SettingsSwitch(
                    title = {
                        Text(
                            text = stringResource(id = R.string.autoCheckForUpdates)
                        )
                    },
                    state = settings.checkForUpdates,
                    onCheckedChange = { newValue -> updateCheckForUpdates(newValue) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.alpha(0f)   // make icon transparent so it is in line with the other settings
                        )
                    },
                    colors = P10SettingsColor.colors()
                )
            }

            HorizontalDivider()

            // Enable Dynamic Colors (Android 12+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                SettingsSwitch(
                    title = {
                        Text(
                            text = stringResource(id = R.string.enableDynamicColors),
                        )
                    },
                    subtitle = {
                        Text(
                            text = stringResource(id = R.string.useSystemColors)
                        )
                    },
                    state = settings.useDynamicColors,
                    onCheckedChange = { newValue -> updateUseDynamicColors(newValue) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.alpha(0f)   // make icon transparent so it is in line with the other settings
                        )
                    },
                    colors = P10SettingsColor.colors()
                )
            }

            // Use System Theme (Android 10+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                SettingsSwitch(
                    title = {
                        Text(
                            text = stringResource(id = R.string.followSystemTheme),
                        )
                    },
                    state = settings.useSystemTheme,
                    onCheckedChange = { newValue -> updateUseSystemTheme(newValue) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.alpha(0f)   // make icon transparent so it is in line with the other settings
                        )
                    },
                    colors = P10SettingsColor.colors()
                )
            }

            // Use Dark Theme
            SettingsSwitch(
                title = {
                    Text(
                        text = stringResource(id = R.string.darkTheme),
                    )
                },
                state = settings.useDarkTheme,
                onCheckedChange = { newValue -> updateUseDarkTheme(newValue) },
                enabled = !settings.useSystemTheme,
                icon = {
                    // TODO Add Dark/Light Theme Icon
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.alpha(0f)   // make icon transparent so it is in line with the other settings
                    )
                },
                colors = P10SettingsColor.colors()
            )

            HorizontalDivider()

            // Force don't Change Ui on Wide Screen
            SettingsSwitch(
                title = {
                    Text(
                        text = stringResource(id = R.string.dontChangeUiOnWideScreen),
                    )
                },
                state = settings.dontChangeUiOnWideScreen,
                onCheckedChange = { newValue -> updateDontChangeUiWideScreen(newValue) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.alpha(0f)   // make icon transparent so it is in line with the other settings
                    )
                },
                colors = P10SettingsColor.colors()
            )

            HorizontalDivider()

            // Backup Game
            val fileName = stringResource(id = R.string.backupFileName)
            SettingsMenuLink(title = {
                Text(
                    text = stringResource(id = R.string.backupGames),
                )
            }, icon = {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = null,
                    modifier = Modifier.alpha(0f)   // make icon transparent so it is in line with the other settings
                )
            }, colors = P10SettingsColor.colors()
            ) {
                val intent =
                    Intent(Intent.ACTION_CREATE_DOCUMENT).setType("application/octet-stream")
                        .putExtra(
                            Intent.EXTRA_TITLE, "$fileName " + SimpleDateFormat(
                                "dd MMM yyyy", Locale.getDefault()
                            ).format(System.currentTimeMillis())
                        )
                backupARL.launch(intent)
            }

            // Restore games from backup file
            SettingsMenuLink(title = {
                Text(
                    text = stringResource(id = R.string.restoreGames)
                )
            }, subtitle = {
                Text(
                    text = stringResource(id = R.string.thisWillOverwriteAllExistingData) + "\n" + stringResource(
                        id = R.string.appRestartMayBeRequired
                    )
                )
            }, icon = {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = null,
                    modifier = Modifier.alpha(0f)   // make icon transparent so it is in line with the other settings
                )
            }, colors = P10SettingsColor.colors()
            ) {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).setType("application/octet-stream")
                restoreARL.launch(intent)
            }

            HorizontalDivider()
        }
    }
}


@Preview(showBackground = true)
@Preview(showBackground = true, locale = "DE")
@Composable
fun SettingsScreenPreview() {
    SettingsScreen(modifier = Modifier,
        settings = SettingsModel(useSystemTheme = false, useDarkTheme = true),
        openDrawer = {},
        updateCheckForUpdates = {},
        updateUseDynamicColors = {},
        updateUseSystemTheme = {},
        doBackup = { _, _, _ -> },
        doRestore = { _, _, _ -> },
        copyError = false,
        updateDontChangeUiWideScreen = {},
        updateUseDarkTheme = {})
}