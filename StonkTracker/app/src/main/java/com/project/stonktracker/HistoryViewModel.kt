package com.project.stonktracker

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HistoryViewModel : ViewModel() {
    // TODO: Implement the ViewModel

    private val historyRepo: HistoryRepository = HistoryRepository()
    // watch history object in fragments
    val history: MutableLiveData<List<PurchaseHistory>> = MutableLiveData<List<PurchaseHistory>>()

    fun getHistory() {
        //history.value = historyRepo.getHistory()!!
    }
}

class HistoryRepository : Application() {
    /*private var db: StonkDatabase? = null
    private var stonkDao: StonkDao? = null

    init {
        db = StonkDatabase.getStonkDatabase(this.applicationContext)
        stonkDao = db?.stonkDao()
    }

    fun getHistory(): List<PurchaseHistory>? {
        return stonkDao?.phGetAllInstances()
    }*/

    // tle je koda za background klice na apije...
}