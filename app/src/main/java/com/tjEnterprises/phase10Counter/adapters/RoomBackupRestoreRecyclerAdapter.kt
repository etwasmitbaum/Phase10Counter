package com.tjEnterprises.phase10Counter.adapters

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tjEnterprises.phase10Counter.R
import com.tjEnterprises.phase10Counter.data.roomBackup.RoomBackup

class RoomBackupRestoreRecyclerAdapter(private val fileNames: MutableList<String>, private val roomBackup: RoomBackup, private val context: Context): RecyclerView.Adapter<RoomBackupRestoreRecyclerAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvFileName = itemView.findViewById<TextView>(R.id.tvFileName)
        val btnDeleteSaveGame = itemView.findViewById<ImageButton>(R.id.btnDeleteSaveGame)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.roombackup_restore_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvFileName.text = fileNames[position]

        // restore selected file
        holder.tvFileName.setOnClickListener {
            roomBackup.restoreSelectedInternalExternalFile(fileNames[position])
        }

        // show dialog to confirm deletion
        holder.btnDeleteSaveGame.setOnClickListener {
            val alert = AlertDialog.Builder(context)
            alert.setTitle(context.getString(R.string.are_u_sure))
            alert.setMessage(fileNames[position] + " " + context.getString(R.string.will_be_deleted))
            alert.setPositiveButton(context.getString(R.string.yes)){ dialog, _ ->
                roomBackup.deleteSingleBackup(fileNames[position])
                fileNames.removeAt(position)
                this.notifyItemChanged(position)
                dialog.dismiss()
            }
            alert.setNegativeButton(context.getString(R.string.no)){ dialog, _ ->
                dialog.dismiss()
            }
            alert.show()
        }
    }

    override fun getItemCount(): Int {
        return fileNames.size
    }
}