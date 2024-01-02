package com.tjEnterprises.phase10Counter.ui.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mikepenz.aboutlibraries.ui.compose.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.LibraryDefaults.libraryColors

@Composable
fun AboutLibrariesComponent(navigateOneBack: () -> Unit) {
    DefaultScaffoldBack(title = "Open Source Licenses", navigateOneBack = navigateOneBack) { scaffoldModifier ->
        LibrariesContainer(
            scaffoldModifier.then(Modifier.fillMaxSize()),
            showAuthor = true,
            showLicenseBadges = true,
            showVersion = true,
            // for some reason I need to define the colors myself
            colors = libraryColors(
                backgroundColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onSurface,
                badgeBackgroundColor = MaterialTheme.colorScheme.primary,
                badgeContentColor = MaterialTheme.colorScheme.onPrimary,
                dialogConfirmButtonColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AboutLibrariesComponentPreview() {
    AboutLibrariesComponent(navigateOneBack = {})
}

