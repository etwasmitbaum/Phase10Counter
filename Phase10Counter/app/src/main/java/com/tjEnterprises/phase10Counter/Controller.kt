package com.tjEnterprises.phase10Counter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentContainerView
import android.view.WindowManager
import com.tjEnterprises.phase10Counter.data.player.PlayerDataDao


class Controller {

    private val players: MutableList<Player> = ArrayList()

    private lateinit var playerDataDao: PlayerDataDao
    private lateinit var sharedPref: SharedPreferences
    private lateinit var edit: SharedPreferences.Editor
    private lateinit var con: Context
    private lateinit var mainActivity: MainActivity

    @SuppressLint("CommitPrefEdits")
    fun setContexts(con: Context, mainActivity: MainActivity, playerDataDao: PlayerDataDao) {
        this.con = con
        this.mainActivity = mainActivity
        this.sharedPref =
            con.getSharedPreferences(("controller_sharedPrefs"), Context.MODE_PRIVATE)
        this.edit = sharedPref.edit()
        this.edit.apply()
        this.playerDataDao = playerDataDao
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
                    con, playerDataDao
                )
            )
            players[i].loadPlayerData()
        }
    }

    fun addPunkteToPlayer(playerNR: Int, punkte: Int) {
        players[playerNR].addPunkte(punkte)
        updateAllPlayerFragments()
        saveAllData()
    }

    private fun setPhase(playerNr: Int, phasenNR: Int, phaseBestanden: Boolean) {
        if (phaseBestanden) {
            players[playerNr].phaseDone(phasenNR)
        } else {
            players[playerNr].phaseUndoDone(phasenNR)
        }
    }

    private fun getPhase(playerNr: Int, phasenNR: Int): Boolean {
        return players[playerNr].getPhase(phasenNR)
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
        edit.clear()
        edit.commit()
    }

    fun addPlayer(name: String) {
        players.add(Player(players.size, name, con, playerDataDao))
    }

    fun placePlayerFragments() {
        val llFragments: LinearLayout = mainActivity.findViewById(R.id.llFragmentHolder)
        llFragments.removeAllViews()

        var landscape = false
        var secondLLFragmentsForLandscape: LinearLayout? = null

        if (mainActivity.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            secondLLFragmentsForLandscape = mainActivity.findViewById(R.id.secondLLFragmentHolderForLandscape)
            secondLLFragmentsForLandscape.removeAllViews()
            landscape = true
        }

        var firstLoopPart = 0

        // split the player fragments in to parts for the two LinearLayouts
        if (landscape){
            var firstHalf: Double = players.size / 2.toDouble()
            if (((firstHalf.rem(2).toInt()) != 0) || (firstHalf == 0.5)) {
                firstHalf += 0.5
            }
            firstLoopPart = firstHalf.toInt()
        } else {
            firstLoopPart = players.size
        }

        for (i in 0 until players.size) {
            val container = FragmentContainerView(mainActivity)
            container.id =
                1000 + players[i].getPlayerNR()  // id+1000 cuz else id would be 0 once, causes error
            container.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            //using the second LinearLayout if the first one should be filled with half of the players
            //else using only the first one (in portrait mode)
            if (i >= firstLoopPart && secondLLFragmentsForLandscape != null){
                secondLLFragmentsForLandscape.addView(container)
            } else {
                llFragments.addView(container)
            }


            players[i].setFragment(
                PlayerFragment.newInstance(), this
            )
            val fragment = players[i].getFragment()
            mainActivity.supportFragmentManager.beginTransaction().apply {
                replace(container.id, fragment)
                commit()
            }
        }
    }

    private fun updateAllPlayerFragments() {
        for (i in 0 until players.size) {
            if (players[i].getPhasenAsString() == con.getString(R.string.none)) {
                players[i].phaseDone(0)
                players[i].getFragment().updateViews(
                    players[i].getPlayerName(),
                    players[i].getPunktzahl(),
                    con.getString(R.string.none)
                )
            } else {
                players[i].getFragment().updateViews(
                    players[i].getPlayerName(),
                    players[i].getPunktzahl(),
                    players[i].getPhasenAsString()
                )
                players[i].phaseUndoDone(0)
            }
        }
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
        for (i in 0 until 10) {
            val cb: CheckBox = if (isLandScape && i > 4) {
                secondLayout?.getChildAt(i.rem(5)) as CheckBox
            } else {
                layout.getChildAt(i) as CheckBox
            }
            var playerNr = 0
            if (v.tag != null) {
                playerNr = v.tag as Int
            }

            setPhase(playerNr, i + 1, cb.isChecked)
            //i+1 because the Phases start with 1 but array here with 0.
            //and place 0 in the array of an player object shows if the player has won
        }
        saveAllData()
        updateAllPlayerFragments()
    }

}