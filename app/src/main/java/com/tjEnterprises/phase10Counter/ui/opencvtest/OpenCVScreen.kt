package com.tjEnterprises.phase10Counter.ui.opencvtest

import android.content.Context
import android.graphics.Bitmap
import androidx.camera.core.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.tjEnterprises.phase10Counter.ui.component.DefaultScaffoldNavigation

@Composable
fun OpenCVScreen(
    modifier: Modifier = Modifier,
    viewModel: OpenCVViewModel = hiltViewModel(),
    openDrawer: () -> Unit,
) {
    val lensFacing by viewModel.lensFacing.collectAsState()
    val displayedBitmap by viewModel.displayedBitmap.collectAsState()

    viewModel.initOpenCV()

    OpenCVScreen(openDrawer = openDrawer, startCamera = { context, lifecycleOwner, preview ->
        viewModel.startCamera(context, lifecycleOwner, preview)
    }, switchCamera = { viewModel.switchCamera() }, lensFacing = lensFacing, displayedBitmap = displayedBitmap
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
internal fun OpenCVScreen(
    openDrawer: () -> Unit,
    startCamera: (Context, LifecycleOwner, (Preview) -> Unit) -> Unit,
    switchCamera: () -> Unit,
    lensFacing: Int,
    displayedBitmap: Bitmap
) {
    DefaultScaffoldNavigation(title = "OpenCV Test", openDrawer = openDrawer) { scaffoldModifier ->
        Surface(
            modifier = scaffoldModifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth()
        ) {

            // Camera permission state
            val cameraPermissionState = rememberPermissionState(
                android.Manifest.permission.CAMERA
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (cameraPermissionState.status.isGranted) {
                    Text("Camera permission Granted")
                    CameraXPreviewScreen(
                        startCamera = startCamera,
                        switchCamera = switchCamera,
                        lensFacing = lensFacing,
                        displayedBitmap = displayedBitmap
                    )
                } else {
                    val textToShow = if (cameraPermissionState.status.shouldShowRationale) {
                        // If the user has denied the permission but the rationale can be shown,
                        // then gently explain why the app requires this permission
                        "The camera is important for this app. Please grant the permission."
                    } else {
                        // If it's the first time the user lands on this feature, or the user
                        // doesn't want to be asked again for this permission, explain that the
                        // permission is required
                        "Camera permission required for this feature to be available. " + "Please grant the permission"
                    }
                    Text(text = textToShow, textAlign = TextAlign.Center)
                    Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                        Text("Request permission")
                    }
                }
            }

        }
    }
}
