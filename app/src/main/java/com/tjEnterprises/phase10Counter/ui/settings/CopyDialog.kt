package com.tjEnterprises.phase10Counter.ui.settings

import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.window.Dialog

@Composable
fun CopyDialog(progress: Float, showDialog: MutableState<Boolean>) {
    if (progress >= 1f){
        showDialog.value = false
    }
    Dialog(onDismissRequest = {  }) {
        LinearProgressIndicator(
            progress = { progress },
        )
    }
}