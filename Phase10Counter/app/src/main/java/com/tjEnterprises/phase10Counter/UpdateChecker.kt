package com.tjEnterprises.phase10Counter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

class UpdateChecker (val con: Context,  val mainActivity: MainActivity) {

    private val CURRENT_VERSION = 3

    private lateinit var downloadURL: String

    fun checkForUpdate(v: TextView) {
        val queue = Volley.newRequestQueue(this.con)
        val URL = "https://api.github.com/repos/etwasmitbaum/Phase10Counter/releases/latest"
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, URL, null,
            { response ->
                val releaseNumber = response.getString("tag_name").filter { it.isDigit() }
                if(releaseNumber.toInt() > CURRENT_VERSION){
                    downloadURL = response.getString("html_url")
                    v.text = con.getString(R.string.new_version_click_to_download)
                    v.setOnClickListener {
                        val uri = Uri.parse(downloadURL)
                        mainActivity.startActivity(Intent(Intent.ACTION_VIEW, uri))
                    }
                }
            },
            { error ->
                v.text = con.getString(R.string.error_while_checking_for_update)
            }
        )
        // dont cache so it will actually check for updates every time
        jsonObjectRequest.setShouldCache(false)
        queue.add(jsonObjectRequest)
    }
}