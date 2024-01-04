package com.tjEnterprises.phase10Counter.ui.database

import android.content.Context
import android.database.Cursor
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.tjEnterprises.phase10Counter.data.legacy.GlobalDataDatabase
import com.tjEnterprises.phase10Counter.data.legacy.GlobalHighscores
import com.tjEnterprises.phase10Counter.data.local.database.AppDatabase
import com.tjEnterprises.phase10Counter.data.local.database.Migration2To3
import com.tjEnterprises.phase10Counter.data.local.database.Migration3To4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.Date

@Suppress("VARIABLE_WITH_REDUNDANT_INITIALIZER")
@RunWith(AndroidJUnit4::class)
class MigrationTest {
    private val TEST_DB = "migration-test"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    @Throws(IOException::class)
    fun migrate2To3WithoutData() {
        val context: Context = ApplicationProvider.getApplicationContext()

        var db = helper.createDatabase(TEST_DB, 2).apply {
            // You can't use DAO classes because they expect the latest schema.
            // Prepare for the next version.
            close()
        }

        db = helper.runMigrationsAndValidate(TEST_DB, 3, true, Migration2To3(context = context))
    }

    @Test
    @Throws(IOException::class)
    fun migrate2To3WithData() {
        val context: Context = ApplicationProvider.getApplicationContext()

        var db = helper.createDatabase(TEST_DB, 2).apply {
            // You can't use DAO classes because they expect the latest schema.
            execSQL("INSERT INTO PlayerData VALUES (1, 'Player 1', 256, '1, 2, 3, 4, 5, 6, 7, 8, 9, 10', 0)")
            execSQL("INSERT INTO PlayerData VALUES (2, 'Player 2', 512, '1, 2, 3, 8, 9, 10', 0)")
            execSQL("INSERT INTO PlayerData VALUES (3, 'Player 3', 1024, '', 1)")

            execSQL("INSERT INTO Highscores (playerName, punkte, date) VALUES ('Player 1', 128, 1704228890)")
            execSQL("INSERT INTO Highscores (playerName, punkte, date) VALUES ('Player 2', 64, 1704142490)")

            // Prepare for the next version.
            close()
        }

        db = helper.runMigrationsAndValidate(TEST_DB, 3, true, Migration2To3(context = context))
        val playersCursor = db.query("SELECT * FROM PlayerData ORDER BY id ASC")
        val highScoreCursor = db.query("SELECT * FROM Highscores ORDER BY id ASC")

        val globalHighscores = GlobalDataDatabase.getInstance(context)

        verifyDataMigration2To3(playersCursor, highScoreCursor, globalHighscores)
    }

    @Test
    @Throws(IOException::class)
    fun migrate3To4WithoutData() {
        val context: Context = ApplicationProvider.getApplicationContext()
        var db = helper.createDatabase(TEST_DB, 3).apply {
            // You can't use DAO classes because they expect the latest schema.
            // Prepare for the next version.
            close()
        }

        db = helper.runMigrationsAndValidate(
            TEST_DB, 4, true, Migration3To4(context = context)
        )
    }

    @Test
    @Throws(IOException::class)
    fun migrate3To4WithData() {
        val context: Context = ApplicationProvider.getApplicationContext()
        val globalHighscores = GlobalDataDatabase.getInstance(context)

        var db = helper.createDatabase(TEST_DB, 3).apply {
            // You can't use DAO classes because they expect the latest schema.
            execSQL("INSERT INTO PlayerData (id, name, punkte, phasen, gameWon) VALUES (1, 'Player 1', 256, '1, 2, 3, 4, 5, 6, 7, 8, 9, 10', 0)")
            execSQL("INSERT INTO PlayerData (id, name, punkte, phasen, gameWon) VALUES (2, 'Player 2', 512, '2, 3, 8, 9, 10', 0)")
            execSQL("INSERT INTO PlayerData (id, name, punkte, phasen, gameWon) VALUES (3, 'Player 3', 768, '', 1)")

            execSQL("INSERT INTO PointHistory (id, point, player_id) VALUES (1, 128, 1)")
            execSQL("INSERT INTO PointHistory (id, point, player_id) VALUES (2, 64, 1)")
            execSQL("INSERT INTO PointHistory (id, point, player_id) VALUES (3, 64, 1)")
            execSQL("INSERT INTO PointHistory (id, point, player_id) VALUES (4, 256, 2)")
            execSQL("INSERT INTO PointHistory (id, point, player_id) VALUES (5, 128, 2)")
            execSQL("INSERT INTO PointHistory (id, point, player_id) VALUES (6, 512, 3)")
            execSQL("INSERT INTO PointHistory (id, point, player_id) VALUES (7, 256, 3)")

            globalHighscores.GlobalHighscoresDao()
                .insertHighscore(GlobalHighscores(0, "Player 1", 128, Date(1704228890)))
            globalHighscores.GlobalHighscoresDao()
                .insertHighscore(GlobalHighscores(0, "Player 2", 64, Date(1704142490)))

            // Prepare for the next version.
            close()
        }

        db = helper.runMigrationsAndValidate(
            TEST_DB, 4, true, Migration3To4(context = context)
        )
        val playersCursor = db.query("SELECT * FROM Player ORDER BY player_id ASC")
        val highScoreCursor = db.query("SELECT * FROM Highscore ORDER BY id ASC")
        val gameCursor = db.query("SELECT * FROM Game ORDER BY game_id ASC")
        val pointHistoryCursor = db.query("SELECT * FROM PointHistory ORDER BY player_id ASC, pointId ASC")
        val phasesCursor = db.query("SELECT * FROM Phases ORDER BY player_id ASC, phase ASC")

        verifyDataMigration3To4(
            playersCursor,
            highScoreCursor,
            gameCursor,
            pointHistoryCursor,
            phasesCursor
        )
    }

    private fun verifyDataMigration3To4(
        playersCursor: Cursor,
        highscoreCursor: Cursor,
        gameCursor: Cursor,
        pointHistoryCursor: Cursor,
        phasesCursor: Cursor
    ) {
        // Verify Player data
        playersCursor.moveToFirst()
        val p1Name = playersCursor.getString(playersCursor.getColumnIndexOrThrow("name"))
        val p1GameId = playersCursor.getLong(playersCursor.getColumnIndexOrThrow("game_id"))
        assertEquals(p1Name, "Player 1")

        playersCursor.moveToNext()
        val p2Name = playersCursor.getString(playersCursor.getColumnIndexOrThrow("name"))
        val p2GameId = playersCursor.getLong(playersCursor.getColumnIndexOrThrow("game_id"))
        assertEquals(p2Name, "Player 2")

        playersCursor.moveToNext()
        val p3Name = playersCursor.getString(playersCursor.getColumnIndexOrThrow("name"))
        val p3GameId = playersCursor.getLong(playersCursor.getColumnIndexOrThrow("game_id"))
        assertEquals(p3Name, "Player 3")

        if (playersCursor.moveToNext()) {
            // This should not happen with 3 players added
            assertEquals(1, 2)
        }
        assertNotEquals(p1GameId, null)
        assertEquals(p1GameId, p2GameId)
        assertEquals(p2GameId, p3GameId)

        // Verify High Score data
        highscoreCursor.moveToFirst()
        val hs1PlayerName =
            highscoreCursor.getString(highscoreCursor.getColumnIndexOrThrow("playerName"))
        val hs1Points = highscoreCursor.getInt(highscoreCursor.getColumnIndexOrThrow("points"))
        val hs1Timestamp =
            highscoreCursor.getInt(highscoreCursor.getColumnIndexOrThrow("timestamp"))
        assertEquals(hs1PlayerName, "Player 1")
        assertEquals(hs1Points, 128)
        assertEquals(hs1Timestamp, 1704228890)

        highscoreCursor.moveToNext()
        val hs2PlayerName =
            highscoreCursor.getString(highscoreCursor.getColumnIndexOrThrow("playerName"))
        val hs2Points = highscoreCursor.getInt(highscoreCursor.getColumnIndexOrThrow("points"))
        val hs2Timestamp =
            highscoreCursor.getInt(highscoreCursor.getColumnIndexOrThrow("timestamp"))
        assertEquals(hs2PlayerName, "Player 2")
        assertEquals(hs2Points, 64)
        assertEquals(hs2Timestamp, 1704142490)
        if (highscoreCursor.moveToNext()) {
            // This should not happen with 3 High scores added
            assertEquals(1, 2)
        }

        // Verify Game data
        gameCursor.moveToFirst()
        val g1Name = gameCursor.getString(gameCursor.getColumnIndexOrThrow("name"))
        val g1Created = gameCursor.getLong(gameCursor.getColumnIndexOrThrow("timestampCreated"))
        val g1Modified = gameCursor.getLong(gameCursor.getColumnIndexOrThrow("timestampModified"))
        assertEquals(g1Name, "Game 1")
        assertNotEquals(0L, g1Created)
        assertNotEquals(0L, g1Modified)
        assertNotEquals(null, g1Created)
        assertNotEquals(null, g1Created)
        if(gameCursor.moveToNext()){
            // This should not happen with 1 Game added through migration
            assertEquals(1, 2)
        }

        // Verify PointHistory Data
        // PointHistory of Player 1
        pointHistoryCursor.moveToFirst()
        val ph1Point = pointHistoryCursor.getInt(pointHistoryCursor.getColumnIndexOrThrow("point"))
        val ph1PlayerId = pointHistoryCursor.getLong(pointHistoryCursor.getColumnIndexOrThrow("player_id"))
        val ph1GameId = pointHistoryCursor.getLong(pointHistoryCursor.getColumnIndexOrThrow("game_id"))
        val ph1TimeCreated = pointHistoryCursor.getLong(pointHistoryCursor.getColumnIndexOrThrow("game_id"))
        assertEquals(128, ph1Point)
        assertEquals(1, ph1PlayerId)
        assertNotEquals(0L, ph1TimeCreated)
        assertNotEquals(null, ph1TimeCreated)

        pointHistoryCursor.moveToNext()
        val ph2Point = pointHistoryCursor.getInt(pointHistoryCursor.getColumnIndexOrThrow("point"))
        val ph2PlayerId = pointHistoryCursor.getLong(pointHistoryCursor.getColumnIndexOrThrow("player_id"))
        val ph2GameId = pointHistoryCursor.getLong(pointHistoryCursor.getColumnIndexOrThrow("game_id"))
        val ph2TimeCreated = pointHistoryCursor.getLong(pointHistoryCursor.getColumnIndexOrThrow("game_id"))
        assertEquals(64, ph2Point)
        assertEquals(1, ph2PlayerId)
        assertNotEquals(0L, ph2TimeCreated)
        assertNotEquals(null, ph2TimeCreated)

        pointHistoryCursor.moveToNext()
        val ph3Point = pointHistoryCursor.getInt(pointHistoryCursor.getColumnIndexOrThrow("point"))
        val ph3PlayerId = pointHistoryCursor.getLong(pointHistoryCursor.getColumnIndexOrThrow("player_id"))
        val ph3GameId = pointHistoryCursor.getLong(pointHistoryCursor.getColumnIndexOrThrow("game_id"))
        val ph3TimeCreated = pointHistoryCursor.getLong(pointHistoryCursor.getColumnIndexOrThrow("game_id"))
        assertEquals(64, ph3Point)
        assertEquals(1, ph3PlayerId)
        assertNotEquals(0L, ph3TimeCreated)
        assertNotEquals(null, ph3TimeCreated)

        assertNotEquals(null, ph1GameId)
        assertEquals(ph1GameId, ph2GameId)
        assertEquals(ph2GameId, ph3GameId)

        assertEquals(256, ph1Point + ph2Point + ph3Point)

        // PointHistory of Player 2
        pointHistoryCursor.moveToNext()
        val ph4Point = pointHistoryCursor.getInt(pointHistoryCursor.getColumnIndexOrThrow("point"))
        val ph4PlayerId = pointHistoryCursor.getLong(pointHistoryCursor.getColumnIndexOrThrow("player_id"))
        val ph4GameId = pointHistoryCursor.getLong(pointHistoryCursor.getColumnIndexOrThrow("game_id"))
        val ph4TimeCreated = pointHistoryCursor.getLong(pointHistoryCursor.getColumnIndexOrThrow("game_id"))
        assertEquals(256 + 128, ph4Point)
        assertEquals(2, ph4PlayerId)
        assertNotEquals(0L, ph4TimeCreated)
        assertNotEquals(null, ph4TimeCreated)

        pointHistoryCursor.moveToNext()
        val ph5Point = pointHistoryCursor.getInt(pointHistoryCursor.getColumnIndexOrThrow("point"))
        val ph5PlayerId = pointHistoryCursor.getLong(pointHistoryCursor.getColumnIndexOrThrow("player_id"))
        val ph5GameId = pointHistoryCursor.getLong(pointHistoryCursor.getColumnIndexOrThrow("game_id"))
        val ph5TimeCreated = pointHistoryCursor.getLong(pointHistoryCursor.getColumnIndexOrThrow("game_id"))
        assertEquals(128, ph5Point)
        assertEquals(2, ph5PlayerId)
        assertNotEquals(0L, ph5TimeCreated)
        assertNotEquals(null, ph5TimeCreated)

        assertEquals(512, ph4Point + ph5Point)

        // PointHistory of Player 3
        pointHistoryCursor.moveToNext()
        val ph6Point = pointHistoryCursor.getInt(pointHistoryCursor.getColumnIndexOrThrow("point"))
        val ph6PlayerId = pointHistoryCursor.getLong(pointHistoryCursor.getColumnIndexOrThrow("player_id"))
        val ph6GameId = pointHistoryCursor.getLong(pointHistoryCursor.getColumnIndexOrThrow("game_id"))
        val ph6TimeCreated = pointHistoryCursor.getLong(pointHistoryCursor.getColumnIndexOrThrow("game_id"))
        assertEquals(512, ph6Point)
        assertEquals(3, ph6PlayerId)
        assertNotEquals(0L, ph6TimeCreated)
        assertNotEquals(null, ph6TimeCreated)

        pointHistoryCursor.moveToNext()
        val ph7Point = pointHistoryCursor.getInt(pointHistoryCursor.getColumnIndexOrThrow("point"))
        val ph7PlayerId = pointHistoryCursor.getLong(pointHistoryCursor.getColumnIndexOrThrow("player_id"))
        val ph7GameId = pointHistoryCursor.getLong(pointHistoryCursor.getColumnIndexOrThrow("game_id"))
        val ph7TimeCreated = pointHistoryCursor.getLong(pointHistoryCursor.getColumnIndexOrThrow("game_id"))
        assertEquals(256, ph7Point)
        assertEquals(3, ph7PlayerId)
        assertNotEquals(0L, ph7TimeCreated)
        assertNotEquals(null, ph7TimeCreated)

        assertEquals(768, ph6Point + ph7Point)

        if (pointHistoryCursor.moveToNext()){
            // This should not happen with 7 PointHistories
            assertEquals(1, 2)
        }

        // Check that all PointHistory entries have the same GameId
        assertNotEquals(null, ph1GameId)
        assertEquals(ph1GameId, ph2GameId)
        assertEquals(ph2GameId, ph3GameId)
        assertEquals(ph3GameId, ph4GameId)
        assertEquals(ph4GameId, ph5GameId)
        assertEquals(ph5GameId, ph6GameId)
        assertEquals(ph6GameId, ph7GameId)

        // Verify Phases
        // Phases of Player 1
        phasesCursor.moveToFirst()
        val psOpenP1 = mutableListOf<Boolean>()
        val psPlayerIdP1 = mutableListOf<Long>()
        val psGameIdP1 = mutableListOf<Long>()
        val psNrP1 = mutableListOf<Int>()
        val psTimestampP1 = mutableListOf<Long>()
        for (i in 0..9){
            val open = phasesCursor.getInt(phasesCursor.getColumnIndexOrThrow("open"))
            val playerId = phasesCursor.getLong(phasesCursor.getColumnIndexOrThrow("player_id"))
            val gameId = phasesCursor.getLong(phasesCursor.getColumnIndexOrThrow("game_id"))
            val nr = phasesCursor.getInt(phasesCursor.getColumnIndexOrThrow("phase"))
            val timeStamp = phasesCursor.getLong(phasesCursor.getColumnIndexOrThrow("timestampModified"))
            psOpenP1.add(open == 1)
            psPlayerIdP1.add(playerId)
            psGameIdP1.add(gameId)
            psNrP1.add(nr)
            psTimestampP1.add(timeStamp)

            phasesCursor.moveToNext()
        }

        assertEquals(false, psOpenP1.contains(false))
        assertEquals(listOf(1L, 1L, 1L, 1L, 1L, 1L, 1L, 1L, 1L, 1L), psPlayerIdP1)
        assertEquals(listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), psNrP1)
        assertEquals(false, psTimestampP1.contains(0L))

        // Phases of Player 2
        val psOpenP2 = mutableListOf<Boolean>()
        val psPlayerIdP2 = mutableListOf<Long>()
        val psGameIdP2 = mutableListOf<Long>()
        val psNrP2 = mutableListOf<Int>()
        val psTimestampP2 = mutableListOf<Long>()
        for (i in 0..9){
            val open = phasesCursor.getInt(phasesCursor.getColumnIndexOrThrow("open"))
            val playerId = phasesCursor.getLong(phasesCursor.getColumnIndexOrThrow("player_id"))
            val gameId = phasesCursor.getLong(phasesCursor.getColumnIndexOrThrow("game_id"))
            val nr = phasesCursor.getInt(phasesCursor.getColumnIndexOrThrow("phase"))
            val timeStamp = phasesCursor.getLong(phasesCursor.getColumnIndexOrThrow("timestampModified"))
            psOpenP2.add(open == 1)
            psPlayerIdP2.add(playerId)
            psGameIdP2.add(gameId)
            psNrP2.add(nr)
            psTimestampP2.add(timeStamp)

            phasesCursor.moveToNext()
        }

        assertEquals(listOf(false, true, true, false, false, false, false, true, true , true), psOpenP2)
        assertEquals(listOf(2L, 2L, 2L, 2L, 2L, 2L, 2L, 2L, 2L, 2L), psPlayerIdP2)
        assertEquals(listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), psNrP2)
        assertEquals(false, psTimestampP2.contains(0L))

        // Phases of Player 3
        val psOpenP3 = mutableListOf<Boolean>()
        val psPlayerIdP3 = mutableListOf<Long>()
        val psGameIdP3 = mutableListOf<Long>()
        val psNrP3 = mutableListOf<Int>()
        val psTimestampP3 = mutableListOf<Long>()
        for (i in 0..9){
            val open = phasesCursor.getInt(phasesCursor.getColumnIndexOrThrow("open"))
            val playerId = phasesCursor.getLong(phasesCursor.getColumnIndexOrThrow("player_id"))
            val gameId = phasesCursor.getLong(phasesCursor.getColumnIndexOrThrow("game_id"))
            val nr = phasesCursor.getInt(phasesCursor.getColumnIndexOrThrow("phase"))
            val timeStamp = phasesCursor.getLong(phasesCursor.getColumnIndexOrThrow("timestampModified"))
            psOpenP3.add(open == 1)
            psPlayerIdP3.add(playerId)
            psGameIdP3.add(gameId)
            psNrP3.add(nr)
            psTimestampP3.add(timeStamp)

            phasesCursor.moveToNext()
        }

        assertEquals(false, psOpenP3.contains(true))
        assertEquals(listOf(3L, 3L, 3L, 3L, 3L, 3L, 3L, 3L, 3L, 3L), psPlayerIdP3)
        assertEquals(listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9), psNrP3)
        assertEquals(false, psTimestampP3.contains(0L))

        assertEquals(psGameIdP1, psGameIdP2)
        assertEquals(psGameIdP2, psGameIdP3)
    }

    private fun verifyDataMigration2To3(
        playersCursor: Cursor,
        highscoreCursor: Cursor,
        globalHighscores: GlobalDataDatabase
    ) {
        // Verify Player data
        playersCursor.moveToFirst()
        val player1Id = playersCursor.getInt(playersCursor.getColumnIndexOrThrow("id"))
        val player1Name = playersCursor.getString(playersCursor.getColumnIndex("name"))
        val player1Punkte = playersCursor.getInt(playersCursor.getColumnIndex("punkte"))
        val player1Phasen = playersCursor.getString(playersCursor.getColumnIndex("phasen"))
        val player1GameWon = playersCursor.getInt(playersCursor.getColumnIndex("gameWon"))
        assertEquals(player1Id, 1)
        assertEquals(player1Name, "Player 1")
        assertEquals(player1Punkte, 256)
        assertEquals(player1Phasen, "1, 2, 3, 4, 5, 6, 7, 8, 9, 10")
        assertEquals(player1GameWon, 0)

        playersCursor.moveToNext()
        val player2Id = playersCursor.getInt(playersCursor.getColumnIndexOrThrow("id"))
        val player2Name = playersCursor.getString(playersCursor.getColumnIndex("name"))
        val player2Punkte = playersCursor.getInt(playersCursor.getColumnIndex("punkte"))
        val player2Phasen = playersCursor.getString(playersCursor.getColumnIndex("phasen"))
        val player2GameWon = playersCursor.getInt(playersCursor.getColumnIndex("gameWon"))
        assertEquals(player2Id, 2)
        assertEquals(player2Name, "Player 2")
        assertEquals(player2Punkte, 512)
        assertEquals(player2Phasen, "1, 2, 3, 8, 9, 10")
        assertEquals(player2GameWon, 0)

        playersCursor.moveToNext()
        val player3Id = playersCursor.getInt(playersCursor.getColumnIndexOrThrow("id"))
        val player3Name = playersCursor.getString(playersCursor.getColumnIndex("name"))
        val player3Punkte = playersCursor.getInt(playersCursor.getColumnIndex("punkte"))
        val player3Phasen = playersCursor.getString(playersCursor.getColumnIndex("phasen"))
        val player3GameWon = playersCursor.getInt(playersCursor.getColumnIndex("gameWon"))
        assertEquals(player3Id, 3)
        assertEquals(player3Name, "Player 3")
        assertEquals(player3Punkte, 1024)
        assertEquals(player3Phasen, "")
        assertEquals(player3GameWon, 1)

        // Verify High score data
        highscoreCursor.moveToFirst()
        val hs1id = highscoreCursor.getInt(highscoreCursor.getColumnIndexOrThrow("id"))
        val hs1PlayerName =
            highscoreCursor.getString(highscoreCursor.getColumnIndexOrThrow("playerName"))
        val hs1Points = highscoreCursor.getInt(highscoreCursor.getColumnIndexOrThrow("punkte"))
        val hs1Timestamp = highscoreCursor.getInt(highscoreCursor.getColumnIndexOrThrow("date"))
        assertEquals(hs1id, 1)
        assertEquals(hs1PlayerName, "Player 1")
        assertEquals(hs1Points, 128)
        assertEquals(hs1Timestamp, 1704228890)

        highscoreCursor.moveToNext()
        val hs2id = highscoreCursor.getInt(highscoreCursor.getColumnIndexOrThrow("id"))
        val hs2PlayerName =
            highscoreCursor.getString(highscoreCursor.getColumnIndexOrThrow("playerName"))
        val hs2Points = highscoreCursor.getInt(highscoreCursor.getColumnIndexOrThrow("punkte"))
        val hs2Timestamp = highscoreCursor.getInt(highscoreCursor.getColumnIndexOrThrow("date"))
        assertEquals(hs2id, 2)
        assertEquals(hs2PlayerName, "Player 2")
        assertEquals(hs2Points, 64)
        assertEquals(hs2Timestamp, 1704142490)

        // Verify new High Score data
        val highscores = globalHighscores.GlobalHighscoresDao().getHighscoreList()
        assertEquals(highscores[0], GlobalHighscores(1, "Player 1", 128, Date(1704228890)))
        assertEquals(highscores[1], GlobalHighscores(2, "Player 2", 64, Date(1704142490)))
    }
}