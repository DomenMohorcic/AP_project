package com.project.stonktracker.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.project.stonktracker.R
import com.project.stonktracker.TransactionFragment
import com.project.stonktracker.databinding.StatsFragmentBinding
import com.project.stonktracker.viewmodels.Company
import com.project.stonktracker.viewmodels.FragmentVM
import com.project.stonktracker.viewmodels.StocksVM
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class StatsFragment : Fragment() {

    companion object {
        fun newInstance() = StatsFragment()
    }

    //private val fragmentVM: FragmentVM by activityViewModels()
    private val stocksVM: StocksVM by activityViewModels()

    private lateinit var valuePieChart: PieChart
    private var showStatus: Int = 0

    // binding
    private var _binding: StatsFragmentBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        container?.removeAllViews() // else previous fragment is visible in background
        _binding = StatsFragmentBinding.inflate(inflater, container, false)

        valuePieChart = binding.valuePieChart
        valuePieChart.description.text = ""
        //valuePieChart.holeRadius = 50f
        //valuePieChart.transparentCircleRadius = 55f
        valuePieChart.legend.isEnabled = false

        stocksVM.getStocks().observe(viewLifecycleOwner, { stocks ->
            val entries = ArrayList<PieEntry>()
            var value = 0.0

            /* when (showStatus) {
                // TODO ...
            } */

            for(stock in stocks) {
                entries.add(PieEntry((stock.shares*stock.last_close).toFloat(), stock.ticker))
                value += stock.shares*stock.last_close
            }

            Log.i("pie", value.toString())
            val pieDataSet = PieDataSet(entries, "")
            pieDataSet.colors = getRainbowColors(stocks.size) //ColorTemplate.COLORFUL_COLORS.toCollection(ArrayList())
            pieDataSet.valueFormatter = MyValueFormatter()
            val pieData = PieData(pieDataSet)
            valuePieChart.data = pieData
            valuePieChart.centerText = "$${String.format("%,.2f", value)}"
            valuePieChart.invalidate()
        })

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        var buttonGreen = resources.getDrawable(R.drawable.smooth_background_buy)
        var buttonNeutral = resources.getDrawable(R.drawable.smooth_background_neutral)

        var green = ContextCompat.getColor(requireContext(), R.color.buy_000)
        var neutral = ContextCompat.getColor(requireContext(), R.color.neutral_000)

        binding.buttonShowStockValue.setOnClickListener {
            showStatus = 0

            binding.buttonShowStockValue.setBackgroundDrawable(buttonGreen)
            binding.buttonShowStockGains.setBackgroundDrawable(buttonNeutral)
            binding.buttonShowSectorValue.setBackgroundDrawable(buttonNeutral)
            binding.buttonShowSectorGains.setBackgroundDrawable(buttonNeutral)

            binding.buttonShowStockValue.setTextColor(green)
            binding.buttonShowStockGains.setTextColor(neutral)
            binding.buttonShowSectorValue.setTextColor(neutral)
            binding.buttonShowSectorGains.setTextColor(neutral)
        }

        binding.buttonShowStockGains.setOnClickListener {
            showStatus = 1

            binding.buttonShowStockValue.setBackgroundDrawable(buttonNeutral)
            binding.buttonShowStockGains.setBackgroundDrawable(buttonGreen)
            binding.buttonShowSectorValue.setBackgroundDrawable(buttonNeutral)
            binding.buttonShowSectorGains.setBackgroundDrawable(buttonNeutral)

            binding.buttonShowStockValue.setTextColor(neutral)
            binding.buttonShowStockGains.setTextColor(green)
            binding.buttonShowSectorValue.setTextColor(neutral)
            binding.buttonShowSectorGains.setTextColor(neutral)
        }

        binding.buttonShowSectorValue.setOnClickListener {
            showStatus = 2

            binding.buttonShowStockValue.setBackgroundDrawable(buttonNeutral)
            binding.buttonShowStockGains.setBackgroundDrawable(buttonNeutral)
            binding.buttonShowSectorValue.setBackgroundDrawable(buttonGreen)
            binding.buttonShowSectorGains.setBackgroundDrawable(buttonNeutral)

            binding.buttonShowStockGains.setTextColor(neutral)
            binding.buttonShowStockValue.setTextColor(neutral)
            binding.buttonShowSectorValue.setTextColor(green)
            binding.buttonShowSectorGains.setTextColor(neutral)
        }

        binding.buttonShowSectorGains.setOnClickListener {
            showStatus = 3

            binding.buttonShowStockValue.setBackgroundDrawable(buttonNeutral)
            binding.buttonShowStockGains.setBackgroundDrawable(buttonNeutral)
            binding.buttonShowSectorValue.setBackgroundDrawable(buttonNeutral)
            binding.buttonShowSectorGains.setBackgroundDrawable(buttonGreen)

            binding.buttonShowStockGains.setTextColor(neutral)
            binding.buttonShowStockValue.setTextColor(neutral)
            binding.buttonShowSectorValue.setTextColor(neutral)
            binding.buttonShowSectorGains.setTextColor(green)
        }
    }

    // TODO make it to create rainbow
    private fun getRainbowColors(number: Int): ArrayList<Int> {
        var c = ArrayList<Int>()
        for(i in 0 until number) {
            c.add(Color.HSVToColor(floatArrayOf((360/number*i).toFloat(), 0.75f, 0.75f)))
        }
        return c
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}

class MyValueFormatter : ValueFormatter() {
    private val format = DecimalFormat("###,##0.0")

    override fun getPieLabel(value: Float, pieEntry: PieEntry?): String {
        return "$${format.format(value)}"
    }
}