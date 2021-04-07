package com.project.stonktracker

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.stonktracker.databinding.HistoryFragmentBinding
import com.project.stonktracker.viewmodels.StocksVM

class HistoryFragment : Fragment() {

    companion object {
        fun newInstance() = HistoryFragment()
    }

    //private val historyVM: HistoryVM by activityViewModels()
    //private val portfolioVM: PortfolioVM by activityViewModels()
    private val stocksVM: StocksVM by activityViewModels()

    private lateinit var recyclerView: RecyclerView

    // binding
    private var _binding: HistoryFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var hist: List<PurchaseHistory>
    private lateinit var tickerURL: HashMap<String, List<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        stocksVM.init()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        container?.removeAllViews() // else previous fragment is visible in background
        _binding = HistoryFragmentBinding.inflate(inflater, container, false)

        // RecyclerView for showing portfolio data
        recyclerView = binding.recyclerViewHistory
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(view?.context, 1)

        stocksVM.getHistory().observe(viewLifecycleOwner, {history ->
            hist = history
            // tickerURL = portfolioVM.getTickersAndURLs().value!!
            Log.i("fragment_observe", "historyVM in HistoryFragment")
            if(this::tickerURL.isInitialized) {
                recyclerView.adapter = HistoryFragmentAdapter(ArrayList(hist), tickerURL)
            }
        })

        stocksVM.getTickersAndURLs().observe(viewLifecycleOwner, {tickerAndURL ->
            tickerURL = tickerAndURL
            // hist = historyVM.getHistory().value!!
            Log.i("fragment_observe", "portfolioVM in HistoryFragment")
            if(this::hist.isInitialized) {
                recyclerView.adapter = HistoryFragmentAdapter(ArrayList(hist), tickerURL)
            }
        })


        // Handle api errors nicely
        stocksVM.successPolygon.observe(viewLifecycleOwner, {status ->
            Log.i("history", status.toString())
            when(status) {
                2 -> Toast.makeText(activity, "Something went wrong, please try again in a couple of seconds", Toast.LENGTH_SHORT).show()
                3 -> Toast.makeText(activity, "Sorry, but we cannot get data for that stock...", Toast.LENGTH_SHORT).show()
            }
            if(status > 1) {stocksVM.successPolygon.value = 0}
        })
        stocksVM.successMarketstack.observe(viewLifecycleOwner, {status ->
            Log.i("history", status.toString())
            when(status) {
                2 -> Toast.makeText(activity, "Something went wrong, please try again in a couple of seconds", Toast.LENGTH_SHORT).show()
                3 -> Toast.makeText(activity, "Please check your internet connection", Toast.LENGTH_SHORT).show()
            }
            if(status > 1) {stocksVM.successMarketstack.value = 0}
        })

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}