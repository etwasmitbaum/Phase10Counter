package com.tjEnterprises.phase10Counter.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tjEnterprises.phase10Counter.data.local.repositories.DatabaseRepository.DefaultDatabaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppBaseScreenViewModel @Inject constructor(databaseRepository: DefaultDatabaseRepository) : ViewModel() {
    private val _gamesCount = MutableStateFlow<Long>(-1L)
    val gamesCount: StateFlow<Long> = _gamesCount

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _gamesCount.value = databaseRepository.getGamesCount()
        }
    }
}