package com.tjEnterprises.phase10Counter.data


data class GameModel(
    val id: Long,
    val name: String,
    val created: Long,
    val modified: Long,
    val players: List<PlayerModel>
)