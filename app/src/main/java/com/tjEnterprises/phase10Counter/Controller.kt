package com.tjEnterprises.phase10Counter

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.CheckBox
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.tjEnterprises.phase10Counter.adapters.PlayerRecyclerAdapter
import com.tjEnterprises.phase10Counter.data.highscores.Highscores
import com.tjEnterprises.phase10Counter.data.highscores.HighscoresDao
import com.tjEnterprises.phase10Counter.data.player.PlayerDataDao
import com.tjEnterprises.phase10Counter.data.pointHistory.PointHistoryDao
import java.util.*


class Controller {

    private val players: MutableList<Player> = ArrayList()

    private lateinit var playerDataDao: PlayerDataDao
    private lateinit var sharedPref: SharedPreferences
    private lateinit var edit: SharedPreferences.Editor
    lateinit var appContext: Context
    private lateinit var mainActivity: MainActivity
    private lateinit var highscoresDao: HighscoresDao
    private lateinit var pointHistoryDao: PointHistoryDao

    private lateinit var playerRecyclerAdapter: PlayerRecyclerAdapter
    private lateinit var recyclerView: RecyclerView


    fun setContextsAndInit(con: Context, mainActivity: MainActivity, playerDataDao: PlayerDataDao, highscoresDao: HighscoresDao, pointHistoryDao: PointHistoryDao) {
        this.appContext = con
        this.mainActivity = mainActivity
        this.sharedPref =
            con.getSharedPreferences(("controller_sharedPrefs"), Context.MODE_PRIVATE)
        this.edit = sharedPref.edit()
        this.edit.apply()
        this.playerDataDao = playerDataDao
        this.highscoresDao = highscoresDao
        this.pointHistoryDao = pointHistoryDao

        playerRecyclerAdapter = PlayerRecyclerAdapter(players, this)
    }

    fun getPlayersSize(): Int {
        return players.size
    }

    fun saveAllData() {
        edit.putInt("player_count", players.size)

        for (i in 0 until players.size) {
            players[i].savePlayerData()
            edit.putInt(i.toString() + "_playerNR", players[i].getPlayerNR())
            edit.putString(i.toString() + "_playerName", players[i].getPlayerName())
        }
        edit.apply()
    }

    fun loadAllData() {
        for (i in 0 until sharedPref.getInt("player_count", 0)) {
            players.add(
                Player(
                    sharedPref.getInt(i.toString() + "_playerNR", -1),
                    sharedPref.getString(i.toString() + "_playerName", "404").toString(),
                    appContext, playerDataDao, pointHistoryDao)
            )
            players[i].loadPlayerData()
        }
    }

    private fun savePlayerData(playerNR: Int){
        players[playerNR].savePlayerData()
    }

    fun addPunkteToPlayer(playerNR: Int, punkte: Int) {
        players[playerNR].addPunkte(punkte)
        savePlayerData(playerNR)

        // need to run this on the UI thread, else app will crash
        recyclerView.post { playerRecyclerAdapter.notifyItemChanged(playerNR) }

        // then scroll futher down after updating UI
        // removed this line, because it may be irritating for the user
        // recyclerView.smoothScrollToPosition(playerNR + 2)
    }

    private fun setPhase(playerNR: Int, phasenNR: Int, phaseBestanden: Boolean) {
        if (phaseBestanden) {
            players[playerNR].phaseDone(phasenNR)
        } else {
            players[playerNR].phaseUndoDone(phasenNR)
        }
    }

    private fun getPhase(playerNR: Int, phasenNR: Int): Boolean {
        return players[playerNR].getPhase(phasenNR)
    }

    // return as string what contentView is used to init the correct views
    fun setCorrectView(): String {
        return if (players.size > 1) {
            mainActivity.setContentView(R.layout.activity_main)
            "main"
        } else {
            mainActivity.setContentView(R.layout.spieler_auswahl)
            "auswahl"
        }
    }

    fun removeAllData() {
        for (i in players.size - 1 downTo 0) {
            players[i].removePlayerData()
            players.removeAt(i)
        }
        pointHistoryDao.deletePointHistory()
        edit.clear()
        edit.commit()
    }

    fun addPlayer(name: String) {
        players.add(Player(players.size, name, appContext, playerDataDao, pointHistoryDao))
    }

    fun phasenOnClick(v: View) {
        val alertDialog = AlertDialog.Builder(mainActivity).create()
        val inflater = mainActivity.layoutInflater
        val dialogView: ConstraintLayout =
            inflater.inflate(R.layout.dialog_phasen_auswahl, null) as ConstraintLayout
        alertDialog.setView(dialogView)

        // using polymorphism to use getChildAt(0) on either ConstraintLayout or LinearLayout
        var isLandScape = false
        var secondLayout: ViewGroup? = null
        val layout: ViewGroup =
            if (mainActivity.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                val sv = dialogView.getChildAt(0) as ViewGroup

                val sv2 = dialogView.getChildAt(1) as ViewGroup
                secondLayout = sv2.getChildAt(0) as ViewGroup
                isLandScape = true

                sv.getChildAt(0) as ViewGroup

            } else {
                val sv = dialogView.getChildAt(0) as ViewGroup
                sv.getChildAt(0) as ViewGroup
            }

        //set checkboxes
        for (i in 0 until 10) {
            val cb: CheckBox = if (isLandScape && i > 4) {
                secondLayout?.getChildAt(i.rem(5)) as CheckBox
            } else {
                layout.getChildAt(i) as CheckBox
            }

            //get player tag to identify the pressed button
            var currentPlayerNr = 0
            if (v.tag != null) {
                currentPlayerNr = v.tag as Int
            }
            //set checkboxes depending on the completed phases (phases start at 1 -> 0 would be "all phases done")
            cb.isChecked = getPhase(currentPlayerNr, i + 1)

            val string = mainActivity.getString((R.string.dialog_rounds_of))
            alertDialog.setTitle(string + players[currentPlayerNr].getPlayerName())
        }

        // set listeners and dialog button
        alertDialog.setOnDismissListener {
            phasenOnClickOnDismiss(v, layout, secondLayout, isLandScape)
        }
        alertDialog.setOnCancelListener {
            phasenOnClickOnDismiss(v, layout, secondLayout, isLandScape)
        }
        alertDialog.setButton(
            AlertDialog.BUTTON_NEUTRAL,
            mainActivity.getString(R.string.ok)
        ) { dialog, _ ->
            dialog.dismiss()
            phasenOnClickOnDismiss(v, layout, secondLayout, isLandScape)
        }

        alertDialog.show()

        // make width to max and height to wrapContent
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(alertDialog.window!!.attributes)
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        alertDialog.window!!.attributes = layoutParams

    }

    private fun phasenOnClickOnDismiss(
        v: View,
        layout: ViewGroup,
        secondLayout: ViewGroup?,
        isLandScape: Boolean
    ) {
        //save checkboxes
        var playerNR = 0
        for (i in 0 until 10) {
            val cb: CheckBox = if (isLandScape && i > 4) {
                secondLayout?.getChildAt(i.rem(5)) as CheckBox
            } else {
                layout.getChildAt(i) as CheckBox
            }

            if (v.tag != null) {
                playerNR = v.tag as Int
            }

            setPhase(playerNR, i + 1, cb.isChecked)
            //i+1 because the Phases start with 1 but array here with 0.
            //and place 0 in the array of an player object shows if the player has won
        }
        savePlayerData(playerNR)

        // need to run this on the UI thread, else app will crash
        recyclerView.post { playerRecyclerAdapter.notifyItemChanged(playerNR) }
    }

    fun addNewHighscore(){
        for (i in 0 until players.size) {
            if(players[i].getPhase(0)){
                val high = Highscores(0, players[i].getPlayerName(), players[i].getPunktzahl(), Calendar.getInstance().time)
                highscoresDao.insertHighscore(high)
            }
        }
    }

    fun makePlayerRecycler(){
        val llMngr = FlexboxLayoutManager(appContext).apply {
            justifyContent = JustifyContent.SPACE_EVENLY
            flexDirection = FlexDirection.ROW
            flexWrap = FlexWrap.WRAP
        }
        llMngr.isItemPrefetchEnabled = true
        recyclerView = mainActivity.findViewById(R.id.recyclerViewPlayers)
        recyclerView.layoutManager = llMngr
        recyclerView.adapter = playerRecyclerAdapter
    }
}