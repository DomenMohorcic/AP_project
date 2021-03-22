package com.project.stonktracker

import android.content.Context
import androidx.room.*

@Database(entities = [StockInfo::class], version = 1, exportSchema = false)
abstract class StockInfoDatabase: RoomDatabase() {
    abstract fun siDao(): StockInfoDao

    companion object {
        private var INSTANCE: StockInfoDatabase? = null

        fun getStockInfoDatabase(context: Context): StockInfoDatabase? {
            if(INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.applicationContext, StockInfoDatabase::class.java, "si-database")
                    .allowMainThreadQueries().build()
            }
            return INSTANCE
        }
    }
}

@Entity(tableName = "stockInfo")
class StockInfo {
    @PrimaryKey
    var ticker: String = ""

    var full_name: String = ""
    var webURL: String = ""
    var imgURL: String = ""
}

@Dao
interface StockInfoDao {
    @Query("SELECT * FROM stockInfo WHERE ticker LIKE :ticker")
    fun getTicker(ticker: String?): StockInfo?

    @Insert
    fun insert(si: StockInfo?)
}