package com.project.stonktracker

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.stonktracker.databinding.PortfolioFragmentBinding
import com.project.stonktracker.databinding.TransactionFragmentBinding
import com.project.stonktracker.viewmodels.Company
import com.project.stonktracker.viewmodels.FragmentVM

class TransactionFragment : Fragment() {
    companion object {
        fun newInstance() = TransactionFragment()
    }

    private val fragmentVM: FragmentVM by activityViewModels()
    private val historyVM: HistoryVM by activityViewModels()

    private lateinit var company: Company
    private lateinit var dateText: String
    private var buyStatus: Boolean = true

    // binding
    private var _binding: TransactionFragmentBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        container?.removeAllViews() // else previous fragment is visible in background
        _binding = TransactionFragmentBinding.inflate(inflater, container, false)

        company = fragmentVM.getCompany()
        binding.stockName = "${company?.ticker} - ${company?.name}"

        // set defualt dateText that will be today's day..
        dateText = binding.editDate.text.toString()

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.editDate.setOnClickListener {
            // TODO
            // dateText = ...
        }

        binding.buttonSave.setOnClickListener {
            var shares = if (binding.editShares.text.toString() != "") binding.editShares.text.toString().toDouble() else 0.0
            var price = if (binding.editPrice.text.toString() != "") binding.editPrice.text.toString().toDouble() else 0.0
            var fees = if (binding.editFees.text.toString() != "") binding.editFees.text.toString().toDouble() else 0.0

            var ph: PurchaseHistory = PurchaseHistory(0,
                company.ticker,
                dateText,
                shares,
                price,
                fees,
                buyStatus
            )
            historyVM.insert(ph)

            // After added transaction go to HISTORY
            var activity: AppCompatActivity = it.context as AppCompatActivity

            val fragmentTransaction = activity.supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment, HistoryFragment())
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        binding.buttonBuy.setOnClickListener {
            buyStatus = true
        }

        binding.buttonSell.setOnClickListener {
            buyStatus = false
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

