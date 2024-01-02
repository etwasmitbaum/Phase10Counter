package com.tjEnterprises.phase10Counter.ui

import com.tjEnterprises.phase10Counter.data.local.database.Highscore
import com.tjEnterprises.phase10Counter.data.local.models.GameModel
import com.tjEnterprises.phase10Counter.data.local.models.SettingsModel

sealed interface SelectGameUiState {
    object SelectGameLoading : SelectGameUiState
    data class SelectGameError(val throwable: Throwable) : SelectGameUiState
    data class SelectGameSuccess(val games: List<GameModel>, val settings: SettingsModel) : SelectGameUiState
}

sealed interface GameUiState {
    object GameLoading : GameUiState
    data class GameError(val throwable: Throwable) : GameUiState
    data class GameSuccess(val game: GameModel) : GameUiState
}

sealed interface SettingsUiState {
    object SettingsLoading : SettingsUiState
    data class SettingsError(val throwable: Throwable) : SettingsUiState
    data class SettingsSuccess(val settings: SettingsModel) : SettingsUiState
}

sealed interface HighscoresUiState {
    object HighscoresLoading : HighscoresUiState
    data class HighscoresError(val throwable: Throwable) : HighscoresUiState
    data class HighscoresSuccess(val highscores: List<Highscore>) : HighscoresUiState
}

sealed interface AppLicenceUiState {
    object AppLicenceLoading : AppLicenceUiState
    data class AppLicenceError(val throwable: Throwable) : AppLicenceUiState
    data class AppLicenceSuccess(val license: String) : AppLicenceUiState
}