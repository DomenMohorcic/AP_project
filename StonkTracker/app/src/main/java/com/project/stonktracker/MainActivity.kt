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
var KEY_VANTAGE: String = "RTUYSN1G309FMPH2"
var KEY_POLYGON: String = "Hd5NWeZJWpSOEQFfQdpj0yENXqlSkoYe"
var KEY_MARKETSTACK: String = "e60e3314fa7e58010abacef621cfc246"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var nav: BottomNavigationView
    private lateinit var selectedFragment: Fragment

    //private val portfolioVM: PortfolioVM by viewModels()
    //private val historyVM: HistoryVM by viewModels()
    private val fragmentVM: FragmentVM by viewModels()

    private val stocksVM: StocksVM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //portfolioVM.repository = PortfolioRepository(StonkDatabase.getStonkDatabase(this)!!.stonkDao())
        //historyVM.repository = HistoryRepository(StonkDatabase.getStonkDatabase(this)!!.stonkDao())

        stocksVM.repository = StocksRepository(StonkDatabase.getStonkDatabase(this)!!.stonkDao())

        queue = Volley.newRequestQueue(this)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        nav = binding.bottomNavigationView
        nav.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.nav_home -> selectedFragment = PortfolioFragment()
                R.id.nav_history -> selectedFragment = HistoryFragment()
                R.id.nav_search -> selectedFragment = SearchFragment()
                R.id.nav_stats -> selectedFragment = PortfolioFragment() // TransactionFragment()
            }

            Log.i("fragment_log", "$selectedFragment")

            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment, selectedFragment)
            // don't put fragments on stack -- Coolio
            // fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()

            return@setOnNavigationItemSelectedListener true
        }

    }

    /* Double back press for exit */
    /*private var doublePressed: Boolean = false
    override fun onBackPressed() {
        if(doublePressed) { super.onBackPressed() }
        else {
            doublePressed = true;
            Toast.makeText(this, "Click BACK again to exit", Toast.LENGTH_SHORT).show();
            Handler().postDelayed(Runnable {doublePressed = false}, 2000)
        }
    }*/
}