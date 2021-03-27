package com.project.stonktracker

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.stonktracker.databinding.PortfolioFragmentBinding

class PortfolioFragment : Fragment() {
    companion object {
        fun newInstance() = PortfolioFragment()
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var recyclerView: RecyclerView

    // binding
    private var _binding: PortfolioFragmentBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

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
        recyclerView.adapter = PortfolioFragmentAdapter(arrayListOf(
            StockInfo("T", "AT&T", "att.com", 20),
            StockInfo("AAPL", "Apple Inc.", "apple.com", 50),
            StockInfo("STB", "Starbucks", "starbucks.com", 45),
            StockInfo("T", "AT&T", "att.com", 20),
            StockInfo("AAPL", "Apple Inc.", "apple.com", 50),
            StockInfo("STB", "Starbucks", "starbucks.com", 45),
            StockInfo("T", "AT&T", "att.com", 20),
            StockInfo("AAPL", "Apple Inc.", "apple.com", 50),
            StockInfo("STB", "Starbucks", "starbucks.com", 45),
            StockInfo("T", "AT&T", "att.com", 20),
            StockInfo("AAPL", "Apple Inc.", "apple.com", 50),
            StockInfo("STB", "Starbucks", "starbucks.com", 45)
        ))

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        // TODO: Use the ViewModel

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

