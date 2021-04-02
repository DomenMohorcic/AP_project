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
    private lateinit var _stocks: ArrayList<String>

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        _stocks = intent?.getStringArrayListExtra("TICKERS") as ArrayList<String>
        Log.i("service_info", "started SERVICE with $_stocks")
        thread.start()
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        thread = Thread {
            while(running) {
                Log.i("service", "Thread executed")
                val portfolioValue = getRealtimeStockPrices(_stocks)
                publishResults(portfolioValue)
                Thread.sleep(60000) // every minute
            }
        }
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

    fun updateStocks(s: ArrayList<String>) {
        _stocks = s
    }

    private fun getRealtimeStockPrices(stocks: ArrayList<String>): HashMap<String, Double> {
        Log.i("service", "getRealtimeStockPrices")
        val hm = HashMap<String, Double>()
        for(stock in stocks) {
            val url = "https://realstonks.p.rapidapi.com/$stock"
            queue?.add(object: StringRequest(Request.Method.GET, url,
                { response ->
                    val str = response.replace("\\", "")
                    val obj = JSONObject(str.substring(1, str.length-1))
                    Log.i("service", "Got $stock")
                    hm[stock] = obj.getDouble("price")
                }, { error -> Log.e("request_error", error.toString()) }) {
                override fun getHeaders(): MutableMap<String, String> {
                    return KEY_HEADER
                }
            })
        }
        while(hm.size < stocks.size) {}
        return hm
    }

    companion object {
        const val NOTIFICATION = "com.project.stonktracker.receiver"
        const val STOCK_DATA = "STOCK_DATA_KEY"
    }

    private fun publishResults(portfolioValue: HashMap<String, Double>) {
        Log.i("service", "Publishing")
        val intent = Intent(NOTIFICATION)
        intent.putExtra(STOCK_DATA, portfolioValue)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }
}