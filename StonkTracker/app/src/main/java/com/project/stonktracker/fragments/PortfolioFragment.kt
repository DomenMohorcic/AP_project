package com.project.stonktracker

import android.animation.ValueAnimator
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.View.VISIBLE
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.project.stonktracker.databinding.PortfolioFragmentBinding
import com.project.stonktracker.viewmodels.FragmentVM
import com.project.stonktracker.viewmodels.StocksVM
import kotlin.math.abs

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

        // RecyclerView for showing portfolio data
        recyclerView = binding.recyclerViewMain
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(view?.context, 1)

        // Observe the stock data in portfolioVM
        stocksVM.getStocks().observe(viewLifecycleOwner, {stocks ->
            Log.i("fragment_observe", "portfolioVM in PortfolioFragment")
            recyclerView.adapter = PortfolioFragmentAdapter(ArrayList(stocks), fragmentVM)

            var total_val = 0.0
            var total_gains = 0.0
            var total_paid = 0.0

            for (stock in stocks) {
                var current_val = stock.shares * stock.last_close
                var current_paid = stock.shares * stock.avg_price
                total_val += current_val
                total_gains += current_val - current_paid
                total_paid += current_paid
            }

            val transToken = if (total_gains > 0) "+" else "-"
            var total_gains_temp = abs(total_gains)

            // binding.portfolioValue = "$${String.format("%,.2f", total_val)}"
            binding.portfolioGains = "$transToken$${String.format("%,.2f", total_gains_temp)} ($transToken${String.format("%,.2f", total_gains_temp / total_paid * 100)}%)"

            // add animated portfolio value
            // TODO dynamic changes... not only from 0
            // TODO if animation is running and you click somewhere app crashes!! animator.end()??
            var animator: ValueAnimator = ValueAnimator.ofFloat(0.0.toFloat(), total_val.toFloat())
            animator.setDuration(1500)
            animator.addUpdateListener {
                binding.portfolioValue = "$${String.format("%,.2f", animator.getAnimatedValue())}"
            }
            animator.start()

            // get proper color for change percent value
            var green = ContextCompat.getColor(requireContext(), R.color.buy_000)
            var red = ContextCompat.getColor(requireContext(), R.color.sell_000)
            if (total_gains >= 0.0) {
                binding.textViewPL.setTextColor(green)
            } else {
                binding.textViewPL.setTextColor(red)
            }
        })

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

