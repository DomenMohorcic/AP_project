package com.project.stonktracker

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.project.stonktracker.databinding.ActivityMainBinding
import com.project.stonktracker.viewmodels.FragmentVM
import com.project.stonktracker.viewmodels.StocksRepository
import com.project.stonktracker.viewmodels.StocksVM
import java.util.*

var queue: RequestQueue? = null

var prev_fragment: Int = FTracker.PORTFOLIO
var now_fragment: Int = FTracker.PORTFOLIO

const val KEY_VANTAGE: String = "RTUYSN1G309FMPH2"
const val KEY_POLYGON: String = "Hd5NWeZJWpSOEQFfQdpj0yENXqlSkoYe"
const val KEY_MARKETSTACK: String = "e60e3314fa7e58010abacef621cfc246"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var nav: BottomNavigationView
    private lateinit var selectedFragment: Fragment

    private val fragmentVM: FragmentVM by viewModels()
    private val stocksVM: StocksVM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        stocksVM.repository = StocksRepository(StonkDatabase.getStonkDatabase(this)!!.stonkDao())
        queue = Volley.newRequestQueue(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        nav = binding.bottomNavigationView
        nav.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.nav_home -> {
                    selectedFragment = PortfolioFragment()
                    now_fragment = FTracker.PORTFOLIO
                }
                R.id.nav_history -> {
                    selectedFragment = HistoryFragment()
                    now_fragment = FTracker.HISTORY
                }
                R.id.nav_search -> {
                    selectedFragment = SearchFragment()
                    now_fragment = FTracker.SEARCH
                }
                R.id.nav_stats -> {
                    selectedFragment = PortfolioFragment()// TransactionFragment()
                    now_fragment = FTracker.STATS
                }
            }

            Log.i("fragment_log", "$selectedFragment")

            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment, selectedFragment)
            // don't put fragments on stack -- Coolio
            //
            //fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()

            return@setOnNavigationItemSelectedListener true
        }

    }

    /* Double back press for exit */
    private var doublePressed: Boolean = false
    override fun onBackPressed() {
        if(now_fragment == FTracker.STOCK) {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment, PortfolioFragment())
            now_fragment = FTracker.PORTFOLIO
            fragmentTransaction.commit()
        } else if(now_fragment == FTracker.TRANSACTION) {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment, SearchFragment())
            now_fragment = FTracker.SEARCH
            fragmentTransaction.commit()
        } else {
            if(doublePressed) { super.onBackPressed() }
            else {
                doublePressed = true;
                Toast.makeText(this, "Click BACK again to exit", Toast.LENGTH_SHORT).show();
                Handler().postDelayed(Runnable {doublePressed = false}, 2000)
            }
        }
    }
}
class FTracker {
    companion object {
        const val PORTFOLIO: Int = 0
        const val STATS: Int = 1
        const val SEARCH: Int = 2
        const val HISTORY: Int = 3
        const val STOCK: Int = 4
        const val TRANSACTION: Int = 5
    }
}
