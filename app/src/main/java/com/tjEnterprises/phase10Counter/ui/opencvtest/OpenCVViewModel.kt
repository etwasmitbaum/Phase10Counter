package com.tjEnterprises.phase10Counter.ui.opencvtest

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.view.Surface
import android.view.WindowManager
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.opencv.android.OpenCVLoader
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@HiltViewModel
class OpenCVViewModel @Inject constructor() : ViewModel() {

    // TODO store last used lens
    private val _lensFacing = MutableStateFlow(CameraSelector.LENS_FACING_BACK)
    val lensFacing: StateFlow<Int> = _lensFacing

    private val _displayedBitmap = MutableStateFlow(
        Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
    )
    val displayedBitmap: StateFlow<Bitmap> = _displayedBitmap

    private var preview by mutableStateOf<Preview?>(null)
    private var cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()


    init {
        preview = Preview.Builder().build()
    }

    fun initOpenCV(): Boolean {
        return OpenCVLoader.initLocal()
    }

    fun startCamera(
        context: Context, lifecycleOwner: LifecycleOwner, onPreviewReady: (Preview) -> Unit
    ) {
        viewModelScope.launch {
            val cameraxSelector =
                CameraSelector.Builder().requireLensFacing(_lensFacing.value).build()
            val cameraProvider = context.getCameraProvider()
            cameraProvider.unbindAll()

            val resStrat = ResolutionStrategy(android.util.Size(1440, 1080), ResolutionStrategy.FALLBACK_RULE_CLOSEST_HIGHER_THEN_LOWER)
            val resSelect = ResolutionSelector.Builder()
                .setAllowedResolutionMode(ResolutionSelector.PREFER_HIGHER_RESOLUTION_OVER_CAPTURE_RATE)
                .setResolutionStrategy(resStrat)
                .build()

            // TODO Maybe this would save some hassle?
            // setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
            // setOutputImageRotationEnabled(true)
            val opencvAnalyzer = ImageAnalysis.Builder().setResolutionSelector(resSelect).build().also {
                it.setAnalyzer(cameraExecutor, CardAnalyzer { _, bitmap ->

                    val deviceRotation = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        context.display?.rotation
                    } else {
                        val windowManager =
                            context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                        windowManager.defaultDisplay.rotation
                    }
                    val rotation = when (deviceRotation) {
                        Surface.ROTATION_0 -> 90
                        Surface.ROTATION_90 -> 0
                        Surface.ROTATION_180 -> 270
                        Surface.ROTATION_270 -> 180
                        else -> 0
                    }

                    _displayedBitmap.value = if (rotation == 0) bitmap else {
                        val matrix = android.graphics.Matrix()
                        matrix.postRotate(rotation.toFloat())
                        Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                    }
                })
            }

            cameraProvider.bindToLifecycle(lifecycleOwner, cameraxSelector, preview, opencvAnalyzer)

            onPreviewReady(preview!!)
        }

    }

    fun switchCamera() {
        // TODO make it possible to switch between more than back and front cam
        _lensFacing.value =
            if (_lensFacing.value == CameraSelector.LENS_FACING_BACK) CameraSelector.LENS_FACING_FRONT else CameraSelector.LENS_FACING_BACK
    }

    override fun onCleared() {
        super.onCleared()
        cameraExecutor.shutdown()
    }

    private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
        suspendCoroutine { continuation ->
            ProcessCameraProvider.getInstance(this).also { cameraProvider ->
                cameraProvider.addListener({
                    continuation.resume(cameraProvider.get())
                }, ContextCompat.getMainExecutor(this))
            }
        }
}