package com.tjEnterprises.phase10Counter.ui.opencvtest

import android.content.Context
import android.graphics.Bitmap
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Composable
fun CameraXPreviewScreen(
    modifier: Modifier = Modifier,
    startCamera: (Context, LifecycleOwner, (Preview) -> Unit) -> Unit,
    switchCamera: () -> Unit,
    lensFacing: Int,
    displayedBitmap: Bitmap
) {

    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val previewView = remember {
        PreviewView(context)
    }

    LaunchedEffect(lensFacing) {
        startCamera(context, lifecycleOwner) { preview ->
            preview.setSurfaceProvider(previewView.surfaceProvider)
        }
    }

    Box {
        AndroidView(factory = { previewView }, modifier = modifier.wrapContentSize())

        Box(modifier = Modifier.align(Alignment.BottomEnd)) {
            Column {
                IconButton(
                    onClick = { /*TODO*/ },
                    modifier = Modifier
                        .padding(32.dp)
                        .size(40.dp)
                        .background(color = MaterialTheme.colorScheme.background)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "OK",
                        modifier = Modifier.size(40.dp)
                    )
                }

                // TODO Add Flashlight button
                // TODO Make it possible to select other than just two cameras
                IconButton(
                    onClick = switchCamera,
                    modifier = Modifier
                        .padding(32.dp)
                        .size(40.dp)
                        .background(color = MaterialTheme.colorScheme.background)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Switch Camera",
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

        }

    }
    Image(
        bitmap = displayedBitmap.asImageBitmap(),
        contentDescription = "OpenCV Image",
        modifier = Modifier.wrapContentSize()
    )

}
