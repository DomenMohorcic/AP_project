package com.project.stonktracker.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.project.stonktracker.R
import com.project.stonktracker.TransactionFragment
import com.project.stonktracker.databinding.FragmentStatsBinding
import com.project.stonktracker.databinding.TransactionFragmentBinding
import com.project.stonktracker.viewmodels.Company
import com.project.stonktracker.viewmodels.FragmentVM
import com.project.stonktracker.viewmodels.StocksVM
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class StatsFragment : Fragment() {
    // TODO: add shit

    companion object {
        fun newInstance() = StatsFragment()
    }

    //private val fragmentVM: FragmentVM by activityViewModels()
    //private val stocksVM: StocksVM by activityViewModels()

    // binding
    private var _binding: FragmentStatsBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        container?.removeAllViews() // else previous fragment is visible in background
        _binding = FragmentStatsBinding.inflate(inflater, container, false)

        return binding.root
    }
}