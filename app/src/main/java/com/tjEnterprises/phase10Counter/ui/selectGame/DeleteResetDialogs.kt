package com.tjEnterprises.phase10Counter.ui.selectGame

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.tjEnterprises.phase10Counter.R

@Composable
fun DeleteGameDialog (showDialog: MutableState<Boolean>, deleteGame: () -> Unit) {
    AlertDialog(
        icon = {
            Icon(Icons.Default.Info, contentDescription = null)
        },
        title = {
            Text(text = stringResource(id = R.string.deleteGame))
        },
        text = {
            Text(text = stringResource(id = R.string.areYouSureAllMatchDataWillBeLost))
        },
        onDismissRequest = {
            showDialog.value = false
        },
        confirmButton = {
            TextButton(
                onClick = {
                    deleteGame()
                    showDialog.value = false
                }
            ) {
                Text(text = stringResource(id = R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    showDialog.value = false
                }
            ) {
                Text(text = stringResource(id = R.string.cancel))
            }
        }
    )

}

@Composable
fun ResetGameDialog (showDialog: MutableState<Boolean>, resetGame: () -> Unit) {
    AlertDialog(
        icon = {
            Icon(Icons.Default.Info, contentDescription = null)
        },
        title = {
            Text(text = stringResource(id = R.string.resetGame))
        },
        text = {
            Text(text = stringResource(id = R.string.areYouSureAllMatchDataWillBeLost))
        },
        onDismissRequest = {
            showDialog.value = false
        },
        confirmButton = {
            TextButton(
                onClick = {
                    resetGame()
                    showDialog.value = false
                }
            ) {
                Text(text = stringResource(id = R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    showDialog.value = false
                }
            ) {
                Text(text = stringResource(id = R.string.cancel))
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun DeleteDialogPreview() {
    val show = remember {
        mutableStateOf(true)
    }
    DeleteGameDialog(showDialog = show, deleteGame = {})
}

@Preview(showBackground = true, locale = "DE")
@Composable
fun ResetDialogPreview() {
    val show = remember {
        mutableStateOf(true)
    }
    ResetGameDialog(showDialog = show, resetGame = {})
}