package com.example.myapplication


import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

// This activity shows the detailed view of the company with the given company info from Network.kt
class DetailedActivity : AppCompatActivity() {
    private lateinit var listView : ListView
    var stats = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detailed_activity)

        // Setting Title name
        val actionBarTitle = this.intent.getStringExtra(SEARCH_TAG)
        supportActionBar!!.title = actionBarTitle

        // Adding all the intent elements into arraylist
        for (i in statsNames.indices){
            val ele = intent.getStringExtra(statsNames[i]) as String
            stats.add(ele)
        }

        // Setting ListView with the InformationAdapter class
        listView = findViewById(R.id.detailed_act)
        val adapter = InformationAdapter(this, stats)
        listView.adapter = adapter
    }

    companion object{
        private const val SEARCH_TAG = "search_query"
        private const val REVENUE_TAG = "annual_revenue"
        private const val EMPLOYEE_TAG = "employees"
        private const val FOUNDER_TAG = "founders"
        private const val LOCATION = "location"
        private const val SUB = "subsidiaries"
        private val statsNames = arrayOf(REVENUE_TAG, EMPLOYEE_TAG, FOUNDER_TAG, LOCATION, SUB)
        private const val TAG = "Final_Proj"
    }
}