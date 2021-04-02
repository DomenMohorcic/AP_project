package com.project.stonktracker

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.project.stonktracker.viewmodels.StocksVM
import org.json.JSONObject
import java.lang.Exception
import java.net.URL

// TODO

class StonkService : Service() {

    @Volatile
    private var running = true
    private lateinit var thread: Thread

    private var count: Int = 0
    private var portfolioValue: String = ""
    private lateinit var stocks: ArrayList<String>

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        stocks = intent?.getStringArrayListExtra("TICKERS") as ArrayList<String>
        Log.i("service_info", "started SERVICE with $stocks")
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()

        thread = Thread {
            while (running) {
                portfolioValue = getRealtimeStockPrices()
                count += 1
                Log.i("jokes_notification", portfolioValue)
                publishResults(portfolioValue, count)
                Thread.sleep(5000)
            }
        }

        thread.start()
    }

    override fun onDestroy() {
        running = false
        try {
            thread.join()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onDestroy()
    }

    // not using right now...
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun getRealtimeStockPrices(): String {
        val url = "https://realstonks.p.rapidapi.com/TSLA"
        queue?.add(object: StringRequest(Request.Method.GET, url,
            { response ->
                Log.i("request", response.toString())
                val str = response.replace("\\", "")
                val obj = JSONObject(str.substring(1, str.length-1))
                Log.i("request", obj.toString())
            }, { error -> Log.e("request_error", error.toString()) }) {
            override fun getHeaders(): MutableMap<String, String> {
                return KEY_HEADER
            }
        })

        return "0"
        /*val json = JSONObject(URL("https://api.icndb.com/jokes/random").readText())

        val type = json.getString("type")
        val joke = JSONObject(json.getString("value")).getString("joke")
        val id = JSONObject(json.getString("value")).getString("id")

        Log.i("service_info", joke)

        return joke*/
    }

    companion object {
        const val NOTIFICATION = "com.example.exercise10startservice.receiver"
        const val JOKE_TEXT = "JOKE_TEXT_KEY"
        const val JOKE_COUNTER = "JOKE_COUNTER_KEY"
    }

    private fun publishResults(jokeText: String, jokeCouter: Int) {
        val intent = Intent(NOTIFICATION)
        intent.putExtra(JOKE_COUNTER, jokeCouter)
        intent.putExtra(JOKE_TEXT, jokeText)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

}