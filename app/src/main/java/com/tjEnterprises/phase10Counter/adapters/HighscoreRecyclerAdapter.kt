package com.tjEnterprises.phase10Counter.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tjEnterprises.phase10Counter.R
import com.tjEnterprises.phase10Counter.data.highscores.Highscores

class HighscoreRecyclerAdapter(private val highscores: List<Highscores>) :
    RecyclerView.Adapter<HighscoreRecyclerAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNameHighscore = itemView.findViewById<TextView>(R.id.tvNameHighscore)
        var tvPunkteHighscore = itemView.findViewById<TextView>(R.id.tvPunkteHighscore)
        var tvDatumHighscore = itemView.findViewById<TextView>(R.id.tvDatumHighscore)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.highscore_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvNameHighscore.text = highscores[position].playerName
        holder.tvPunkteHighscore.text = highscores[position].punkte.toString()
        val time = highscores[position].date.toString()
        var t = time.drop(8).dropLast(24)        //day
        t = t + " " + time.drop(4).dropLast(27)  //month
        t = t + ". " + time.drop(30)                // year
        holder.tvDatumHighscore.text = t
    }

    override fun getItemCount(): Int {
        return highscores.size
    }

}