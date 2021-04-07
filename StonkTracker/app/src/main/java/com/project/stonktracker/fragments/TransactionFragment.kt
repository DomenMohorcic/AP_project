package com.project.stonktracker

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.project.stonktracker.databinding.TransactionFragmentBinding
import com.project.stonktracker.viewmodels.Company
import com.project.stonktracker.viewmodels.FragmentVM
import com.project.stonktracker.viewmodels.StocksVM
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

class TransactionFragment : Fragment() {
    companion object {
        fun newInstance() = TransactionFragment()
    }

    private val fragmentVM: FragmentVM by activityViewModels()
    private val stocksVM: StocksVM by activityViewModels()

    private lateinit var company: Company
    private lateinit var dateText: String
    private var g_day = 1
    private var g_month = 1
    private var g_year = 1970
    private var buyStatus: Boolean = true

    // binding
    private var _binding: TransactionFragmentBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        container?.removeAllViews() // else previous fragment is visible in background
        _binding = TransactionFragmentBinding.inflate(inflater, container, false)

        company = fragmentVM.getCompany()
        binding.stockName = "${company.ticker} - ${company.name}"

        // set defualt dateText that will be today's day..
        val currentDateTime = LocalDateTime.now()
        dateText = currentDateTime.format(DateTimeFormatter.BASIC_ISO_DATE)
        binding.editDate.setText(currentDateTime.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)))

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.editDate.setOnClickListener {
            val c_day: Int
            val c_month: Int
            val c_year: Int
            if(g_year == 1970) {
                val cldr: Calendar = Calendar.getInstance()
                c_day = cldr.get(Calendar.DAY_OF_MONTH)
                c_month = cldr.get(Calendar.MONTH)
                c_year = cldr.get(Calendar.YEAR)
            } else {
                c_day = g_day
                c_month = g_month-1
                c_year = g_year
            }
            // date picker dialog
            val picker = DatePickerDialog(requireContext(),
                { view, year, monthOfYear, dayOfMonth ->
                    dateText = year.toString()+"/"+(monthOfYear + 1)+"/"+dayOfMonth.toString()
                    Log.i("datepicker_info", dateText)
                    val formatter = SimpleDateFormat("yyyy/MM/dd")
                    val date = formatter.parse(dateText)
                    dateText = LocalDateTime.of(year, monthOfYear+1, dayOfMonth, 0, 0).format(DateTimeFormatter.BASIC_ISO_DATE)
                    Log.i("datepicker_info", dateText)
                    g_day = dayOfMonth
                    g_month = monthOfYear+1
                    g_year = year
                    // Log.i("date_info", SimpleDateFormat("dd MMM yyyy").format(date))
                    binding.editDate.setText(SimpleDateFormat("dd MMM yyyy").format(date))
                },
                c_year,
                c_month,
                c_day)
            picker.show()
        }

        binding.buttonSave.setOnClickListener {
            val shares = if(binding.editShares.text.toString() != "") binding.editShares.text.toString().toDouble() else 0.0
            val price = if(binding.editPrice.text.toString() != "") binding.editPrice.text.toString().toDouble() else 0.0
            val fees = if(binding.editFees.text.toString() != "") binding.editFees.text.toString().toDouble() else 0.0

            if ((shares <= 0.0) || (price <= 0.0) || (fees < 0.0)) {
              Toast.makeText(view?.context, "Please enter valid numbers for shares, price and fees!", Toast.LENGTH_SHORT).show()
            } else {
                val ph = PurchaseHistory(0, company.ticker, dateText, shares, price, fees, buyStatus)

                // add to database via MVVM
                Log.i("fragment_observe", "Saving transaction")
                stocksVM.phInsert(ph)
                // portfolioVM.updateStocks()

                // After added transaction go to HISTORY
                val activity: AppCompatActivity = it.context as AppCompatActivity

                val fragmentTransaction = activity.supportFragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.fragment, HistoryFragment())
                prev_fragment = now_fragment
                now_fragment = FTracker.HISTORY
                // fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()

                // check the right navigation item...
                var nav = getActivity()?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
                nav!!.menu.getItem(3).isChecked = true
            }
        }

        binding.buttonBuy.setOnClickListener {
            buyStatus = true

            var backNeutral = resources.getDrawable(R.drawable.smooth_background_neutral)
            var backBuy= resources.getDrawable(R.drawable.smooth_background_buy)

            binding.buttonBuy.setBackgroundDrawable(backBuy)
            binding.buttonSell.setBackgroundDrawable(backNeutral)
            binding.buttonSave.setBackgroundDrawable(backBuy)

            var green = ContextCompat.getColor(requireContext(), R.color.buy_000)
            var neutral = ContextCompat.getColor(requireContext(), R.color.neutral_000)

            binding.buttonBuy.setTextColor(green)
            binding.buttonSell.setTextColor(neutral)
            binding.buttonSave.setTextColor(green)
        }

        binding.buttonSell.setOnClickListener {
            buyStatus = false

            var backNeutral = resources.getDrawable(R.drawable.smooth_background_neutral)
            var backSell = resources.getDrawable(R.drawable.smooth_background_sell)

            binding.buttonBuy.setBackgroundDrawable(backNeutral)
            binding.buttonSell.setBackgroundDrawable(backSell)
            binding.buttonSave.setBackgroundDrawable(backSell)

            var red = ContextCompat.getColor(requireContext(), R.color.sell_000)
            var neutral = ContextCompat.getColor(requireContext(), R.color.neutral_000)

            binding.buttonBuy.setTextColor(neutral)
            binding.buttonSell.setTextColor(red)
            binding.buttonSave.setTextColor(red)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

