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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.tjEnterprises.phase10Counter.adapters.AddPlayerAdapter
import com.tjEnterprises.phase10Counter.adapters.PlayerRecyclerAdapter
import com.tjEnterprises.phase10Counter.data.globalHighscores.GlobalHighscores
import com.tjEnterprises.phase10Counter.data.globalHighscores.GlobalHighscoresDao
import com.tjEnterprises.phase10Counter.data.player.PlayerDataDao
import com.tjEnterprises.phase10Counter.data.pointHistory.PointHistoryDao
import java.util.Calendar


class Controller {

    private val players: MutableList<Player> = ArrayList()

    private lateinit var playerDataDao: PlayerDataDao
    lateinit var appContext: Context
    private lateinit var mainActivity: MainActivity
    private lateinit var globalHighscoresDao: GlobalHighscoresDao
    private lateinit var pointHistoryDao: PointHistoryDao

    private lateinit var playerRecyclerAdapter: PlayerRecyclerAdapter
    private lateinit var playersRecyclerView: RecyclerView
    private lateinit var addPlayerRecyclerAdapter: AddPlayerAdapter
    private lateinit var addPlayersRecyclerView: RecyclerView

    private lateinit var sharedPref: SharedPreferences

    companion object{
        val GLOBAL_FLAGS_SHARED_PREF_KEY = "GlobalFlags"
        val GLOBAL_FLAGS_SHARED_PREF_RESOTORE_OCCURRED_KEY = "restoreOccurred"
        val GLOBAL_FLAGS_SHARED_PREF_PREVIOUS_PLAYER_COUNT = "prevPlayerCount"
        val GLOBAL_FLAGS_SHARED_PREF_PREVIOUS_PLAYER_NAME_I = "prevPlayerName_"
    }

    fun setContextsAndInit(
        con: Context,
        mainActivity: MainActivity,
        playerDataDao: PlayerDataDao,
        highscoresDao: GlobalHighscoresDao,
        pointHistoryDao: PointHistoryDao,
    ) {
        this.appContext = con
        this.mainActivity = mainActivity
        this.playerDataDao = playerDataDao
        this.globalHighscoresDao = highscoresDao
        this.pointHistoryDao = pointHistoryDao

        this.sharedPref = con.getSharedPreferences(GLOBAL_FLAGS_SHARED_PREF_KEY, Context.MODE_PRIVATE)

        playerRecyclerAdapter = PlayerRecyclerAdapter(players, this)
        addPlayerRecyclerAdapter = AddPlayerAdapter(players, this)
    }

    fun getPlayersSize(): Int {
        return players.size
    }

    fun saveAllData() {
        for (i in 0 until players.size) {
            players[i].savePlayerData()
        }
    }

    fun loadAllData() {
        for (i in 0 until playerDataDao.getPlayerCount()) {
            players.add(
                Player(
                    playerDataDao.getSinglePlayer(i).id,
                    playerDataDao.getSinglePlayer(i).name,
                    appContext, playerDataDao, pointHistoryDao
                )
            )
            players[i].loadPlayerData()
        }
    }

    private fun savePlayerData(playerNR: Int) {
        players[playerNR].savePlayerData()
    }

    fun addPunkteToPlayer(playerNR: Int, punkte: Int) {
        players[playerNR].addPunkte(punkte)
        savePlayerData(playerNR)

        // need to run this on the UI thread, else app will crash
        playersRecyclerView.post { playerRecyclerAdapter.notifyItemChanged(playerNR) }
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

    fun removePlayer(playerId: Int) {
        players[playerId].removePlayerData()
        players.removeAt(playerId)

        // change all IDs after the one removed, so no id duplicates will be created
        for (i in playerId until players.size) {
            players[i].changePlayerNR(players[i].getPlayerNR() - 1)
        }
        // all player data was changed, so this is the fastest way
        addPlayerRecyclerAdapter.notifyDataSetChanged()
    }

    fun removeAllData() {
        for (i in players.size - 1 downTo 0) {
            players[i].removePlayerData()
            players.removeAt(i)
        }
        pointHistoryDao.deletePointHistory()
    }

    fun addPlayer(name: String) {
        players.add(Player(players.size, name, appContext, playerDataDao, pointHistoryDao))
        addPlayerRecyclerAdapter.notifyItemInserted(players.size - 1)
        addPlayersRecyclerView.smoothScrollToPosition(players.size)
    }

    fun phasenOnClick(v: View) {
        val alertDialog = AlertDialog.Builder(mainActivity, R.style.AlertDialog_AppCompat_phase10Counter).create()
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
        playersRecyclerView.post { playerRecyclerAdapter.notifyItemChanged(playerNR) }
    }

    fun addNewHighscore() {
        for (i in 0 until players.size) {
            if (players[i].getPhase(0)) {
                val high = GlobalHighscores(
                    0,
                    players[i].getPlayerName(),
                    players[i].getPunktzahl(),
                    Calendar.getInstance().time
                )
                globalHighscoresDao.insertHighscore(high)
            }
        }
    }

    fun makePlayerRecycler() {
        val llMngr = FlexboxLayoutManager(appContext).apply {
            justifyContent = JustifyContent.SPACE_EVENLY
            flexDirection = FlexDirection.ROW
            flexWrap = FlexWrap.WRAP
        }
        llMngr.isItemPrefetchEnabled = true
        playersRecyclerView = mainActivity.findViewById(R.id.recyclerViewPlayers)
        playersRecyclerView.layoutManager = llMngr
        playersRecyclerView.adapter = playerRecyclerAdapter
    }

    fun makeAddPlayerRecycler() {
        val llMngr = FlexboxLayoutManager(appContext).apply {
            justifyContent = JustifyContent.SPACE_EVENLY
            flexDirection = FlexDirection.ROW
            flexWrap = FlexWrap.WRAP
        }
        llMngr.isItemPrefetchEnabled = true
        addPlayersRecyclerView = mainActivity.findViewById(R.id.recyclerViewAddPlayer)
        addPlayersRecyclerView.layoutManager = llMngr
        addPlayersRecyclerView.adapter = addPlayerRecyclerAdapter

        ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val removedPlayer: Player = players[viewHolder.adapterPosition]
                removePlayer(removedPlayer.getPlayerNR())
            }
        }).attachToRecyclerView(addPlayersRecyclerView)
    }

    fun changePlayerName(newName: String, playerId: Int) {
        players[playerId].changePlayerName(newName)
    }

    fun storePlayerNames(){
        val playerCount = getPlayersSize()
        sharedPref.edit().putInt(GLOBAL_FLAGS_SHARED_PREF_PREVIOUS_PLAYER_COUNT, playerCount).commit()
        for (i in 0 until playerCount){
            sharedPref.edit().putString(GLOBAL_FLAGS_SHARED_PREF_PREVIOUS_PLAYER_NAME_I + i, players[i].getPlayerName()).commit()
        }
    }
    fun restorePlayerNames(): Boolean{
        val playerCount = sharedPref.getInt(GLOBAL_FLAGS_SHARED_PREF_PREVIOUS_PLAYER_COUNT, 0)
        var didRestore = false
        for (i in 0 until playerCount){
            addPlayer(sharedPref.getString(GLOBAL_FLAGS_SHARED_PREF_PREVIOUS_PLAYER_NAME_I + i, "error player not found")
                .toString())
            didRestore = true
        }
        return didRestore
    }
}