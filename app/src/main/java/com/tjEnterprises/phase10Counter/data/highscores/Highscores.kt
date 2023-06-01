package com.tjEnterprises.phase10Counter.data.highscores

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
@Deprecated("This is only kept, to not change the existing Player Database. All Highscores will be stored in \"GlobalHighscores\". This is part of the backup and restore functionality")
data class Highscores(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "playerName") val playerName: String,
    @ColumnInfo(name = "punkte") val punkte: Int,
    @ColumnInfo(name = "date") val date: Date
)
