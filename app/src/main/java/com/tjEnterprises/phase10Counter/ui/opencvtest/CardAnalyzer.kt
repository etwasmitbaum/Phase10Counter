package com.tjEnterprises.phase10Counter.ui.opencvtest

import android.graphics.Bitmap
import android.graphics.ImageFormat
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.tjEnterprises.phase10Counter.ui.opencvtest.Utils.yuvToRgba
import org.opencv.android.Utils
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.MatOfPoint2f
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import kotlin.math.abs

typealias OpencvListener = (message: String, bitmap: Bitmap) -> Unit

class CardAnalyzer(private val listener: OpencvListener) : ImageAnalysis.Analyzer {

    @OptIn(ExperimentalGetImage::class) override fun analyze(image: ImageProxy) {
        image.image?.let {
            if (it.format == ImageFormat.YUV_420_888 && it.planes.size == 3) {
                val rgbMat = it.yuvToRgba()
                val buf = Mat()

                //gray
                Imgproc.cvtColor(rgbMat, buf, Imgproc.COLOR_RGBA2GRAY)

                // blur
                Imgproc.GaussianBlur(buf, buf, Size(5.0, 5.0), 0.0)

                Imgproc.threshold(buf, buf, 127.0, 255.0, Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_OTSU )

                val edges = Mat()
                Imgproc.Canny(buf, edges, 50.0, 150.0)

                val contours = mutableListOf<MatOfPoint>()
                val hierarchy = Mat()
                Imgproc.findContours(edges, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE)

                // Draw rectangles around contours
                for (cnt in contours) {
                    // Filter contours (similar logic as Python example)
                    val cnt2f = MatOfPoint2f()
                    cnt.convertTo(cnt2f, CvType.CV_32F)

                    val area = Imgproc.contourArea(cnt)
                    val aspectRatio = abs(1 - area / (Imgproc.arcLength(cnt2f, true) * Imgproc.arcLength(cnt2f, true)))
                    if (area > 50 && aspectRatio > 0.2) {
                        val rect = Imgproc.boundingRect(cnt)
                        // Draw rectangle
                        Imgproc.rectangle(buf, rect.tl(), rect.br(), Scalar(0.0, 255.0, 0.0), 2)
                    }
                }

                val bmp = Bitmap.createBitmap(buf.cols(), buf.rows(), Bitmap.Config.ARGB_8888)
                val message = "Frame"

                Utils.matToBitmap(buf, bmp)

                listener(message, bmp)
            }
        }

        image.close()
    }
}