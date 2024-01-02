package com.tjEnterprises.phase10Counter.ui.about

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tjEnterprises.phase10Counter.R
import com.tjEnterprises.phase10Counter.ui.AppLicenceUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import javax.inject.Inject

@HiltViewModel
class AppLicenceViewModel @Inject constructor() : ViewModel() {
    private val _licence: MutableStateFlow<AppLicenceUiState> = MutableStateFlow(
        AppLicenceUiState.AppLicenceLoading
    )
    val license: StateFlow<AppLicenceUiState> = _licence

    fun loadData(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val inputStream: InputStream = context.resources.openRawResource(R.raw.license)
                val reader = InputStreamReader(inputStream)
                val bufferedReader = BufferedReader(reader)
                val stringBuilder = StringBuilder()
                var line: String? = bufferedReader.readLine()
                while (line != null) {
                    stringBuilder.append(line)
                    stringBuilder.append("\n")
                    line = bufferedReader.readLine()
                }
                _licence.value = AppLicenceUiState.AppLicenceSuccess(stringBuilder.toString())
            } catch (e: IOException) {
                e.printStackTrace()
                _licence.value = AppLicenceUiState.AppLicenceError(e)
            }
        }
    }
}