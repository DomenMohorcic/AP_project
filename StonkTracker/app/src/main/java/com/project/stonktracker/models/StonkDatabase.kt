package com.project.stonktracker

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import java.util.*

@Database(entities = [PurchaseHistory::class, StockInfo::class], version = 1, exportSchema = false)
abstract class StonkDatabase: RoomDatabase() {
    abstract fun stonkDao(): StonkDao

    companion object {
        private var INSTANCE: StonkDatabase? = null

        fun getStonkDatabase(context: Context): StonkDatabase? {
            if(INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.applicationContext, StonkDatabase::class.java, "ph-database")
                    .build()
            }
            return INSTANCE
        }
    }
}

@Entity(tableName = "purchaseHistory")
data class PurchaseHistory (
    @PrimaryKey(autoGenerate = true) var pID: Int = 0,
    @ColumnInfo var ticker: String = "",
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
    @ColumnInfo var sector: String = "",
    @ColumnInfo var webURL: String = "",
    @ColumnInfo var webURL_alt: String = "",
    @ColumnInfo var shares: Double = 0.0,
    @ColumnInfo var avg_price: Double = 0.0,
    @ColumnInfo var last_close: Double = 0.0,
    @ColumnInfo var last_volume: Int = 0,
    @ColumnInfo var last_date: String = "01/01/1970",
    @ColumnInfo var PE: Double = 0.0,
    @ColumnInfo var EPS: Double = 0.0,
    @ColumnInfo var market_cap: Double = 0.0
)

data class InfoTickerUrl (
    @ColumnInfo val ticker: String = "",
    @ColumnInfo val webURL: String = "",
    @ColumnInfo val webURL_alt: String = ""
)

@Dao
interface StonkDao {

    // PURCHASE HISTORY DATABASE

    @Query("SELECT DISTINCT ticker FROM purchaseHistory")
    fun phGetAllTickers(): List<String>

    @Query("SELECT * FROM purchaseHistory WHERE ticker LIKE :ticker ORDER BY date DESC")
    fun phGetTicker(ticker: String): List<PurchaseHistory>

    @Query("SELECT * FROM purchaseHistory ORDER BY date DESC, ticker ASC")
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

    @Query("SELECT * FROM stockInfo WHERE shares > 0 ORDER BY ticker ASC")
    fun siGetStocksWithShares(): List<StockInfo>

    @Query("SELECT COUNT(1) FROM stockInfo WHERE ticker LIKE :ticker")
    fun siCheckTicker(ticker: String): Int

    @Query("SELECT * FROM stockInfo ORDER BY ticker ASC")
    fun siGetAllInstances(): List<StockInfo>

    @Query("SELECT DISTINCT ticker, webURL, webURL_alt FROM stockInfo")
    fun siGetTickersAndURLs(): List<InfoTickerUrl>

    @Query("SELECT COUNT(*) FROM stockInfo")
    fun siCountInstances(): Int

    @Insert(entity = StockInfo::class)
    fun siInsert(si: StockInfo)

    @Update(entity = StockInfo::class)
    fun siUpdate(si: StockInfo)
}