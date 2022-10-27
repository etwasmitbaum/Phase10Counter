package com.tjEnterprises.phase10Counter

import android.content.Context
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

class UpdateChecker (val con: Context) {

    fun checkForUpdate(v: TextView) {
        //TODO help
        val queue = Volley.newRequestQueue(this.con)
        val URL = "https://api.github.com/repos/etwasmitbaum/Phase10Counter/releases/latest"
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, URL, null,
            { response ->
                v.text = "Response: %s".format(response.toString())
            },
            { error ->
                v.text = error.toString()
            }
        )
        queue.add(jsonObjectRequest)
    }
}