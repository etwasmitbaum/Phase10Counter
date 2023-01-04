package com.tjEnterprises.phase10Counter.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.tjEnterprises.phase10Counter.Controller
import com.tjEnterprises.phase10Counter.Player
import com.tjEnterprises.phase10Counter.R

class PlayerRecyclerAdapter(private val player: MutableList<Player> = ArrayList(), private val controller: Controller) : RecyclerView.Adapter<PlayerRecyclerAdapter.ViewHolder>() {
    class ViewHolder(itemView: View, private val con: Controller) : RecyclerView.ViewHolder(itemView), OnClickListener {
        val tvName = itemView.findViewById<TextView>(R.id.tvName)
        val tvPunkte = itemView.findViewById<Spinner>(R.id.spinPunkte)
        val tvPhasen = itemView.findViewById<TextView>(R.id.tvPhasen)

        var btnPhasen: Button
        var etPunkte: EditText
        lateinit var pl: Player

        init {
            btnPhasen = itemView.findViewById(R.id.btnPhasen)
            etPunkte = itemView.findViewById(R.id.etPunkte)

            btnPhasen.setOnClickListener { v ->
                con.phasenOnClick(v)
            }


            // we need to listeners on the editText, to also save on last editText in list.
            // else the "done" action would not change the focus and data would not be saved
            // also if we net the action for the last editText to "next" it only goes to the top most visible editText
            etPunkte.setOnEditorActionListener  { _, actionID, _ ->
                if(actionID == EditorInfo.IME_ACTION_DONE){
                    editorAction()
                    true
                } else {
                    false
                }
            }
            etPunkte.setOnFocusChangeListener {_, _ ->
                editorAction()
            }
        }

        fun editorAction(){
            try {
                if (etPunkte.text.toString() != "") {
                    con.addPunkteToPlayer(pl.getPlayerNR(), etPunkte.text.toString().toInt())
                    etPunkte.setText("")
                }
            } catch (e: NumberFormatException) {
                etPunkte.setText("")
            }
        }

        override fun onClick(p0: View?) {}

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_player, parent, false)
        return ViewHolder(view, controller)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvName.text = player[position].getPlayerName()
        //holder.tvPunkte.text = player[position].getPunktzahl().toString()
        holder.tvPhasen.text = player[position].getPhasenAsString()
        holder.btnPhasen.tag = player[position].getPlayerNR()
        holder.pl = player[position]

        val adpater = ArrayAdapter<Int>(controller.appContext, R.layout.point_history_spinner, player[position].getPunkteList())
        adpater.setDropDownViewResource(R.layout.point_history_spinner)
        holder.tvPunkte.adapter = adpater
        holder.tvPunkte.setSelection(0)

        if(position == player.size - 1){
            holder.etPunkte.imeOptions = EditorInfo.IME_ACTION_DONE
        } else {
            holder.etPunkte.imeOptions = EditorInfo.IME_ACTION_NEXT
        }

    }

    override fun getItemCount(): Int {
        return player.size
    }
}