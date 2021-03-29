package com.project.stonktracker

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.stonktracker.databinding.PortfolioFragmentBinding
import com.project.stonktracker.viewmodels.FragmentVM
import com.project.stonktracker.viewmodels.StocksVM

class PortfolioFragment : Fragment() {
    companion object {
        fun newInstance() = PortfolioFragment()
    }

    //private val portfolioVM: PortfolioVM by activityViewModels()
    private val stocksVM: StocksVM by activityViewModels()
    private val fragmentVM: FragmentVM by activityViewModels()

    private lateinit var recyclerView: RecyclerView

    // binding
    private var _binding: PortfolioFragmentBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        stocksVM.init()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        container?.removeAllViews() // else previous fragment is visible in background
        _binding = PortfolioFragmentBinding.inflate(inflater, container, false)

        // TODO Calculate/Get from database
        binding.portfolioValue = "1,865.65€"
        binding.portfolioGains = "+250.15€ (+15.85%)"

        // RecyclerView for showing portfolio data
        recyclerView = binding.recyclerViewMain
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(view?.context, 1)

        // Observe the stock data in portfolioVM
        stocksVM.getStocks().observe(viewLifecycleOwner, {stocks ->
            stocksVM.updateCloses()
            Log.i("fragment_observe", "portfolioVM in PortfolioFragment")
            recyclerView.adapter = PortfolioFragmentAdapter(ArrayList(stocks), fragmentVM)
        })

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

