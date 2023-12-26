package com.tjEnterprises.phase10Counter.data

data class PlayerModel (
    val id: Long,
    val name: String,
    val points: Long,
    val pointHistory: List<PointHistoryModel>,
    val phases: List<PhasesModel>
)