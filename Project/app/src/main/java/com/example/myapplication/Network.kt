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
        private const val FOUNDER_TAG = "founders"
        private const val LOCATION = "location"
        private const val SUB = "subsidiaries"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fetchStats(intent.getStringExtra(SEARCH_TAG)!!, this, ArrayList<String>())

    }

    private fun fetchStats(query: String, context: Context, stats: ArrayList<String>){
        Log.i(TAG, "Fetching stats")
        // Fetching infobox from wikipedia
        val url_infobox =
            "https://en.wikipedia.org/w/api.php?action=query&prop=revisions&rvprop=content&format=json&titles=$query&rvsection=0"
        var infobox_page = ""
        val infobox_task = DownloaderTask(url_infobox, object: MyCallBack {
            override fun finished(output: String) {
                // Cleanup some of the extra spacing for formatting
                infobox_page = output.replace(" +".toRegex(), " ")
                Log.i(TAG, infobox_page)
                val regexp_emp = Regex("num_employees = [{]*[a-zA-Z]*\\|*[}]* ?(([0-9]{1,3},?)+)")
                val regexp_loc = Regex("((location|location_city|hq_location|hq_location_city) =.+\\[{2}((\\w ?)+, (\\w ?|[A-Za-z\\|])+)+\\]{2})")
                val regexpLoc = Regex("\\[{2}((\\w ?)+, (\\w ?|[A-Za-z\\|])+)+\\]{2}")
                val regexpRevLine = Regex("revenue =[A-Za-z\\{ \\}]*[\\{\\[A-Za-z \\|\\]\\\$\\}]*(\\d+.\\d+)[A-Z0-9,a-z&*;\\[| ]*(billion|trillion|million)")
                val regexpRev = Regex("(\\d+.\\d+)[A-Z0-9,a-z&*;\\[| ]*(billion|trillion|million)")
                val regexpFoundersLine = Regex("(founders|founder) =[A-Za-z\\{ \\|\\=\\\\]*(\\[*(\\w+ \\w+|\\w+[\\.]* \\w+[\\.]* \\w+)\\]*[ \\|\\}\\\\a-z]*)+\\\\n")
                val regexpFounders = Regex("(\\[*(\\w+[\\.]* \\w+[\\.]* \\w+|\\w+ \\w+)\\]*[ \\|\\}\\\\a-z]*)+")
                val regexpSubsidLine = Regex("subsid =[A-Za-z\\{ \\|\\\\]*(\\[*(\\w+[A-Za-z \\,]*)+\\]*[ \\|\\}\\\\]*)+\\\\n")
                val regexpSubsid = Regex("\\{\\{(\\[*(\\w+[A-Za-z \\,]*)+\\]*[ \\|\\}\\\\]*)+\\}\\}")

                // Try Getting Revenue
                try {
                    val revLine = regexpRevLine.find(infobox_page, 0)!!.groupValues[0]
                    val rev = regexpRev.find(revLine, 0)!!.groupValues[1] + " " + regexpRev.find(revLine, 0)!!.groupValues[2]
                    Log.i(TAG, rev)
                    stats.add(rev)
                } catch (e : Exception) {
                    Toast.makeText(context, "Could not find revenue", Toast.LENGTH_LONG).show()
                    e.printStackTrace()
                    stats.add("N/A")
                }

                // Try Getting Employee Count
                try {
                    val empCount = regexp_emp.find(infobox_page, 0)!!.groupValues[1]
                    Log.i(TAG, empCount)
                    stats.add(empCount)
                } catch (e : Exception) {
                    Toast.makeText(context, "Could not find employee count", Toast.LENGTH_LONG).show()
                    e.printStackTrace()
                    stats.add("N/A")
                }

                // Try Getting Founders
                try {
                    val founderLine = regexpFoundersLine.find(infobox_page, 0)!!.groupValues[0]
                    val founders = regexpFounders.find(founderLine, 0)!!.groupValues[0]
                        .replace("[","").replace("]", "")
                        .replace("{","").replace("}","")
                        .replace("|", ",").replace("\\\\n".toRegex(), "")
                        .replace("Unbulleted list, ", "").replace("ubl","")
                    Log.i(TAG, founders)
                    stats.add(founders)
                } catch (e : Exception) {
                    Toast.makeText(context, "Could not find founders", Toast.LENGTH_LONG).show()
                    e.printStackTrace()
                    stats.add("N/A")
                }

                // Try Getting Location
                try {
                    val locLine = regexp_loc.find(infobox_page, 0)!!.groupValues[0]
                    val loc = regexpLoc.find(locLine, 0)!!.groupValues[1]
                    Log.i(TAG, loc)
                    stats.add(loc)
                } catch (e : Exception) {
                    Toast.makeText(context, "Could not find location", Toast.LENGTH_LONG).show()
                    e.printStackTrace()
                    stats.add("N/A")
                }

                // Try Getting Subsidiaries
                try {
                    val subsidLine = regexpSubsidLine.find(infobox_page, 0)!!.groupValues[0]
                    var subsid = regexpSubsid.find(subsidLine, 0)!!.groupValues[0]
                        .replace("\\\\n ".toRegex(), "").replace("ubl| ","")
                        .replace("ubl|","").replace("[","").replace("]", "")
                        .replace("{","").replace("}","")
                        .replace("|", ",").replace("\\\\n".toRegex(), "")
                    Log.i(TAG, subsid)
                    stats.add(subsid)
                } catch (e : Exception) {
                    Toast.makeText(context, "Could not find subsidiaries", Toast.LENGTH_LONG).show()
                    e.printStackTrace()
                    stats.add("N/A")
                }

                // Packaging Intent
                val intent = Intent(this@Network, DetailedActivity::class.java)
                for (i in stats.indices){
                    intent.putExtra(MainActivity.statsNames[i], stats[i])
                }
                intent.putExtra(SEARCH_TAG, query)
                startActivity(intent)
            }
        })
        infobox_task.execute()
    }

    // Used to get rid of blank Activity screen between transition from MainActivity to DetailedView
    override fun onResume() {
        super.onResume()
        finish()
    }

    @SuppressLint("StaticFieldLeak")
    inner class DownloaderTask(query_url: String, var callback: MyCallBack) : AsyncTask<Void, Void, String>() {
        private var url = query_url

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