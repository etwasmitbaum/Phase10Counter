package com.tjEnterprises.phase10Counter.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.tjEnterprises.phase10Counter.Controller
import com.tjEnterprises.phase10Counter.Player
import com.tjEnterprises.phase10Counter.R

class AddPlayerAdapter(private val players: List<Player>, private val controller: Controller) :
    RecyclerView.Adapter<AddPlayerAdapter.ViewHolder>() {

    class ViewHolder(itemView: View, private val controller: Controller) :
        RecyclerView.ViewHolder(itemView) {

        val etChangePlayerName = itemView.findViewById<EditText>(R.id.etChangePlayerName)
        val btnDeletePlayer = itemView.findViewById<ImageButton>(R.id.btnDeletePlayer)

        lateinit var pl: Player

        init {
            btnDeletePlayer.setOnClickListener {
                controller.removePlayer(pl.getPlayerNR())
            }

            etChangePlayerName.setOnEditorActionListener { _, actionID, _ ->
                if (actionID == EditorInfo.IME_ACTION_DONE) {
                    changeNameAction()
                    true
                } else {
                    false
                }
            }
            etChangePlayerName.setOnFocusChangeListener { _, _ ->
                changeNameAction()
            }
        }

        fun changeNameAction() {
            controller.changePlayerName(etChangePlayerName.text.toString(), pl.getPlayerNR())
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddPlayerAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.add_player_list_element, parent, false)
        return AddPlayerAdapter.ViewHolder(view, controller)
    }

    override fun onBindViewHolder(holder: AddPlayerAdapter.ViewHolder, position: Int) {
        holder.etChangePlayerName.setText(players[position].getPlayerName())
        holder.pl = players[position]
    }

    override fun getItemCount(): Int {
        return players.size
    }

}