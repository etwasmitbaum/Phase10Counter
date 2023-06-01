package com.tjEnterprises.phase10Counter.data.roomBackup

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.tjEnterprises.phase10Counter.R
import com.tjEnterprises.phase10Counter.adapters.RoomBackupRestoreRecyclerAdapter

class DialogFileRestore(context: Context, private var fileNameList: MutableList<String>, private val roomBackup: RoomBackup) : AlertDialog(context) {
    private lateinit var adapter: RoomBackupRestoreRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState ?: Bundle())

        // Use the LayoutInflater to inflate the
        // dialog_list layout file into a View object
        val view = LayoutInflater.from(context).inflate(R.layout.roomback_restore_view, null)

        // Set the dialog's content view
        // to the newly created View object
        setContentView(view)

        // Allow the dialog to be dismissed
        // by touching outside of it
        setCanceledOnTouchOutside(true)

        // Allow the dialog to be canceled
        // by pressing the back button
        setCancelable(true)

        // Set up the RecyclerView in the dialog
        setUpRecyclerView(view)
    }

    private fun setUpRecyclerView(view: View) {
        // Find the RecyclerView in the layout file and set
        // its layout manager to a LinearLayoutManager
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewBackups)
        val llMngr = FlexboxLayoutManager(context).apply {
            justifyContent = JustifyContent.SPACE_EVENLY
            flexDirection = FlexDirection.ROW
            flexWrap = FlexWrap.WRAP
        }
        llMngr.isItemPrefetchEnabled = true

        recyclerView.layoutManager = llMngr

        // Create a new instance of the EmployeeAdapter
        // and set it as the RecyclerView's adapter
        adapter = RoomBackupRestoreRecyclerAdapter(fileNameList, roomBackup, context)
        recyclerView.adapter = adapter
    }
}