package com.example.myapplication

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL


class Network: Activity() {
    companion object {
        private const val TAG = "Final_Proj"
        private const val SEARCH_TAG = "search_query"
        private const val REVENUE_TAG = "annual_revenue"
        private const val EMPLOYEE_TAG = "employees"
        private const val OWNER_TAG = "owners"
        private const val LOCATION = "location"
        private const val SUB = "subsidiaries"
    }
    val statsNames = arrayOf(REVENUE_TAG, EMPLOYEE_TAG, OWNER_TAG, LOCATION, SUB)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fetchStats(intent.getStringExtra(SEARCH_TAG)!!, this, ArrayList<String>())
    }

    var output = ""

    fun fetchStats(query: String, context: Context, stats: ArrayList<String>){
        Log.i(TAG, "Fetching stats")
        // Fetching infobox from wikipedia
        val url_infobox = "https://en.wikipedia.org/w/api.php?action=query&prop=revisions&rvprop=content&format=json&titles=" + query + "&rvsection=0"
        var infobox_page = ""
        val infobox_task = DownloaderTask(url_infobox, object: MyCallBack {
            override fun finished(output: String) {
                // Cleanup some of the extra spacing for formatting
                infobox_page = output.replace(" +".toRegex(), " ")
                Log.i(TAG, infobox_page)
                val regexp_emp = Regex("num_employees = [{]*[a-zA-Z]*[}]* ?(([0-9]{1,3},?)+)")
                val regexp_loc = Regex("hq_location_city = ..((\\w+ ?,? ?)+)..")
                val regexp_rev = Regex("revenue")
                try {
                    val emp_count = regexp_emp.find(infobox_page, 0)!!.groupValues[1]
                    val loc = regexp_loc.find(infobox_page, 0)!!.groupValues[1]
                    Log.i(TAG, emp_count)
                    Log.i(TAG, loc)
                    stats.add("0")
                    stats.add(emp_count)
                    stats.add("None")
                    stats.add(loc)
                    stats.add("None Found")
                    val intent = Intent(this@Network, DetailedActivity::class.java)
                    for (i in stats.indices){
                        intent.putExtra(MainActivity.statsNames[i], stats[i])
                    }
                    intent.putExtra(SEARCH_TAG, query)
                    startActivity(intent)
                } catch (e : Exception){
                    Toast.makeText(context, "Could not find desired company", Toast.LENGTH_LONG).show()
                    intent = Intent(this@Network, MainActivity::class.java)
                    startActivity(intent)

                }


            }
        })
        infobox_task.execute()

    }
    @SuppressLint("StaticFieldLeak")
    inner class DownloaderTask(query_url: String, var callback: MyCallBack) : AsyncTask<Void, Void, String>() {
        var url = query_url

        override fun doInBackground(vararg params: Void?): String {
            Log.i(TAG, "inBackground")
            var data: String? = null
            var httpUrlConnection: HttpURLConnection? = null

            try {
                // 1. Get connection. 2. Prepare request (URI)
                httpUrlConnection = URL(url)
                    .openConnection() as HttpURLConnection


                // 3. This app does not use a request body
                // 4. Read the response
                val inputStream = BufferedInputStream(
                    httpUrlConnection.inputStream
                )

                data = readStream(inputStream)
            } catch (exception: MalformedURLException) {
                Log.e(TAG, "MalformedURLException")
            } catch (exception: IOException) {
                Log.e(TAG, exception.toString())
            } finally {
                httpUrlConnection?.disconnect()
            }
            return data!!

        }
        override fun onPostExecute(result: String) {
            Log.i(TAG, "OnPostExecute")
            callback.finished(result)
        }

        private fun readStream(inputStream: InputStream): String {
            Log.i(TAG, "readStream")
            val reader = BufferedReader(InputStreamReader(inputStream))
            val data = StringBuilder()
            val sep = System.getProperty("line.separator")
            try {
                reader.forEachLine {
                    Log.i(TAG, "Reading from socket")
                    data.append(it + sep)
                }
            } catch (e: IOException) {
                Log.e(TAG, e.toString())
            } finally {
                try {
                    reader.close()
                } catch (e: IOException) {
                    Log.e(TAG, e.toString())
                }
            }
            return data.toString()
        }

    }

}