package com.tjEnterprises.phase10Counter.data.local

data class GameModel(
    val gameId: Long,
    val name: String,
    val created: Long,
    val modified: Long,
    val players: List<PlayerModel>
)