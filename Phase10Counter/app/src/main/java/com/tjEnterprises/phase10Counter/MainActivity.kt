package com.tjEnterprises.phase10Counter

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.tjEnterprises.phase10Counter.data.AppDatabase
import com.tjEnterprises.phase10Counter.data.highscores.HighscoresDao
import com.tjEnterprises.phase10Counter.data.player.PlayerDataDao


class MainActivity : AppCompatActivity() {

    private lateinit var playerDataDao: PlayerDataDao
    private lateinit var highscoresDao: HighscoresDao
    private val controller: Controller = Controller()

    private lateinit var etPlayerName: EditText
    private lateinit var tvPlayers: TextView
    private lateinit var tvMessage: TextView
    private lateinit var btnWeiter: Button
    private lateinit var btnAddPlayer: Button
    private lateinit var btnEndMatch: Button
    private lateinit var btnShowPhasenInfo: Button
    private lateinit var btnHighscores: Button

    private lateinit var currentLayout: String

    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "Database"
        ).fallbackToDestructiveMigration().allowMainThreadQueries().build()
        playerDataDao = db.PlayerDataDao()
        highscoresDao = db.HighscoresDao()
        controller.setContextsAndInit(applicationContext, this, playerDataDao, highscoresDao)
        controller.loadAllData()
        currentLayout = controller.setCorrectView()
        initViews()

        //UpdateChecker(applicationContext).checkForUpdate(findViewById(R.id.tvUpdate))

    }

    private fun initViews() {
        if (currentLayout == "main") {
            controller.makePlayerRecycler()

            btnEndMatch = findViewById(R.id.btnEndMatch)
            btnEndMatch.setOnClickListener {
                btnEndMatch()
            }

            btnShowPhasenInfo = findViewById(R.id.btnPhasenInfo)
            btnShowPhasenInfo.setOnClickListener {
                showPhasenInfo()
            }

        } else if (currentLayout == "auswahl"){
            etPlayerName = findViewById(R.id.etPlayerName)
            tvPlayers = findViewById(R.id.tvAllPlayers)
            btnWeiter = findViewById(R.id.btnSpielerAuswahlWeiter)
            btnAddPlayer = findViewById(R.id.btnAddPlayer)
            tvMessage = findViewById(R.id.tvMessage)
            btnHighscores = findViewById(R.id.btnToHighscore)


            btnWeiter.visibility = View.INVISIBLE

            btnWeiter.setOnClickListener {
                btnWeiter()
            }

            btnAddPlayer.setOnClickListener { v ->
                btnOnClickAddPlayer(v)
            }

            btnHighscores.setOnClickListener{
                startActivity(Intent(this, HighscoreActivity::class.java))
            }

            etPlayerName.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                    btnOnClickAddPlayer(v)
                    return@OnKeyListener true
                }
                false
            })
        }
    }

    private fun showPhasenInfo() {
        val d = Dialog(this@MainActivity)
        d.setContentView(R.layout.dialog_phasen_info)
        d.show()
    }

    private fun btnOnClickAddPlayer(v: View) {
        //make sure text was entered
        if (etPlayerName.text.toString().isNotEmpty() && etPlayerName.text.toString().isNotBlank()) {
            //saving the player
            controller.addPlayer(etPlayerName.text.toString())
            val textToBeSet = (etPlayerName.text.toString() + "\n") + tvPlayers.text.toString()
            tvPlayers.text = textToBeSet

            //resetting all texts
            etPlayerName.text = null
            tvMessage.text = null

            //activating the next button
            btnWeiter.visibility = View.VISIBLE

            for (i in 2 until 100){
                controller.addPlayer(i.toString())
            }

        } else {
            if (controller.getPlayersSize() > 1) {
                //closing keyboard to signaling the user to continue
                val imm: InputMethodManager =
                    applicationContext.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)

            }
            tvMessage.text = getString(R.string.player_needs_a_name)
        }
    }

    private fun btnWeiter() {
        if (controller.getPlayersSize() > 1){
            controller.saveAllData()
            currentLayout = controller.setCorrectView()
            initViews()
        } else {
            findViewById<TextView>(R.id.tvMessage).text =
                getString(R.string.at_leat_2_players)
        }

    }

    private fun btnEndMatch() {

        val alertDialog = AlertDialog.Builder(this@MainActivity).create()
        alertDialog.setTitle(getString(R.string.all_data_will_be_deleted))
        alertDialog.setMessage(getString(R.string.are_you_sure_data_loss))

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.yes)) { dialog, _ ->
            dialog.dismiss()
            controller.addNewHighscore()
            controller.removeAllData()

            // restarting the app
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            applicationContext.startActivity(intent)
            if (applicationContext is Activity) {
                (applicationContext as Activity).finish()
            }
            Runtime.getRuntime().exit(0)
        }

        alertDialog.setButton(
            AlertDialog.BUTTON_NEGATIVE,
            getString(R.string.no)
        ) { dialog, _ -> dialog.cancel() }

        alertDialog.show()
    }
}