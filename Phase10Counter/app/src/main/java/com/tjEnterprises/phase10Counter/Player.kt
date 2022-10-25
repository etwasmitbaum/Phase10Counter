package com.tjEnterprises.phase10Counter

import android.content.Context
import android.content.SharedPreferences
import com.tjEnterprises.phase10Counter.data.player.PlayerData
import com.tjEnterprises.phase10Counter.data.player.PlayerDataDao

class Player(private val playerNR: Int, private val name: String, private val con: Context, private val playerDataDao: PlayerDataDao) {

    private var pData: PlayerData = PlayerData(playerNR, name, 0, "")
    private var punkte: Int = 0

    private var phasen: BooleanArray = BooleanArray(11) { false }
    // index 0 = game won
    // value false = phase not complete
    // value true = phase complete

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

        // loop from 1 to 10, to write all phasen numbers if they are not done
        for (i in 1 until phasen.size) {
            if (!phasen[i]) {
                s = "$s$i, "
            }
        }

        // set "none" string, if all phases are complete. Else on first startup fragment will only show "" as phasen
        s = if(s == ""){
            this.con.getString(R.string.none)
        } else {
            s.dropLast(2)
        }

        return s
    }

    fun getPlayerNR(): Int {
        return this.playerNR
    }

    fun getPlayerName(): String {
        return this.name
    }

    fun addPunkte(punkte: Int) {
        this.punkte = this.punkte + punkte
    }

    fun getPunktzahl(): Int {
        return this.punkte
    }

    fun phaseDone(phasenNR: Int) {
        this.phasen[phasenNR] = true
    }

    fun phaseUndoDone(phasenNR: Int) {
        this.phasen[phasenNR] = false
    }

    fun getPhase(phasenNR: Int): Boolean {
        return phasen[phasenNR]
    }

    fun savePlayerData() {
        pData.punkte = getPunktzahl()
        pData.phasen = getPhasenAsString()

        playerDataDao.insertPlayerData(pData)
    }

    fun loadPlayerData() {
        // load from database
        pData = playerDataDao.getSinglePlayer(playerNR)
        this.punkte = pData.punkte

        // get phasen and convert string to bool array of phasen
        val phasen = pData.phasen.filter { it.isDigit() }
        val hasPhase10 = phasen.contains("10")

        for(i in this.phasen.indices){
            phaseDone(i)
        }
        phaseUndoDone(0)

        // only apply minus 2 in the below loop, if phase 10 is still in the list
        // this is done to not unCheck phase "1" and "0", since "10" contains those digits
        var minus2forPhase10 = 0
        if(hasPhase10){
            phaseUndoDone(10)
            minus2forPhase10 = -2
        }
        for(i: Int in 0 until (phasen.length + minus2forPhase10)){
            phaseUndoDone(phasen[i].digitToInt())
        }
    }

    fun removePlayerData() {
        playerDataDao.deletePlayer(pData)
    }

}