package com.project.stonktracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment

import com.google.android.material.bottomnavigation.BottomNavigationView
import com.project.stonktracker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var nav: BottomNavigationView
    private lateinit var selectedFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        nav = binding.bottomNavigationView
        nav.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> selectedFragment = MainFragment()
                R.id.nav_histroy -> selectedFragment = HistoryFragment()
                R.id.nav_search -> selectedFragment = MainFragment() //TODO make it proper
            }

            Log.i("fragment_log", "$selectedFragment")

            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment, selectedFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()

            return@setOnNavigationItemSelectedListener true
        }
    }

}