package com.tjEnterprises.phase10Counter.ui.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import com.mikepenz.aboutlibraries.ui.compose.android.rememberLibraries
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.tjEnterprises.phase10Counter.R

@Composable
fun AboutLibrariesComponent(navigateOneBack: () -> Unit) {
    DefaultScaffoldBack(
        title = "Open Source Licenses", navigateOneBack = navigateOneBack
    ) { scaffoldModifier ->

        val libraries by rememberLibraries(R.raw.aboutlibraries)
        LibrariesContainer(
            libraries,
            scaffoldModifier.fillMaxSize(),
            showAuthor = true,
            showLicenseBadges = true,
            showVersion = true,
            showDescription = true
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AboutLibrariesComponentPreview() {
    AboutLibrariesComponent(navigateOneBack = {})
}

