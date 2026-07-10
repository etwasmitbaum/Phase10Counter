package com.tjEnterprises.phase10Counter.ui.editGame

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.tjEnterprises.phase10Counter.R

@Composable
fun DeletePlayerDialog(closeDialog: () -> Unit, playerName: String, deletePlayer: () -> Unit) {
    AlertDialog(
        icon = {
            Icon(Icons.Default.Info, contentDescription = null)
        },
        title = {
            Text(text = stringResource(id = R.string.deletePlayer) + " " + playerName)
        },
        text = {
            Text(text = stringResource(id = R.string.areYouSure))
        },
        onDismissRequest = {
            closeDialog()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    deletePlayer()
                    closeDialog()
                }
            ) {
                Text(text = stringResource(id = R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    closeDialog()
                }
            ) {
                Text(text = stringResource(id = R.string.cancel))
            }
        }
    )

}

@Preview(showBackground = true)
@Composable
fun DeletePlayerDialogPreview() {
    DeletePlayerDialog(closeDialog = {}, playerName = "Player 1", deletePlayer = {})
}

@Preview(showBackground = true)
@Composable
fun DeletePlayerDialogLongNamePreview() {
    DeletePlayerDialog(
        closeDialog = { },
        playerName = "Player 1 with very very very very very very very very long Name",
        deletePlayer = {})
}
