package com.tjEnterprises.phase10Counter

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.tjEnterprises.phase10Counter.data.AppDatabase
import com.tjEnterprises.phase10Counter.data.GlobalDataDatabase
import com.tjEnterprises.phase10Counter.data.globalHighscores.GlobalHighscores
import com.tjEnterprises.phase10Counter.data.globalHighscores.GlobalHighscoresDao
import com.tjEnterprises.phase10Counter.data.highscores.Highscores
import com.tjEnterprises.phase10Counter.data.highscores.HighscoresDao
import com.tjEnterprises.phase10Counter.data.player.PlayerDataDao
import com.tjEnterprises.phase10Counter.data.pointHistory.PointHistoryDao
import java.util.Date

class MainActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var globalDB: GlobalDataDatabase
    private lateinit var playerDataDao: PlayerDataDao
    private lateinit var highscoresDao: HighscoresDao
    private lateinit var globalHighscoresDao: GlobalHighscoresDao
    private lateinit var pointHistoryDao: PointHistoryDao
    private val controller: Controller = Controller()

    private lateinit var etPlayerName: EditText
    private lateinit var tvMessage: TextView
    private lateinit var btnWeiter: Button
    private lateinit var btnAddPlayer: Button
    private lateinit var btnEndMatch: Button
    private lateinit var btnShowPhasenInfo: Button
    private lateinit var btnHighscores: Button
    private lateinit var tvUpdate: TextView
    private lateinit var btnSettings: ImageButton

    private lateinit var currentLayout: String

    private lateinit var sharedPref: SharedPreferences

    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPref = this.getSharedPreferences(Controller.GLOBAL_FLAGS_SHARED_PREF_KEY, Context.MODE_PRIVATE)

        makeDataBases()

        controller.setContextsAndInit(
            applicationContext,
            this,
            playerDataDao,
            globalHighscoresDao,
            pointHistoryDao,
        )
        controller.loadAllData()
        currentLayout = controller.setCorrectView()
        initViews()

        // only sync if a file was restored
        if(sharedPref.getBoolean(Controller.GLOBAL_FLAGS_SHARED_PREF_RESOTORE_OCCURRED_KEY, false)){
            syncHighscoreDB()
            sharedPref.edit().putBoolean(Controller.GLOBAL_FLAGS_SHARED_PREF_RESOTORE_OCCURRED_KEY, false).apply()
        }

        //addDummyData()

        // Only Check for updates, if github release is installed
        if (BuildConfig.BUILD_TYPE != "release") {
            UpdateChecker(applicationContext, this).checkForUpdate(tvUpdate)
        }
    }

    private fun addDummyData(){
        for (i in 0 until (10)){
            val high = Highscores(
                0,
                i.toString() + "player",
                i*i,
                Date(i.toLong())
            )
            highscoresDao.insertHighscore(high)
        }

    }
    private fun makeDataBases() {
        db = AppDatabase.getInstance(this)
        playerDataDao = db.PlayerDataDao()
        highscoresDao = db.HighscoresDao()
        pointHistoryDao = db.PointHistoryDao()

        globalDB = GlobalDataDatabase.getInstance(this)
        globalHighscoresDao = globalDB.GlobalHighscoresDao()
    }

    /**
     * This will copy all new Highscores from the deprecated Database table to the new one,
     * to ensure the after restoring a backup, the Highscores are also transferred to the new GlobalHighscores
     *
     */
    private fun syncHighscoreDB() {
        val oldHighscores = highscoresDao.getHighscoreList()
        val newHighscores = globalHighscoresDao.getHighscoreList()
        var copy: Boolean

        // compare all old to all new Highscores, and copy only Highscores from old to new,
        // if they do not exist in the new one
        for (i in oldHighscores.indices) {
            copy = true
            for (j in newHighscores.indices) {
                if (oldHighscores[i].date == newHighscores[j].date
                    && oldHighscores[i].playerName == newHighscores[j].playerName
                    && oldHighscores[i].punkte == newHighscores[j].punkte
                ) {
                    copy = false
                    break
                }
            }
            if (copy) {
                val high = GlobalHighscores(
                    0,
                    oldHighscores[i].playerName,
                    oldHighscores[i].punkte,
                    oldHighscores[i].date
                )
                globalHighscoresDao.insertHighscore(high)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun initViews() {
        tvUpdate = findViewById(R.id.tvUpdate)
        tvUpdate.text = ""
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

            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                btnSettings = findViewById(R.id.imgBtnSettings)
                btnSettings.setOnClickListener {
                    startActivity(Intent(this, SettingsActivity::class.java))
                }
            }


        } else if (currentLayout == "auswahl") {
            etPlayerName = findViewById(R.id.etPlayerName)
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

            btnHighscores.setOnClickListener {
                startActivity(Intent(this, HighscoreActivity::class.java))
            }

            etPlayerName.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                    btnOnClickAddPlayer(v)
                    return@OnKeyListener true
                }
                false
            })
            controller.makeAddPlayerRecycler()
        }
        setSupportActionBar(findViewById(R.id.toolbar_menu))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.open_settings -> startActivity(Intent(this, SettingsActivity::class.java))
        }

        return super.onOptionsItemSelected(item)
    }

    private fun showPhasenInfo() {
        val d = Dialog(this@MainActivity, R.style.AlertDialog_AppCompat_phase10Counter)
        d.setContentView(R.layout.dialog_phasen_info)
        d.show()
    }

    private fun btnOnClickAddPlayer(v: View) {
        //make sure text was entered
        if (etPlayerName.text.toString().isNotEmpty() && etPlayerName.text.toString()
                .isNotBlank()
        ) {
            //saving the player
            controller.addPlayer(etPlayerName.text.toString())

            //resetting all texts
            etPlayerName.text = null
            tvMessage.text = null

            //activating the next button
            btnWeiter.visibility = View.VISIBLE

            //for (i in 2 until 100){
            //    controller.addPlayer(i.toString())
            //}

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
        if (controller.getPlayersSize() > 1) {
            controller.saveAllData()
            currentLayout = controller.setCorrectView()
            initViews()
        } else {
            findViewById<TextView>(R.id.tvMessage).text =
                getString(R.string.at_leat_2_players)
        }

    }

    private fun btnEndMatch() {

        val alertDialog = AlertDialog.Builder(this, R.style.AlertDialog_AppCompat_phase10Counter).create()
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