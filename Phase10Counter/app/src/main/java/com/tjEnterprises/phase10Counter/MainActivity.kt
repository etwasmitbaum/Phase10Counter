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
import java.io.File


class MainActivity : AppCompatActivity() {

    private val controller: Controller = Controller()

    private lateinit var etPlayerName: EditText
    private lateinit var tvPlayers: TextView
    private lateinit var tvMessage: TextView
    private lateinit var btnWeiter: Button
    private lateinit var btnAddPlayer: Button
    private lateinit var btnReset: Button
    private lateinit var btnShowPhasenInfo: Button
    private lateinit var currentLayout: String

    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        controller.setContexts(applicationContext, this)
        controller.loadAllData()
        currentLayout = controller.setCorrectView()
        initViews()

    }

    private fun initViews() {
        if (currentLayout == "main") {
            controller.placePlayerFragments()
            btnReset = findViewById(R.id.btnReset)
            btnShowPhasenInfo = findViewById(R.id.btnPhasenInfo)

            btnReset.setOnClickListener {
                btnReset()
            }

            btnShowPhasenInfo.setOnClickListener {
                showPhasenInfo()
            }

        } else {
            etPlayerName = findViewById(R.id.etPlayerName)
            tvPlayers = findViewById(R.id.tvAllPlayers)
            btnWeiter = findViewById(R.id.btnSpielerAuswahlWeiter)
            btnAddPlayer = findViewById(R.id.btnAddPlayer)
            tvMessage = findViewById(R.id.tvMessage)


            btnWeiter.visibility = View.INVISIBLE

            btnWeiter.setOnClickListener {
                btnWeiter()
            }

            btnAddPlayer.setOnClickListener { v ->
                btnOnClickAddPlayer(v)
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

    private fun btnReset() {

        val alertDialog = AlertDialog.Builder(this@MainActivity).create()
        alertDialog.setTitle(getString(R.string.all_data_will_be_deleted))
        alertDialog.setMessage(getString(R.string.are_you_sure))

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.yes)) { dialog, _ ->
            dialog.dismiss()
            controller.removeAllData()

            // manually deleting all shredprefs files if there are too many to not fully trash the device
            val shPreFile = File("data/data/com.tjEnterprises.phase10Counter/shared_prefs/")
            if (shPreFile.isDirectory) {
                val children: Array<String> = shPreFile.list() as Array<String>
                val filecount = children.size
                if (filecount >= 24) {
                    for (i in 0 until filecount) {
                        File(shPreFile, children[i]).delete()
                    }
                }
            }
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