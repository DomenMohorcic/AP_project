package com.project.stonktracker.fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.text.createTextLayoutResult
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.IMarker
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.project.stonktracker.R
import com.project.stonktracker.StockInfo
import com.project.stonktracker.databinding.StatsFragmentBinding
import com.project.stonktracker.viewmodels.StocksVM
import java.text.DecimalFormat

class StatsFragment : Fragment() {
    companion object {
        fun newInstance() = StatsFragment()
    }

    //private val fragmentVM: FragmentVM by activityViewModels()
    private val stocksVM: StocksVM by activityViewModels()

    private lateinit var pieChart: PieChart
    private lateinit var stocks: List<StockInfo>

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

        pieChart = binding.valuePieChart
        pieChart.description.text = ""
        //valuePieChart.holeRadius = 50f
        //valuePieChart.transparentCircleRadius = 55f
        pieChart.legend.isEnabled = false

        stocksVM.getStocks().observe(viewLifecycleOwner, { stocks ->
            this.stocks = stocks
            pieStockValues()
        })

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val buttonGreen = resources.getDrawable(R.drawable.smooth_background_buy)
        val buttonNeutral = resources.getDrawable(R.drawable.smooth_background_neutral)

        val green = ContextCompat.getColor(requireContext(), R.color.buy_000)
        val neutral = ContextCompat.getColor(requireContext(), R.color.neutral_000)

        binding.buttonShowStockValue.setOnClickListener {
            pieStockValues()

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
            pieStockGains()

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
            pieSectorValues()

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
            pieSectorGains()

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

    private fun pieStockValues() {
        val entries = ArrayList<PieEntry>()
        var value = 0.0
        val textColors = ArrayList<Int>()

        for(stock in stocks) {
            entries.add(PieEntry((stock.shares*stock.last_close).toFloat(), stock.ticker))
            value += stock.shares*stock.last_close
            textColors.add(R.color.black)
        }

        pieDraw(entries, value, textColors)
    }

    private fun pieStockGains() {
        val entries = ArrayList<PieEntry>()
        var value = 0.0
        val textColors = ArrayList<Int>()

        for(stock in stocks) {
            entries.add(PieEntry((stock.shares*stock.last_close-stock.shares*stock.avg_price).toFloat(), stock.ticker))
            value += stock.shares*stock.last_close-stock.shares*stock.avg_price
            textColors.add(R.color.black)
        }

        pieDraw(entries, value, textColors)
    }

    private fun pieSectorValues() {
        val entries = ArrayList<PieEntry>()
        val sectors = HashMap<String, Float>().withDefault { 0.0f }
        var value = 0.0
        val textColors = ArrayList<Int>()

        for(stock in stocks) {
            var sec: String = if(stock.sector.isBlank()) {"Unknown"} else {stock.sector}
            if(sectors.containsKey(sec)) {
                var a = sectors[sec]!!
                a += (stock.shares*stock.last_close).toFloat()
                sectors[sec] = a
            } else {sectors[sec] = (stock.shares*stock.last_close).toFloat()}
            value += stock.shares*stock.last_close
        }
        for((key, value) in sectors) {
            entries.add(PieEntry(value, key))
            textColors.add(R.color.black)
        }

        pieDraw(entries, value, textColors)
    }

    private fun pieSectorGains() {
        val entries = ArrayList<PieEntry>()
        val sectors = HashMap<String, Float>().withDefault { 0.0f }
        var value = 0.0
        val textColors = ArrayList<Int>()

        for(stock in stocks) {
            var sec: String = if(stock.sector.isBlank()) {"Unknown"} else {stock.sector}
            if(sectors.containsKey(sec)) {
                var a = sectors[sec]!!
                a += (stock.shares*stock.last_close).toFloat()
                sectors[sec] = a
            } else {sectors[sec] = (stock.shares*stock.last_close-stock.shares*stock.avg_price).toFloat()}
            value += stock.shares*stock.last_close-stock.shares*stock.avg_price
        }
        for((key, value) in sectors) {
            entries.add(PieEntry(value, key))
            textColors.add(R.color.black)
        }

        pieDraw(entries, value, textColors)
    }

    private fun pieDraw(entries: List<PieEntry>, value: Double, textColors: List<Int>) {
        val pieDataSet = PieDataSet(entries, "")
        pieDataSet.colors = getRainbowColors(stocks.size)
        pieDataSet.valueFormatter = MyValueFormatter()
        val pieData = PieData(pieDataSet)
        pieData.setValueTextColors(textColors)
        pieChart.data = pieData
        pieChart.centerText = "$${String.format("%,.2f", value)}"
        pieChart.invalidate()
    }

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