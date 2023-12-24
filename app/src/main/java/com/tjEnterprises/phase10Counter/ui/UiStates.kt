package com.tjEnterprises.phase10Counter.ui

import com.tjEnterprises.phase10Counter.data.GameModel
import com.tjEnterprises.phase10Counter.data.PlayerModel
import com.tjEnterprises.phase10Counter.data.local.database.PointHistory

sealed interface PlayerUiState {
    object PlayersLoading : PlayerUiState
    data class PlayersError(val throwable: Throwable) : PlayerUiState
    data class PlayersSuccess(val data: List<PlayerModel>) : PlayerUiState
}

sealed interface GamesUiState {
    object GamesLoading : GamesUiState
    data class GamesError(val throwable: Throwable) : GamesUiState
    data class GamesSuccess(val data: List<GameModel>) : GamesUiState
}

sealed interface GameUiState {
    object GameLoading : GameUiState
    data class GameError(val throwable: Throwable) : GameUiState
    data class GameSuccess(val data: GameModel) : GameUiState
}

sealed interface PointHistoryUiState {
    object PointHistoryLoading : PointHistoryUiState
    data class PointHistoryError(val throwable: Throwable) : PointHistoryUiState
    data class PointHistorySuccess(val data: List<PointHistory>) : PointHistoryUiState
}
