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
        binding.about = "Apple Inc. designs, manufactures, and markets smartphones, personal computers, tablets, wearables, and accessories worldwide. It also sells various related services. The company offers iPhone, a line of smartphones; Mac, a line of personal computers; iPad, a line of multi-purpose tablets; and wearables, home, and accessories comprising AirPods, Apple TV, Apple Watch, Beats products, HomePod, iPod touch, and other Apple-branded and third-party accessories. It also provides AppleCare support services; cloud services store services; and operates various platforms, including the App Store, that allow customers to discover and download applications and digital content, such as books, music, video, games, and podcasts. In addition, the company offers various services, such as Apple Arcade, a game subscription service; Apple Music, which offers users a curated listening experience with on-demand radio stations; Apple News+, a subscription news and magazine service; Apple TV+, which offers exclusive original content; Apple Card, a co-branded credit card; and Apple Pay, a cashless payment service, as well as licenses its intellectual property. The company serves consumers, and small and mid-sized businesses; and the education, enterprise, and government markets. It sells and delivers third-party applications for its products through the App Store. The company also sells its products through its retail and online stores, and direct sales force; and third-party cellular network carriers, wholesalers, retailers, and resellers. Apple Inc. was founded in 1977 and is headquartered in Cupertino, California."
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