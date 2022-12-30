package com.tjEnterprises.phase10Counter

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.tjEnterprises.phase10Counter.adapters.HighscoreRecyclerAdapter
import com.tjEnterprises.phase10Counter.data.AppDatabase
import com.tjEnterprises.phase10Counter.data.highscores.HighscoresDao

class HighscoreActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnBack: Button

    private lateinit var highscoreRecyclerAdapter: HighscoreRecyclerAdapter
    private lateinit var highscoresDao: HighscoresDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_highscore)

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "Database"
        ).fallbackToDestructiveMigration().allowMainThreadQueries().build()
        highscoresDao = db.HighscoresDao()

        initViews()
        initRecyclerView()
    }

    private fun initViews(){
        recyclerView = findViewById(R.id.recyclerView)
        btnBack = findViewById(R.id.btnBackFromHighscore)

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun initRecyclerView(){
        val llMngr = LinearLayoutManager(this)
        recyclerView.layoutManager = llMngr
        highscoreRecyclerAdapter = HighscoreRecyclerAdapter(highscoresDao.getHighscoreList())
        recyclerView.adapter = highscoreRecyclerAdapter
    }
}