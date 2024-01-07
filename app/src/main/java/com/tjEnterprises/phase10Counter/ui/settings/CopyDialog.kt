package com.tjEnterprises.phase10Counter.ui.settings

import android.widget.Toast
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import com.tjEnterprises.phase10Counter.R

@Composable
fun CopyDialog(progress: Float, showDialog: MutableState<Boolean>) {
    if (progress >= 1f){
        showDialog.value = false
    }
    Dialog(onDismissRequest = {  }) {
        LinearProgressIndicator(progress = progress)
    }
}