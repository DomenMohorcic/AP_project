package com.project.stonktracker

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.stonktracker.databinding.HistoryFragmentBinding

class HistoryFragment : Fragment() {

    companion object {
        fun newInstance() = HistoryFragment()
    }

    private lateinit var viewModel: HistoryViewModel
    private lateinit var recyclerView: RecyclerView

    // binding
    private var _binding: HistoryFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        container?.removeAllViews() // else previous fragment is visible in background
        _binding = HistoryFragmentBinding.inflate(inflater, container, false)

        // RecyclerView for showing portfolio data
        recyclerView = binding.recyclerViewHistory
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(view?.context, 1)
        recyclerView.adapter = HistoryAdapter(arrayListOf(
            PurchaseHistory(0, "AAPL", "Oct 31st 2020", 80.0, 120.86, 0.0, true),
            PurchaseHistory(0, "AAPL", "Oct 30th 2020", 32.0, 120.98, 0.0, true),
            PurchaseHistory(0, "AAPL", "Oct 29th 2020", 20.0, 180.86, 0.0, true),
            PurchaseHistory(0, "AAPL", "Oct 23rd 2020", 45.0, 150.86, 0.0, true),
            PurchaseHistory(0, "AAPL", "Aug 1st 2020", 10.0, 115.86, 0.0, true),
            PurchaseHistory(0, "AAPL", "Jun 15th 2020", 15.0, 110.86, 0.0, true),
            PurchaseHistory(0, "AAPL", "Jan 31st 2020", 10.0, 98.86, 0.0, true),
            PurchaseHistory(0, "AAPL", "Oct 25th 2018", 2.0, 70.86, 0.0, true),
            PurchaseHistory(0, "AAPL", "Mar 18th 2017", 5.0, 80.86, 0.0, true),
            PurchaseHistory(0, "AAPL", "Oct 2nd 2005", 1.0, 70.86, 0.0, true)

        ))

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(HistoryViewModel::class.java)
        // TODO: Use the ViewModel
    }
}