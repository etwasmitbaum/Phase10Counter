package com.tjEnterprises.phase10Counter.ui.settings

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.compose.runtime.MutableFloatState
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tjEnterprises.phase10Counter.data.local.database.AppDatabase
import com.tjEnterprises.phase10Counter.data.local.models.SettingsModel
import com.tjEnterprises.phase10Counter.data.local.repositories.DatabaseRepository.DefaultDatabaseRepository
import com.tjEnterprises.phase10Counter.data.local.repositories.SettingsRepository
import com.tjEnterprises.phase10Counter.ui.SettingsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val databaseRepository: DefaultDatabaseRepository
) : ViewModel() {

    private val _copyError: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val copyError: StateFlow<Boolean> = _copyError

    val settingsUiState: StateFlow<SettingsUiState> =
        settingsRepository.settingsModelFlow.map<SettingsModel, SettingsUiState.SettingsSuccess>(
            SettingsUiState::SettingsSuccess
        ).catch { SettingsUiState.SettingsError(it) }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SettingsUiState.SettingsLoading
        )

    fun updateCheckForUpdates(checkForUpdates: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepository.updateCheckForUpdates(checkForUpdates)
        }
    }

    fun updateUseDynamicColors(useDynamicColors: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepository.updateUseDynamicColors(useDynamicColors)
        }
    }

    fun updateUseSystemTheme(useSystemTheme: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepository.updateUseSystemTheme(useSystemTheme)
        }
    }

    fun updateUseDarkTheme(useDarkTheme: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepository.updateUseDarkTheme(useDarkTheme)
        }
    }

    fun updateDontChangeUiWideScreen(dontChangeUiWideScreen: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepository.updateDontChangeUiWideScreen(dontChangeUiWideScreen)
        }
    }

    fun backUpDatabase(context: Context, pickedUri: Uri, progress: MutableFloatState) {
        databaseRepository.closeDatabase()
        viewModelScope.launch(Dispatchers.IO) {
            copyFileWithUri(
                context = context,
                sourceUri = context.getDatabasePath(AppDatabase.getName()).toUri(),
                destinationUri = pickedUri,
                progress = progress
            )
        }
    }

    fun restoreDatabase(context: Context, pickedUri: Uri, progress: MutableFloatState) {
        databaseRepository.closeDatabase()
        viewModelScope.launch(Dispatchers.IO) {
            copyFileWithUri(
                context = context,
                sourceUri = pickedUri,
                destinationUri = context.getDatabasePath(AppDatabase.getName()).toUri(),
                progress = progress
            )
        }
    }

    private fun copyFileWithUri(
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
                    _copyError.value = true
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
            _copyError.value = false
            return true // Successfully copied the file
        } catch (e: IOException) {
            e.printStackTrace()
            _copyError.value = true
            return false // Failed to copy the file
        }
    }
}

object WasCopyRestore {
    const val WAS_NEITHER = 0
    const val WAS_BACKUP = 1
    const val WAS_RESTORE = 2
}