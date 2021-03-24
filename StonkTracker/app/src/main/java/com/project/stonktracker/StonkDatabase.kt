package com.project.stonktracker

import android.content.Context
import androidx.room.*

@Database(entities = [PurchaseHistory::class, StockInfo::class], version = 1, exportSchema = false)
abstract class StonkDatabase: RoomDatabase() {
    abstract fun stonkDao(): StonkDao

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
    @PrimaryKey(autoGenerate = true) var pID: Int = 0,
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
    @ColumnInfo var shares: Int = 0
)

@Dao
interface StonkDao {

    // PURCHASE HISTORY DATABASE

    @Query("SELECT DISTINCT ticker FROM purchaseHistory")
    fun phGetAllTickers(): List<String>

    @Query("SELECT * FROM purchaseHistory WHERE ticker LIKE :ticker")
    fun phGetTicker(ticker: String): List<PurchaseHistory>

    @Query("SELECT * FROM purchaseHistory")
    fun phGetAllInstances(): List<PurchaseHistory>

    @Query("SELECT COUNT(*) FROM purchaseHistory")
    fun phCountInstances(): Int

    @Query("SELECT SUM(quantity) FROM purchaseHistory WHERE ticker LIKE :ticker")
    fun phGetSharesTicker(ticker: String): Double

    @Query("SELECT SUM(price) FROM purchaseHistory WHERE ticker LIKE :ticker AND buy")
    fun phGetBoughtTicker(ticker: String): Double

    @Query("SELECT SUM(price) FROM purchaseHistory WHERE ticker LIKE :ticker AND NOT buy")
    fun phGetSoldTicker(ticker: String): Double

    @Insert(entity = PurchaseHistory::class)
    fun phInsert(ph: PurchaseHistory)

    @Delete(entity = PurchaseHistory::class)
    fun phDelete(ph: PurchaseHistory)


    // STOCK INFO DATABASE

    @Query("SELECT * FROM stockInfo WHERE ticker LIKE :ticker")
    fun siGetTicker(ticker: String): StockInfo

    @Query("SELECT * FROM stockInfo WHERE shares > 0")
    fun siGetStocksWithShares(): List<StockInfo>

    @Query("SELECT * FROM stockInfo")
    fun siGetAllInstances(): List<StockInfo>

    @Query("SELECT COUNT(*) FROM stockInfo")
    fun siCountInstances(): Int

    @Insert(entity = StockInfo::class)
    fun siInsert(si: StockInfo)
}