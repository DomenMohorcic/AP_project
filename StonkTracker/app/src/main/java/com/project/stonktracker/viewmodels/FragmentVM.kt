package com.project.stonktracker.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import com.project.stonktracker.PurchaseHistory

class FragmentVM : ViewModel() {

    private var company = MutableLiveData<Company>()

    fun getCompany(): Company {
        return company.value!!
    }

    fun setCompany(cmp: Company) {
        company.postValue(cmp)
    }

}

data class Company(var ticker: String, var name: String)