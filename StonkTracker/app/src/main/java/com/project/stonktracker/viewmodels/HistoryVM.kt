package com.project.stonktracker

import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
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
            Log.i("fragment_observe", "Posting value...")
            history.postValue(repository.getHistory())
            Log.i("fragment_observe", "DONE?")
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

    // BE CAREFUL!!! --> USE ONLY IN THREADS
    fun insert(ph: PurchaseHistory) {
        // var resultDone = false

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
            // resultDone = true
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
                            Log.i("fragment_observe", "Actually saving to DB...")
                            // resultDone = true
                        }
                        t.start()
                    } else {
                        // API key usage error
                    }
                },
                { error -> Log.e("request_error", error.toString()) }))
        }

        // while (!resultDone) {}
    }
}