package com.tjEnterprises.phase10Counter

import android.content.Context
import android.content.SharedPreferences

class Player(private val playerNR: Int, private val name: String, con: Context) {

    private var punkte: MutableList<Int> = ArrayList()
    private var phasen: BooleanArray = BooleanArray(11) { false }     // index 0 = game won
    // value false = phase not complete
    // value true = phase complete

    // sharedPrefs name is playerNR name and "sharedPrefs" so every player has its own
    private val sharedPref = con.applicationContext.getSharedPreferences(
        (playerNR.toString() + "_" + name + "_sharedPrefs"),
        Context.MODE_PRIVATE
    )
    private val edit: SharedPreferences.Editor = sharedPref.edit()

    private lateinit var fragment: PlayerFragment

    fun getFragment(): PlayerFragment {
        return fragment
    }

    fun setFragment(frag: PlayerFragment, con: Controller) {
        this.fragment = frag
        this.fragment.setController(con, this)
    }

    fun getPhasenAsString(): String {
        var s = ""
        for (i in 1 until phasen.size) {
            if (!phasen[i]) {
                s = "$s$i, "
            }
        }
        return s.dropLast(2)
    }

    fun getPlayerNR(): Int {
        return this.playerNR
    }

    fun getPlayerName(): String {
        return this.name
    }

    fun addPunkte(punkte: Int) {
        this.punkte.add(punkte)
    }

    /* comment out unused code
    fun replacePunkte(idx: Int, punkte: Int) {
        this.punkte[idx] = punkte
    }

    fun removePunkte(idx: Int) {
        this.punkte.removeAt(idx)
    }
    */

    fun getGesamtPunktzahl(): Int {
        var gesamtPunkte = 0
        for (i in 0 until punkte.size) {
            gesamtPunkte += punkte[i]
        }
        return gesamtPunkte
    }

    fun phaseAbgeschlossen(phasenNR: Int) {
        this.phasen[phasenNR] = true
    }

    fun phaseDochNichtAbgeschlossen(phasenNR: Int) {
        this.phasen[phasenNR] = false
    }

    fun getPhase(phasenNR: Int): Boolean {
        return phasen[phasenNR]
    }

    fun savePlayerData() {

        edit.putInt("punkte_size", punkte.size)

        for (i in 0 until punkte.size) {
            edit.putInt(i.toString() + "_punkte", punkte[i])
        }

        for (i in phasen.indices) {
            edit.putBoolean(i.toString() + "_phase", phasen[i])
        }

        edit.commit()
    }

    fun loadPlayerData() {
        for (i in 0 until sharedPref.getInt("punkte_size", 0)) {
            punkte.add(sharedPref.getInt(i.toString() + "_punkte", 0))
        }

        for (i in 0 until 11) {
            phasen[i] = sharedPref.getBoolean(i.toString() + "_phase", false)
        }
    }

    fun removePlayerData() {
        edit.clear()
        edit.commit()
    }

}