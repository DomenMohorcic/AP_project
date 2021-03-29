package com.project.stonktracker.fragments

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.project.stonktracker.R
import com.project.stonktracker.databinding.StockFragmentBinding
import com.project.stonktracker.viewmodels.FragmentVM
import com.project.stonktracker.viewmodels.StocksVM
import kotlin.math.abs

class StockFragment: Fragment() {

    private val stocksVM: StocksVM by activityViewModels()
    private val fragmentVM: FragmentVM by activityViewModels()

    // binding
    private var _binding: StockFragmentBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // stocksVM.init()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        container?.removeAllViews() // else previous fragment is visible in background
        _binding = StockFragmentBinding.inflate(inflater, container, false)

        val cmpy = fragmentVM.getCompany()
        val inv = fragmentVM.getInvestment()

        binding.companyName = cmpy.name
        binding.companyTicker = cmpy.ticker
        binding.companySector = cmpy.sector

        val transToken = if (inv.roi > 0) "+" else "-"
        var roi = abs(inv.roi)
        var roi_percent = abs(inv.roipercent)

        binding.value = "$${String.format("%,.2f", inv.value * inv.shares)}"
        binding.roi = "$transToken$${String.format("%,.2f", roi)} ($transToken${String.format("%,.2f", roi_percent)}%)"

        if (inv.shares % 10 == 0.0) {
            binding.shares = inv.shares.toInt().toString()
        } else {
            binding.shares = inv.shares.toString()
        }

        var webURL = stocksVM.getTickersAndURLs().value!!
        Glide.with(binding.imageViewLogo.context as Activity)
            .load("https://logo.clearbit.com/${webURL[cmpy.ticker]?.get(0)}")
            .into(binding.imageViewLogo)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}