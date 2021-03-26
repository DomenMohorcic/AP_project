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

    var stocks = MutableLiveData<List<StockInfo>>()

    var count: Int = 0

    // tole je shit....
    fun init() {
        var is_init = true
        viewModelScope.launch(Dispatchers.IO) {
            count = repository.getCount()
        }
        if(count == 0) {
            // is_init = false
            /*viewModelScope.launch(Dispatchers.IO) {
                repository.insert(StockInfo("T", "AT&T", "att.com", 50))
                repository.insert(StockInfo("AAPL", "Apple Inc.", "apple.com", 25))
                repository.insert(StockInfo("STB", "Starbucks", "starbucks.com", 45))
                repository.insert(StockInfo("CSCO", "Cisco", "cisco.com", 42))
                repository.insert(StockInfo("KO", "Coca-Cola", "coca-colacompany.com", 50))
                repository.insert(StockInfo("VICI", "VICI Properties", "viciproperties.com", 69))
                repository.insert(StockInfo("TSLA", "Tesla", "tesla.com", 42069))
                repository.insert(StockInfo("MCD", "McDonald's Corp", "mcdonalds.com", 1))
                is_init = true
            }*/
        }
        while(!is_init) {}
        viewModelScope.launch(Dispatchers.IO) {
            stocks.postValue(repository.getPortfolio())
        }
    }

    /* Insert new stock into stock info database */
    fun insert(si: StockInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(si)
            stocks.postValue(repository.getPortfolio())
        }
    }

    /* Update this stock with new values (shares) */
    fun update(si: StockInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.update(si)
            stocks.postValue(repository.getPortfolio())
        }
    }
}

class PortfolioRepository(private val stonkDao: StonkDao) {
    fun getPortfolio(): List<StockInfo> {
        return stonkDao.siGetAllInstances()
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