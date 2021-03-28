package com.project.stonktracker

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PortfolioVM : ViewModel() {
    lateinit var repository: PortfolioRepository

    private var stocks = MutableLiveData<List<StockInfo>>()
    private var tickers_web = MutableLiveData<HashMap<String, String>>()

    fun init() {
        /* insert(StockInfo("T", "AT&T", "att.com", 50.0, 5000.0))
        insert(StockInfo("AAPL", "Apple Inc.", "apple.com", 25.0, 5000.0))
        insert(StockInfo("STB", "Starbucks", "starbucks.com", 45.0, 5000.0))
        insert(StockInfo("CSCO", "Cisco", "cisco.com", 42.0, 5000.0))
        insert(StockInfo("KO", "Coca-Cola", "coca-colacompany.com", 50.0, 5000.0))
        insert(StockInfo("VICI", "VICI Properties", "viciproperties.com", 69.0, 5000.0))
        insert(StockInfo("TSLA", "Tesla", "tesla.com", 42069.0, 5000.0))
        insert(StockInfo("MCD", "McDonald's Corp", "mcdonalds.com", 1.0, 5000.0)) */

        viewModelScope.launch(Dispatchers.IO) {
            stocks.postValue(repository.getPortfolio())
            tickers_web.postValue(repository.getTickersAndURLs())
        }
    }

    fun getStocks(): MutableLiveData<List<StockInfo>> {
        return stocks
    }
    fun getTickersAndURLs(): MutableLiveData<HashMap<String, String>> {
        return tickers_web
    }

    /* Insert new stock into stock info database */
    fun insert(si: StockInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(si)
            stocks.postValue(repository.getPortfolio())
            tickers_web.postValue(repository.getTickersAndURLs())
        }
    }

    /* Update this stock with new values (shares) */
    fun update(si: StockInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.update(si)
            stocks.postValue(repository.getPortfolio())
            tickers_web.postValue(repository.getTickersAndURLs())
        }
    }
}

class PortfolioRepository(private val stonkDao: StonkDao) {
    fun getPortfolio(): List<StockInfo> {
        return stonkDao.siGetStocksWithShares()
    }

    fun getTickersAndURLs(): HashMap<String, String> {
        val tau = stonkDao.siGetTickersAndURLs()
        var hm = HashMap<String, String>()
        tau.forEach { info ->
            hm[info.ticker] = info.webURL
        }
        return hm
    }

    fun getCount(): Int {
        return stonkDao.siCountInstances()
    }

    fun insert(si: StockInfo) {
        stonkDao.siInsert(si)
    }

    fun update(si: StockInfo) {
        stonkDao.siUpdate(si)
    }
}