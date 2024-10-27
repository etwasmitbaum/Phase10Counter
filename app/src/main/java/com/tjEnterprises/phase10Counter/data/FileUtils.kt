package com.tjEnterprises.phase10Counter.data

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.compose.runtime.MutableFloatState
import java.io.File
import java.io.FileInputStream
import java.io.IOException

class FileUtils {
    companion object {
        // Return true, if successfully
        fun copyFileWithUri(
            context: Context, sourceUri: Uri, destinationUri: Uri, progress: MutableFloatState
        ): Boolean {
            try {
                val contentResolver: ContentResolver = context.contentResolver

                // Open a ParcelFileDescriptor for the source URI
                val sourcePFD = contentResolver.openFileDescriptor(sourceUri, "r")

                // Open an InputStream to read from the source URI
                val inputStream = FileInputStream(sourcePFD!!.fileDescriptor)

                // Open an OutputStream to write to the destination URI
                val outputStream = contentResolver.openOutputStream(destinationUri)

                // Copy data from the input stream to the output stream

                val sourceFileSize = sourceUri.path?.let { File(it).length() }
                val buffer = ByteArray(1024)
                var bytesRead: Int
                var bytesReadTotal = 0

                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    // display progress
                    bytesReadTotal += bytesRead
                    if (sourceFileSize != null) {
                        progress.floatValue = ((bytesReadTotal.toFloat() / sourceFileSize.toFloat()) * 100)
                    } else {
                        return false
                    }

                    outputStream?.write(buffer, 0, bytesRead)
                }

                // Close the streams
                inputStream.close()
                outputStream?.close()

                // Close the ParcelFileDescriptor
                sourcePFD.close()

                progress.floatValue = 1f
                return true // Successfully copied the file
            } catch (e: IOException) {
                e.printStackTrace()
                return false // Failed to copy the file
            }
        }
    }
}