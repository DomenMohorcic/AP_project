package com.project.stonktracker.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import com.project.stonktracker.PurchaseHistory
import com.project.stonktracker.StockInfo

class FragmentVM : ViewModel() {

    private val stockInfo = MutableLiveData<StockInfo>()
    private var company = MutableLiveData<Company>()
    private var investment = MutableLiveData<Investment>()

    fun getStockInfo(): StockInfo {
        return stockInfo.value!!
    }

    fun setStockInfo(si: StockInfo) {
        stockInfo.postValue(si)
    }

    fun getCompany(): Company {
        return company.value!!
    }

    fun setCompany(cmp: Company) {
        company.postValue(cmp)
    }

    fun getInvestment(): Investment {
        return investment.value!!
    }

    fun setInvestment(inv: Investment) {
        investment.postValue(inv)
    }

}

data class Company(var ticker: String, var name: String, var sector: String)
data class Investment(var ticker: String, var shares: Double, var value: Double, var roi: Double, var roipercent: Double)