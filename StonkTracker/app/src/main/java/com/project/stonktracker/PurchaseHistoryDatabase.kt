package com.project.stonktracker

import android.content.Context
import androidx.room.*

@Database(entities = [PurchaseHistory::class], version = 1, exportSchema = false)
abstract class PurchaseHistoryDatabase: RoomDatabase() {
    abstract fun phDao(): PurchaseHistoryDao

    companion object {
        private var INSTANCE: PurchaseHistoryDatabase? = null

        fun getPurchaseHistoryDatabase(context: Context): PurchaseHistoryDatabase? {
            if(INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.applicationContext, PurchaseHistoryDatabase::class.java, "ph-database")
                    .allowMainThreadQueries().build()
            }
            return INSTANCE
        }
    }
}

@Entity(tableName = "purchaseHistory")
class PurchaseHistory {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    var ticker: String = "null"
    var date: String = ""
    var quantity: Int = 0
    var price: Double = 0.0
    var fee: Double = 0.0
    var buy: Boolean = false
}

@Dao
interface PurchaseHistoryDao {
    @Query("SELECT DISTINCT ticker FROM purchaseHistory")
    fun getAllTickers(): List<String>

    @Query("SELECT * FROM purchaseHistory WHERE ticker LIKE :ticker")
    fun getTicker(ticker: String): List<PurchaseHistory>

    @Insert
    fun insert(ph: PurchaseHistory?)
}