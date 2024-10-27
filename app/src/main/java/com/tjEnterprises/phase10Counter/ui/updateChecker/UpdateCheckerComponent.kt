package com.tjEnterprises.phase10Counter.ui.updateChecker

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
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

    // Do nothing on versionNumber == NO_RESPONSE (-2) (number not yet received)
    // Do nothing on else, already on latest version

    // only place Text if text exists
    if (text.isNotBlank()) {
        Text(
            buildAnnotatedString {
                if (!enableOnClick) {
                    append(text)
                }
                withLink(LinkAnnotation.Url(url = "https://github.com/etwasmitbaum/Phase10Counter/releases/latest/download/Phase10Counter.apk"),
                    block = { if (enableOnClick) append(text) })
            }, modifier = modifier.fillMaxWidth(), style = TextStyle(
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UpdateCheckerComponentPreview() {
    UpdateCheckerComponent(modifier = Modifier, versionNumber = 200)
}