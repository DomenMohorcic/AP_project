package com.project.stonktracker

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.project.stonktracker.databinding.ActivityMainBinding
import com.project.stonktracker.viewmodels.FragmentVM

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var nav: BottomNavigationView
    private lateinit var selectedFragment: Fragment

    // private val historyModel: HistoryViewModel by viewModels()
    private val portfolioVM: PortfolioVM by viewModels()
    private val historyVM: HistoryVM by viewModels()
    private val fragmentVM: FragmentVM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        portfolioVM.repository = PortfolioRepository(StonkDatabase.getStonkDatabase(this)!!.stonkDao())
        historyVM.repository = HistoryRepository(StonkDatabase.getStonkDatabase(this)!!.stonkDao())

        // hide text from tool bar
        // supportActionBar?.setDisplayShowTitleEnabled(false);

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