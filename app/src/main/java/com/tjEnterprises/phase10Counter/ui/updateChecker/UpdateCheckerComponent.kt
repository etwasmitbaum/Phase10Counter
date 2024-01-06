package com.tjEnterprises.phase10Counter.ui.updateChecker

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tjEnterprises.phase10Counter.BuildConfig
import com.tjEnterprises.phase10Counter.R
import com.tjEnterprises.phase10Counter.data.network.repositories.UpdateCheckerCodes

@Composable
fun UpdateCheckerComponent(
    modifier: Modifier = Modifier, viewModel: UpdateCheckerViewModel = hiltViewModel()
) {
    val versionNumber by viewModel.versionNumber.collectAsState()
    val checkForUpdates by viewModel.checkForUpdates.collectAsState()

    if (checkForUpdates) {
        viewModel.loadVersionNumber()
        UpdateCheckerComponent(modifier = modifier, versionNumber = versionNumber)
    }
}

@Composable
internal fun UpdateCheckerComponent(modifier: Modifier, versionNumber: Int) {

    var text by remember { mutableStateOf("") }
    var enableOnClick = false

    if (versionNumber > BuildConfig.VERSION_CODE) {
        text = stringResource(id = R.string.newVersionClickToDownload)
        enableOnClick = true
    } else if (versionNumber == UpdateCheckerCodes.ERROR_GETTING_LATEST_VERSION_NUMBER) {
        text = stringResource(id = R.string.errorWhileCheckingForUpdate)
    }

    // Do nothing on versionNumber == -2 (number not yet received)
    // DO nothing on else, already on latest version

    // only place ClickableText if text exists
    if (text != "") {
        val annotatedString = buildAnnotatedString {
            append(text)
        }
        val uriHandler = LocalUriHandler.current
        ClickableText(text = annotatedString, modifier = modifier.fillMaxWidth(), style = TextStyle(
            textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp
        ), onClick = {
            if (enableOnClick) {
                uriHandler.openUri("https://github.com/etwasmitbaum/Phase10Counter/releases/latest/download/Phase10Counter.apk")
            }
        })
    }
}

@Preview(showBackground = true)
@Composable
fun UpdateCheckerComponentPreview() {
    UpdateCheckerComponent(modifier = Modifier, versionNumber = 200)
}