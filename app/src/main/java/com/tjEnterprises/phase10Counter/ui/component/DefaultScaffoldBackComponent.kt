package com.tjEnterprises.phase10Counter.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultScaffoldBack (
    title: String, navigateOneBack: () -> Unit, content: @Composable (modifier: Modifier) -> Unit
) {
    BoxWithConstraints {
        /*
        if in landscape with low height, remove the topBar and only show Menu button
        the left side is filled with padding, so the content wont overlap with the button
        TODO make it configurable to disable the the landscape separation
        */
        val landscape = maxHeight <= 375.dp

        Scaffold(topBar = {
            if (!landscape) {
                CenterAlignedTopAppBar(
                    title = { Text(text = title) },
                    navigationIcon = {
                        IconButton(onClick = navigateOneBack) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = stringResource(
                                    id = R.string.back
                                )
                            )
                        }
                    },
                )
            }
        }) { innerPadding ->
            if (landscape) {
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(4.dp)
                ) {
                    IconButton(onClick = navigateOneBack) {
                        Icon(
                            Icons.Default.ArrowBack, contentDescription = stringResource(
                            id = R.string.back
                        )
                        )
                    }
                }
                content(
                    Modifier
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
fun DefaultScaffoldBackPreview() {
    DefaultScaffoldBack(title = "Title", navigateOneBack = {} ) {
        Text(text = "Content", fontSize = 24.sp, modifier = it)
    }
}