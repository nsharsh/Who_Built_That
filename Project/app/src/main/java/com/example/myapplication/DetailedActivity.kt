package com.example.myapplication


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class DetailedActivity : AppCompatActivity() {
    private var firebase: Firebase? = null
    private lateinit var listView : ListView
    var stats = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detailed_activity)
        val actionBarTitle = this.intent.getStringExtra(SEARCH_TAG)
        firebase = Firebase()
        supportActionBar!!.title = actionBarTitle
        for (i in statsNames.indices){
            val ele = intent.getStringExtra(statsNames[i]) as String
            stats.add(ele)
        }
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