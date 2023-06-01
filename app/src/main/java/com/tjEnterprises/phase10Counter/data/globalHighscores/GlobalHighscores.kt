package com.tjEnterprises.phase10Counter.data.globalHighscores

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class GlobalHighscores(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "playerName") val playerName: String,
    @ColumnInfo(name = "punkte") val punkte: Int,
    @ColumnInfo(name = "date") val date: Date
)
