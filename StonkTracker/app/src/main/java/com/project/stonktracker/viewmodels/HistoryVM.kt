package com.project.stonktracker

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HistoryVM : ViewModel() {
    lateinit var repository: HistoryRepository

    private var history = MutableLiveData<List<PurchaseHistory>>()

    fun init() {
        /*insert(PurchaseHistory(0, "AAPL", "Oct 31st 2020", 80.0, 120.86, 0.0, true))
        insert(PurchaseHistory(0, "AAPL", "Oct 30th 2020", 32.0, 120.98, 0.0, true))
        insert(PurchaseHistory(0, "AAPL", "Oct 29th 2020", 20.0, 180.86, 0.0, true))
        insert(PurchaseHistory(0, "AAPL", "Oct 23rd 2020", 45.0, 150.86, 0.0, true))
        insert(PurchaseHistory(0, "AAPL", "Aug 1st 2020", 10.0, 115.86, 0.0, true))
        insert(PurchaseHistory(0, "AAPL", "Jun 15th 2020", 15.0, 110.86, 0.0, true))
        insert(PurchaseHistory(0, "AAPL", "Jan 31st 2020", 10.0, 98.86, 0.0, true))
        insert(PurchaseHistory(0, "AAPL", "Oct 25th 2018", 2.0, 70.86, 0.0, true))
        insert(PurchaseHistory(0, "AAPL", "Mar 18th 2017", 5.0, 80.86, 0.0, true))
        insert(PurchaseHistory(0, "AAPL", "Oct 2nd 2005", 1.0, 70.86, 0.0, true))*/
        viewModelScope.launch(Dispatchers.IO) {
            history.postValue(repository.getHistory())
        }
    }

    fun getHistory(): MutableLiveData<List<PurchaseHistory>> {
        return history
    }

    fun insert(ph: PurchaseHistory) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(ph)
            history.postValue(repository.getHistory())
        }
    }
}

class HistoryRepository(private val stonkDao: StonkDao) {
    fun getHistory(): List<PurchaseHistory> {
        return stonkDao.phGetAllInstances()
    }

    fun getCount(): Int {
        return stonkDao.phCountInstances()
    }

    fun insert(ph: PurchaseHistory) {
        // also updates stockInfo table
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
        } else {
            val url = "https://api.polygon.io/v1/meta/symbols/${ph.ticker}/company?&apiKey=${KEY_POLYGON}"
            queue?.add(JsonObjectRequest(Request.Method.GET, url, null,
                { response ->
                    if(!response.has("status")) {
                        val name: String = response.getString("name")
                        val sector: String = response.getString("sector")
                        val webURL: String = response.getString("url")
                        val si = StockInfo(ph.ticker, name, sector, webURL, ph.quantity, ph.price)
                        val t = Thread {
                            stonkDao.phInsert(ph)
                            stonkDao.siInsert(si)
                        }
                        t.start()
                    } else {
                        // API key usage error
                    }
                },
                { error -> Log.e("request_error", error.toString()) }))
        }
    }
}