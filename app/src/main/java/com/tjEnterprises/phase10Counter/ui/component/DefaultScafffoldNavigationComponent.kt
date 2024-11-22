package com.tjEnterprises.phase10Counter.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tjEnterprises.phase10Counter.R
import com.tjEnterprises.phase10Counter.data.local.models.SettingsModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultScaffoldNavigation(
    title: String,
    openDrawer: () -> Unit,
    dontChangeUiWideScreen: Boolean = SettingsModel().dontChangeUiOnWideScreen,
    content: @Composable (modifier: Modifier) -> Unit
) {
    BoxWithConstraints {
        /*
        if in landscape with low height, remove the topBar and only show Menu button
        the left side is filled with padding, so the content wont overlap with the button
        */
        val landscapeMaxHeightLow = maxHeight <= 375.dp

        Scaffold(topBar = {
            if (!landscapeMaxHeightLow || dontChangeUiWideScreen) {
                CenterAlignedTopAppBar(
                    title = { Text(text = title) },
                    navigationIcon = {
                        IconButton(onClick = openDrawer) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = stringResource(
                                    id = R.string.menu
                                )
                            )
                        }
                    },
                )
            }
        }, contentWindowInsets = WindowInsets.safeDrawing
        ) { innerPadding ->
            if (landscapeMaxHeightLow && !dontChangeUiWideScreen) {
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(4.dp)
                ) {
                    IconButton(onClick = openDrawer) {
                        Icon(
                            Icons.Default.Menu, contentDescription = stringResource(
                                id = R.string.menu
                            )
                        )
                    }
                }
                content(
                    Modifier
                        //.windowInsetsPadding(WindowInsets.safeDrawing)
                        .padding(innerPadding)
                        .padding(start = 52.dp, top = 8.dp)
                )
            } else {
                content(Modifier.padding(innerPadding))
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 600)
@Preview(showBackground = true, widthDp = 600, heightDp = 250)
@Composable
fun DefaultScaffoldPreview() {
    DefaultScaffoldNavigation(title = "Title", openDrawer = { }, dontChangeUiWideScreen = false) {
        Text(text = "Content", fontSize = 24.sp, modifier = it)
    }
}