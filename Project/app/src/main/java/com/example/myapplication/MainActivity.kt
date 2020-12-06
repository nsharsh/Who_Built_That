package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val search = findViewById<Button>(R.id.button)
        search.setOnClickListener{
            val searchQuery = findViewById<TextView>(R.id.search_query).text
            val intent = Intent(this, Network::class.java)
            Log.i(TAG, searchQuery.toString())
            intent.putExtra(SEARCH_TAG, searchQuery.toString())
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return when (item.itemId) {
            R.id.action_settings -> {
                val builder: AlertDialog.Builder? = this?.let{
                    AlertDialog.Builder(it)
                }
                builder?.setTitle(R.string.app_name)
                    ?.setMessage(R.string.dialogue_text)
                val dialog: AlertDialog? = builder?.create()
                dialog?.show()
                true

            }
            else -> super.onOptionsItemSelected(item)
        }

    }

    companion object{
        private const val SEARCH_TAG = "search_query"
        private const val REVENUE_TAG = "annual_revenue"
        private const val EMPLOYEE_TAG = "employees"
        private const val OWNER_TAG = "owners"
        private const val LOCATION = "location"
        private const val SUB = "subsidiaries"
        val statsNames = arrayOf(REVENUE_TAG, EMPLOYEE_TAG, OWNER_TAG, LOCATION, SUB)
        private const val TAG = "Final_Proj"
    }


}