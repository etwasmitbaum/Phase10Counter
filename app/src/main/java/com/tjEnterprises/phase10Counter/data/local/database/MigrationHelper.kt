package com.tjEnterprises.phase10Counter.data.local.database

import android.content.Context
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.tjEnterprises.phase10Counter.data.legacy.GlobalDataDatabase
import com.tjEnterprises.phase10Counter.data.legacy.GlobalHighscores
import com.tjEnterprises.phase10Counter.data.local.models.GameType
import java.util.Date

object Migration1To2 : Migration(startVersion = 1, endVersion = 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        try {
            db.execSQL("CREATE TABLE `Highscores` (`id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, `playerName` TEXT NOT NULL, `punkte` INTEGER NOT NULL, `date` INTEGER NOT NULL)")
        } catch (_: Exception) {
        }
        db.execSQL("ALTER TABLE PlayerData ADD COLUMN gameWon INTEGER NOT NULL DEFAULT false")
    }
}

class Migration2To3(private val context: Context) : Migration(startVersion = 2, endVersion = 3) {
    override fun migrate(db: SupportSQLiteDatabase) {

        val globalHighscores = GlobalDataDatabase.getInstance(context)

        db.execSQL("CREATE TABLE `PointHistory` (`id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, `player_id` INTEGER NOT NULL, `point` INTEGER NOT NULL)")

        val highScoreCursor = db.query("SELECT * FROM Highscores ORDER BY id ASC")

        if (highScoreCursor.moveToFirst()) {
            do {
                val playerName =
                    highScoreCursor.getString(highScoreCursor.getColumnIndexOrThrow("playerName"))
                val points = highScoreCursor.getInt(highScoreCursor.getColumnIndexOrThrow("punkte"))
                val timestamp =
                    highScoreCursor.getInt(highScoreCursor.getColumnIndexOrThrow("date"))
                globalHighscores.GlobalHighscoresDao().insertHighscore(
                    GlobalHighscores(
                        0, playerName, points, Date(timestamp.toLong())
                    )
                )
            } while (highScoreCursor.moveToNext())
        }
    }
}

class Migration3To4(private val context: Context) : Migration(startVersion = 3, endVersion = 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        val globalHighscores = GlobalDataDatabase.getInstance(context)
        val oldPlayersCursor = db.query("SELECT * FROM PlayerData ORDER BY id ASC")
        val oldPointHistoryCursor = db.query("SELECT * FROM PointHistory ORDER BY id ASC")

        data class OldPlayer(val id: Int, val name: String, val punkte: Int, val phasen: String)

        // Point needs to be var, so it can be modified in case of sum != PlayerData Punkte
        data class OldPointHistory(val id: Int, var point: Int, val playerId: Int, val time: Long)

        val timeNow = System.currentTimeMillis()

        // Store old data
        val oldHighscores = globalHighscores.GlobalHighscoresDao().getHighscoreList()
        val oldPlayers = mutableListOf<OldPlayer>()
        val oldPointHistory = mutableListOf<OldPointHistory>()

        if (oldPlayersCursor.moveToFirst()) {
            do {
                val playerId = oldPlayersCursor.getInt(oldPlayersCursor.getColumnIndexOrThrow("id"))
                val playerName =
                    oldPlayersCursor.getString(oldPlayersCursor.getColumnIndexOrThrow("name"))
                val playerPunkte =
                    oldPlayersCursor.getInt(oldPlayersCursor.getColumnIndexOrThrow("punkte"))
                val playerPhasen =
                    oldPlayersCursor.getString(oldPlayersCursor.getColumnIndexOrThrow("phasen"))
                oldPlayers.add(OldPlayer(playerId, playerName, playerPunkte, playerPhasen))
            } while (oldPlayersCursor.moveToNext())
        }

        if (oldPointHistoryCursor.moveToFirst()) {
            do {
                val pointId =
                    oldPointHistoryCursor.getInt(oldPointHistoryCursor.getColumnIndexOrThrow("id"))
                val point =
                    oldPointHistoryCursor.getInt(oldPointHistoryCursor.getColumnIndexOrThrow("point"))
                val playerId =
                    oldPointHistoryCursor.getInt(oldPointHistoryCursor.getColumnIndexOrThrow("player_id"))
                val time = timeNow + 1
                oldPointHistory.add(OldPointHistory(pointId, point, playerId, time))
            } while (oldPointHistoryCursor.moveToNext())
        }

        // Delete old Database
        db.execSQL("DROP TABLE PlayerData")
        db.execSQL("DROP TABLE PointHistory")
        db.execSQL("DROP TABLE Highscores")

        // Create new Database
        db.execSQL("CREATE TABLE IF NOT EXISTS `Game` (`name` TEXT NOT NULL, `game_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `timestampCreated` INTEGER NOT NULL, `timestampModified` INTEGER NOT NULL)")

        db.execSQL("CREATE TABLE IF NOT EXISTS `Player` (`game_id` INTEGER NOT NULL, `name` TEXT NOT NULL, `player_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, FOREIGN KEY(`game_id`) REFERENCES `Game`(`game_id`) ON UPDATE CASCADE ON DELETE CASCADE )")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_Player_game_id` ON `Player` (`game_id`)")

        db.execSQL("CREATE TABLE IF NOT EXISTS `PointHistory` (`point` INTEGER NOT NULL, `player_id` INTEGER NOT NULL, `game_id` INTEGER NOT NULL, `pointId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `timestampCreated` INTEGER NOT NULL, FOREIGN KEY(`game_id`) REFERENCES `Game`(`game_id`) ON UPDATE CASCADE ON DELETE CASCADE , FOREIGN KEY(`player_id`) REFERENCES `Player`(`player_id`) ON UPDATE CASCADE ON DELETE CASCADE )")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_PointHistory_game_id` ON `PointHistory` (`game_id`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_PointHistory_player_id` ON `PointHistory` (`player_id`)")

        db.execSQL("CREATE TABLE IF NOT EXISTS `Highscore` (`playerName` TEXT NOT NULL, `points` INTEGER NOT NULL, `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `timestamp` INTEGER NOT NULL)")

        db.execSQL("CREATE TABLE IF NOT EXISTS `Phases` (`player_id` INTEGER NOT NULL, `game_id` INTEGER NOT NULL, `phase` INTEGER NOT NULL, `open` INTEGER NOT NULL, `timestampModified` INTEGER NOT NULL, PRIMARY KEY(`game_id`, `player_id`, `phase`), FOREIGN KEY(`game_id`) REFERENCES `Game`(`game_id`) ON UPDATE CASCADE ON DELETE CASCADE , FOREIGN KEY(`player_id`) REFERENCES `Player`(`player_id`) ON UPDATE CASCADE ON DELETE CASCADE )")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_Phases_game_id` ON `Phases` (`game_id`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_Phases_player_id` ON `Phases` (`player_id`)")

        // Insert old data to new
        oldHighscores.forEach { highscore ->
            val playerName = highscore.playerName
            val point = highscore.punkte.toLong()
            val timeStamp = highscore.date.time

            db.execSQL("INSERT INTO 'Highscore' (playerName, points, timestamp) VALUES ('$playerName', '$point', '$timeStamp')")
        }

        // Only create game, if players existed before
        if (oldPlayers.isNotEmpty()) {
            db.execSQL("INSERT INTO 'Game' (name, timestampCreated, timestampModified) VALUES ('Game 1', $timeNow, $timeNow)")
            // get created gameId so nothing can go wrong
            val gameCursor = db.query("SELECT * FROM Game")
            gameCursor.moveToFirst()
            val gameId = gameCursor.getLong(gameCursor.getColumnIndexOrThrow("game_id"))

            oldPlayers.forEach { player ->
                val pointsHistoryOfPlayer = oldPointHistory.filter { it.playerId == player.id }
                val playerName = player.name
                db.execSQL("INSERT INTO 'Player' (game_id, name) VALUES($gameId, '$playerName')")

                // get created playerId
                val newPlayerIdCursor = db.query("SELECT last_insert_rowid() AS player_id")
                newPlayerIdCursor.moveToFirst()
                val newPlayerId =
                    newPlayerIdCursor.getLong(newPlayerIdCursor.getColumnIndexOrThrow("player_id"))

                // Handle if PlayerData Punkte != sum of PointHistory
                pointsHistoryOfPlayer.run {
                    if (isEmpty()) {
                        listOf(OldPointHistory(0, player.punkte, player.id, timeNow))
                    } else {
                        var sum = 0
                        forEach { point ->
                            sum += point.point
                        }
                        if (sum != player.punkte) {
                            pointsHistoryOfPlayer[0].point += player.punkte - sum
                            pointsHistoryOfPlayer
                        } else {
                            pointsHistoryOfPlayer
                        }
                    }
                }.let { updatedPointsHistoryOfPlayer ->
                    updatedPointsHistoryOfPlayer.forEach { point ->
                        val pointValue = point.point
                        val timeStamp = point.time
                        db.execSQL("INSERT INTO 'PointHistory' (point, player_id, game_id, timestampCreated) VALUES($pointValue, $newPlayerId, $gameId, $timeStamp)")
                    }
                }

                val phasesOpen = mutableListOf<Boolean>()
                val openPhasesOfPlayer =
                    "\\d+".toRegex().findAll(player.phasen).map { it.value.toInt() }
                for (i in 0..9) {

                    // if phase found from string, do not check box
                    if (openPhasesOfPlayer.find { it == (i + 1) } != null) {
                        phasesOpen.add(i, true)
                    } else {
                        phasesOpen.add(i, false)
                    }
                }

                phasesOpen.forEachIndexed { idx, open ->
                    db.execSQL("INSERT INTO 'Phases' (player_id, game_id, phase, open, timestampModified) VALUES($newPlayerId, $gameId, $idx, $open, $timeNow)")
                }
            }
        }
    }
}

object Migration4To5 : Migration(startVersion = 4, endVersion = 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        val defaultGameType = GameType.DEFAULT_GAMETYPE_KEY
        db.execSQL("ALTER TABLE Game ADD COLUMN gameType TEXT NOT NULL DEFAULT '$defaultGameType'" )
    }

}