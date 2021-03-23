package com.project.stonktracker

import android.content.Context
import androidx.room.*

@Database(entities = [PurchaseHistory::class, StockInfo::class], version = 1, exportSchema = false)
abstract class StonkDatabase: RoomDatabase() {
    abstract fun phDao(): StonkDao

    companion object {
        private var INSTANCE: StonkDatabase? = null

        fun getStonkDatabase(context: Context): StonkDatabase? {
            if(INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.applicationContext, StonkDatabase::class.java, "ph-database").build()
            }
            return INSTANCE
        }
    }
}

@Entity(tableName = "purchaseHistory")
data class PurchaseHistory (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo var ticker: String = "null",
    @ColumnInfo var date: String = "",
    @ColumnInfo var quantity: Double = 0.0,
    @ColumnInfo var price: Double = 0.0,
    @ColumnInfo var fee: Double = 0.0,
    @ColumnInfo var buy: Boolean = false
)

@Entity(tableName = "stockInfo")
data class StockInfo (
    @PrimaryKey val ticker: String = "",
    @ColumnInfo var full_name: String = "",
    @ColumnInfo var webURL: String = "",
    @ColumnInfo var imgURL: String = ""
)

@Dao
interface StonkDao {

    // PURCHASE HISTORY DATABASE

    @Query("SELECT DISTINCT ticker FROM purchaseHistory")
    fun getAllTickers(): List<String>

    @Query("SELECT * FROM purchaseHistory WHERE ticker LIKE :ticker")
    fun getTicker(ticker: String): List<PurchaseHistory>

    @Query("SELECT *")
    fun getAllInstances(): List<PurchaseHistory>

    @Query("SELECT COUNT(*)")
    fun countInstances(): Int

    @Query("SELECT SUM(quantity) FROM purchaseHistory WHERE ticker LIKE :ticker")
    fun getSharesTicker(ticker: String): Double

    @Query("SELECT SUM(price) FROM purchaseHistory WHERE ticker LIKE :ticker AND buy")
    fun getPaidTicker(ticker: String): Double

    @Query("SELECT SUM(price) FROM purchaseHistory WHERE ticker LIKE :ticker AND NOT buy")
    fun getSoldTicker(ticker: String): Double

    @Insert(entity = PurchaseHistory::class)
    fun insert(ph: PurchaseHistory?)


    // STOCK INFO DATABASE

    @Query("SELECT * FROM stockInfo WHERE ticker LIKE :ticker")
    fun getTicker(ticker: String?): StockInfo?

    @Insert(entity = StockInfo::class)
    fun insert(si: StockInfo?)
}