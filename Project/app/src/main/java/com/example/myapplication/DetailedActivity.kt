package com.example.myapplication


import android.os.Bundle
import android.util.Log
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class DetailedActivity : AppCompatActivity() {
    private var firebase: Firebase? = null
    private lateinit var listView : ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detailed_activity)
        val actionBarTitle = this.intent.getStringExtra(SEARCH_TAG)
        firebase = Firebase()
        supportActionBar!!.title = actionBarTitle
        listView = findViewById(R.id.detailed_act)
        val stats = arrayListOf(firebase!!.annualRevenue.toString(), firebase!!.estEmployeeCount.toString(),
            firebase!!.owners.toString(), firebase!!.location.toString(), firebase!!.subsidiaries.toString())
        val adapter = InformationAdapter(this, stats)
        listView.adapter = adapter
    }
    companion object{
        private const val SEARCH_TAG = "search_query"
        private const val TAG = "Final_Proj"
    }
}