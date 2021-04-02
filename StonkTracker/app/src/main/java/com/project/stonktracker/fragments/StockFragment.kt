package com.project.stonktracker.fragments

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.project.stonktracker.*
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

    private lateinit var recyclerView: RecyclerView
    private lateinit var hist: List<PurchaseHistory>
    private lateinit var tickerURL: HashMap<String, List<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        container?.removeAllViews() // else previous fragment is visible in background
        _binding = StockFragmentBinding.inflate(inflater, container, false)

        val si = fragmentVM.getStockInfo()
        val cmpy = fragmentVM.getCompany()
        val inv = fragmentVM.getInvestment()

        stocksVM.fetchHistoryTicker(cmpy.ticker)

        binding.companyName = cmpy.name
        binding.companyTicker = cmpy.ticker
        binding.companySector = cmpy.sector

        val transToken = if (inv.roi > 0) "+" else "-"
        var roi = abs(inv.roi)
        var roi_percent = abs(inv.roipercent)

        binding.value = "$${String.format("%,.2f", inv.value * inv.shares)}"
        binding.roi = "$transToken$${String.format("%,.2f", roi)} ($transToken${String.format("%,.2f", roi_percent)}%)"

        // TODO make this work...
        if (inv.shares % 10 == 0.0) {
            binding.shares = inv.shares.toInt().toString()
        } else {
            binding.shares = inv.shares.toString()
        }

        // Load LOGO
        var webURL = stocksVM.getTickersAndURLs().value!!
        Glide.with(binding.imageViewLogo.context as Activity)
            .load("https://logo.clearbit.com/${webURL[cmpy.ticker]?.get(0)}")
            .into(binding.imageViewLogo)

        // onClick New Transaction
        binding.buttonNewTransaction.setOnClickListener {
            // After added transaction go to HISTORY
            var activity: AppCompatActivity = it.context as AppCompatActivity

            val fragmentTransaction = activity.supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment, TransactionFragment())
            prev_fragment = now_fragment
            now_fragment = FTracker.TRANSACTION
            // fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        // TODO replace with actual ABOUT
        binding.about = si.description
        // For using character length
        var green = ContextCompat.getColor(requireContext(), R.color.buy_000)
        var red = ContextCompat.getColor(requireContext(), R.color.sell_000)
        binding.showMoreTextAbout.setShowingLine(1)
        binding.showMoreTextAbout.setShowMoreColor(green)
        binding.showMoreTextAbout.setShowLessTextColor(red)

        // Transaction History
        // RecyclerView for showing portfolio data
        recyclerView = binding.recyclerViewHistoryTicker
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(view?.context, 1)

        stocksVM.getHistoryTicker().observe(viewLifecycleOwner, {history ->
            hist = history
            if(this::tickerURL.isInitialized) {
                recyclerView.adapter = HistoryFragmentAdapter(ArrayList(hist), tickerURL)
            }
        })

        stocksVM.getTickersAndURLs().observe(viewLifecycleOwner, {tickerAndURL ->
            tickerURL = tickerAndURL
            if(this::hist.isInitialized) {
                recyclerView.adapter = HistoryFragmentAdapter(ArrayList(hist), tickerURL)
            }
        })

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}