package com.tjEnterprises.phase10Counter.data.local.models

data class PlayerModel(
    val playerId: Long,
    val gameId: Long,
    val name: String,
    val pointHistory: List<Long>,
    val pointSum: Long,
    val phasesOpen: List<Boolean>
)