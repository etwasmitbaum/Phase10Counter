package com.tjEnterprises.phase10Counter

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tjEnterprises.phase10Counter.adapters.HighscoreRecyclerAdapter
import com.tjEnterprises.phase10Counter.data.GlobalDataDatabase
import com.tjEnterprises.phase10Counter.data.globalHighscores.GlobalHighscoresDao

class HighscoreActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnBack: Button

    private lateinit var highscoreRecyclerAdapter: HighscoreRecyclerAdapter
    private lateinit var globalHighscoresDao: GlobalHighscoresDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_highscore)

        val globalDB = GlobalDataDatabase.getInstance(this)
        globalHighscoresDao = globalDB.GlobalHighscoresDao()

        initViews()
        initRecyclerView()
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.recyclerView)
        btnBack = findViewById(R.id.btnBackFromHighscore)

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun initRecyclerView() {
        val llMngr = LinearLayoutManager(this)
        recyclerView.layoutManager = llMngr
        highscoreRecyclerAdapter = HighscoreRecyclerAdapter(globalHighscoresDao.getHighscoreList())
        recyclerView.adapter = highscoreRecyclerAdapter
    }
}