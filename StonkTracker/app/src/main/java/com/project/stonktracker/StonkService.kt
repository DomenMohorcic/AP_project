package com.project.stonktracker

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
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

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()

        thread = Thread {
            while (running) {
                portfolioValue = getJokeFromInternet()
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

    private fun getJokeFromInternet(): String {
        val json = JSONObject(URL("https://api.icndb.com/jokes/random").readText())

        val type = json.getString("type")
        val joke = JSONObject(json.getString("value")).getString("joke")
        val id = JSONObject(json.getString("value")).getString("id")

        Log.i("API_Request", joke)

        return joke
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