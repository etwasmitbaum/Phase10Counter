package com.tjEnterprises.phase10Counter.data.player

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PlayerData(
    @PrimaryKey(autoGenerate = false) val id: Int,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "punkte") var punkte: Int,
    @ColumnInfo(name = "phasen") var phasen: String

)
