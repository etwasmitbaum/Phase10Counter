package com.tjEnterprises.phase10Counter.ui.about

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tjEnterprises.phase10Counter.R
import com.tjEnterprises.phase10Counter.ui.AppLicenceUiState
import com.tjEnterprises.phase10Counter.ui.component.DefaultScaffoldBack


@Composable
fun AppLicenceScreen(
    viewModel: AppLicenceViewModel = hiltViewModel(), navigateOneBack: () -> Unit
) {
    viewModel.loadData(LocalContext.current)

    val licenceUiState by viewModel.license.collectAsStateWithLifecycle()

    when (licenceUiState) {
        is AppLicenceUiState.AppLicenceSuccess -> {
            AppLicenceScreen(
                modifier = Modifier,
                licence = (licenceUiState as AppLicenceUiState.AppLicenceSuccess).license,
                title = stringResource(id = R.string.app_license),
                navigateOneBack = navigateOneBack
            )
        }

        is AppLicenceUiState.AppLicenceLoading -> {
            DefaultScaffoldBack(
                title = stringResource(id = R.string.loadingAppLicence),
                navigateOneBack = navigateOneBack
            ) {}
        }

        is AppLicenceUiState.AppLicenceError -> {
            DefaultScaffoldBack(
                title = stringResource(id = R.string.errorAppLicence),
                navigateOneBack = navigateOneBack
            ) {}
        }
    }
}

@Composable
internal fun AppLicenceScreen(
    modifier: Modifier, licence: String, title: String, navigateOneBack: () -> Unit
) {
    DefaultScaffoldBack(title = title, navigateOneBack = navigateOneBack) { scaffoldModifier ->

        val scrollState = rememberScrollState(0)

        Text(
            text = licence,
            modifier = scaffoldModifier
                .then(modifier)
                .padding(8.dp)
                .verticalScroll(scrollState)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AppLicenceScreenPreview() {
    AppLicenceScreen(modifier = Modifier,
        licence = "bla bla bla",
        title = stringResource(id = R.string.app_license),
        navigateOneBack = {})
}