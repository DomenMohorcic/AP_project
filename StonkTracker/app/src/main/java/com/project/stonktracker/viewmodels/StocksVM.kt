package com.project.stonktracker.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.project.stonktracker.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.HashMap

class StocksVM : ViewModel() {
    lateinit var repository: StocksRepository

    // Live data
    private var stocks = MutableLiveData<List<StockInfo>>()
    private var tickers_web = MutableLiveData<HashMap<String, List<String>>>()
    private var history = MutableLiveData<List<PurchaseHistory>>()

    fun init() {
        viewModelScope.launch(Dispatchers.IO) {
            stocks.postValue(repository.siGetPortfolio())
            tickers_web.postValue(repository.siGetTickersAndURLs())
            history.postValue(repository.phGetHistory())
        }
    }

    fun getHistory(): MutableLiveData<List<PurchaseHistory>> {
        return history
    }
    fun getStocks(): MutableLiveData<List<StockInfo>> {
        return stocks
    }
    fun getTickersAndURLs(): MutableLiveData<HashMap<String, List<String>>> {
        return tickers_web
    }

    fun updateCloses() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getCloses()
        }
    }

    fun siInsert(si: StockInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.siInsert(si)
            stocks.postValue(repository.siGetPortfolio())
            tickers_web.postValue(repository.siGetTickersAndURLs())
        }
    }

    /* Update this stock with new values (shares) */
    fun siUpdate(si: StockInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.siUpdate(si)
            stocks.postValue(repository.siGetPortfolio())
            tickers_web.postValue(repository.siGetTickersAndURLs())
        }
    }

    fun phInsert(ph: PurchaseHistory) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.phInsert(ph)
            Log.i("fragment_observe", "Posting value...")
            history.postValue(repository.phGetHistory())
            stocks.postValue(repository.siGetPortfolio())
            tickers_web.postValue(repository.siGetTickersAndURLs())
            Log.i("fragment_observe", "DONE?")
        }
    }
}

//
// REPOSITORY!
//

class StocksRepository(private val stonkDao: StonkDao) {

    // Closes

    fun getCloses() {
        var date = Calendar.getInstance().time
        val time = "${date.day}/${date.month+1}/${date.year+1900}"
        var updateList = ""
        var c = 0
        for(si in stonkDao.siGetStocksWithShares()) {
            if(!si.last_date.equals(time)) {
                if(c > 0) { updateList += "," }
                updateList += si.ticker
                c++
            }
        }
        if(!updateList.equals("")) {
            val url = "http://api.marketstack.com/v1/eod?access_key=$KEY_MARKETSTACK&symbols=$updateList&limit=$c"
            queue?.add(JsonObjectRequest(Request.Method.GET, url, null,
                { response ->
                    Log.i("request_api", "Got response")
                    if(response.has("error")) {
                        // API ERROR
                    } else {
                        val data = response.getJSONArray("data")
                        val updatedSI = emptyList<StockInfo>()
                        for(i in 0 until data.length()) {
                            val obj = data.getJSONObject(i)
                            val t = Thread {
                                var si = stonkDao.siGetTicker(obj.getString("symbol"))
                                si.last_date = time
                                si.last_close = obj.getDouble("close")
                                stonkDao.siUpdate(si)
                                c--
                            }
                            t.start()
                        }
                    }
                },
                { error -> Log.e("request_api", error.toString()) }))
        }
        while(c > 0) {}
    }

    // Purchase History

    fun phGetHistory(): List<PurchaseHistory> {
        return stonkDao.phGetAllInstances()
    }

    fun phGetCount(): Int {
        return stonkDao.phCountInstances()
    }

    fun phInsert(ph: PurchaseHistory) {
        var resultDone = false

        if(stonkDao.siCheckTicker(ph.ticker) == 1) {
            val si = stonkDao.siGetTicker(ph.ticker)
            val prev_price = si.shares * si.avg_price
            var new_price = when(ph.buy) {
                true -> prev_price + ph.quantity * ph.price
                false -> prev_price - ph.quantity * ph.price
            }
            var new_shares = when(ph.buy) {
                true -> si.shares + ph.quantity
                false -> si.shares - ph.quantity
            }
            if(new_shares < 0.00001) {si.avg_price = 0.0}
            else {si.avg_price = new_price / new_shares}
            si.shares = new_shares

            stonkDao.phInsert(ph)
            stonkDao.siUpdate(si)
            resultDone = true
        } else {
            val url = "https://api.polygon.io/v1/meta/symbols/${ph.ticker}/company?&apiKey=$KEY_POLYGON"
            queue?.add(JsonObjectRequest(Request.Method.GET, url, null,
                { response ->
                    if(response.has("error")) {
                        // API ERROR
                    } else {
                        val name: String = response.getString("name")
                        val sector: String = response.getString("sector")
                        val webURL: String = response.getString("url")
                        val webURLalt: String = response.getString("logo")
                        val si = StockInfo(ph.ticker, name, sector, webURL, webURLalt, ph.quantity, ph.price)
                        Log.i("fragment_observe", "Setting thread...")
                        val t = Thread {
                            stonkDao.phInsert(ph)
                            stonkDao.siInsert(si)
                            Log.i("fragment_observe", "Actually saving to DB...")
                            resultDone = true
                        }
                        t.start()
                    }
                },
                { error -> Log.e("request_error", error.toString()) }))
        }
        while(!resultDone) {}
        getCloses()
    }

    // STOCK INFO

    fun siGetPortfolio(): List<StockInfo> {
        return stonkDao.siGetStocksWithShares()
    }

    fun siGetTickersAndURLs(): HashMap<String, List<String>> {
        val tau = stonkDao.siGetTickersAndURLs()
        var hm = HashMap<String, List<String>>()
        tau.forEach { info ->
            hm[info.ticker] = listOf(info.webURL, info.webURL_alt)
        }
        return hm
    }

    fun siGetTicker(ticker: String): StockInfo {
        return stonkDao.siGetTicker(ticker)
    }

    fun siInsert(si: StockInfo) {
        stonkDao.siInsert(si)
    }

    fun siUpdate(si: StockInfo) {
        stonkDao.siUpdate(si)
    }
}