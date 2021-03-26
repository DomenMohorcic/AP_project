package com.project.stonktracker

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.stonktracker.databinding.PortfolioFragmentBinding
import com.project.stonktracker.databinding.TransactionFragmentBinding

class TransactionFragment : Fragment() {
    companion object {
        fun newInstance() = TransactionFragment()
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var recyclerView: RecyclerView

    // binding
    private var _binding: TransactionFragmentBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        container?.removeAllViews() // else previous fragment is visible in background
        _binding = TransactionFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        // TODO: Use the ViewModel

        binding.editDate.setOnClickListener {

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

