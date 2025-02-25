package com.tjEnterprises.phase10Counter.data.local.models

data class GameModel(
    val gameId: Long,
    val name: String,
    val gameType: GameType.Type,
    val created: Long,
    val modified: Long,
    val players: List<PlayerModel>
)