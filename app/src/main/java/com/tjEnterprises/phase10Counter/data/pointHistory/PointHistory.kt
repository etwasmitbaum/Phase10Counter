package com.tjEnterprises.phase10Counter.data.pointHistory

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PointHistory(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "point") var point: Int,
    @ColumnInfo(name = "player_id") var player_id: Int,
)
