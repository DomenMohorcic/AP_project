package com.project.stonktracker

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.project.stonktracker.fragments.StockFragment
import com.project.stonktracker.viewmodels.Company
import com.project.stonktracker.viewmodels.FragmentVM
import com.project.stonktracker.viewmodels.Investment
import kotlin.math.abs

class PortfolioFragmentAdapter(private val stockData: ArrayList<StockInfo>, private val fragmentVM: FragmentVM) : RecyclerView.Adapter<PortfolioFragmentAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val logoImage: ImageView = view.findViewById(R.id.imageViewLogo)
        val nameText: TextView = view.findViewById(R.id.nameText)
        val sharesPriceText: TextView = view.findViewById(R.id.totalSharesCurrentPrice)
        val valueText: TextView = view.findViewById(R.id.totalValue)
        val changeText: TextView = view.findViewById(R.id.sharesPL)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.portfolio_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val si: StockInfo = stockData[position]

        Glide.with(holder.logoImage.context as Activity)
            .load("https://logo.clearbit.com/${si.webURL}")
            .into(holder.logoImage)
        holder.nameText.text = si.full_name
        holder.sharesPriceText.text = "${si.shares} shares @ $${String.format("%,.2f", si.last_close)}"

        // calculate and set returns

        var value: Double = si.last_close * si.shares
        holder.valueText.text = "$${String.format("%,.2f", value)}"

        var change: Double = value - si.avg_price*si.shares

        val transToken = if (change > 0) "+" else "-"
        var change_temp = abs(change)

        var change_percent: Double = (change_temp / (si.avg_price*si.shares)) * 100
        holder.changeText.text = "$transToken${String.format("%,.2f", change_percent)}%"

        // get proper color for change percent values
        var green = ContextCompat.getColor(holder.changeText.context, R.color.buy_000)
        var red = ContextCompat.getColor(holder.changeText.context, R.color.sell_000)
        if (change >= 0.0) {
            holder.changeText.setTextColor(green)
        } else {
            holder.changeText.setTextColor(red)
        }

        // onClick go to stock fragment for more info...
        val onStockClick = View.OnClickListener { view ->
            var activity: AppCompatActivity = view.context as AppCompatActivity

            // save choice to FragmentVM
            fragmentVM.setCompany(Company(si.ticker, si.full_name, si.sector))
            fragmentVM.setInvestment(Investment(si.ticker, si.shares, si.last_close, change, change_percent))

            // start fragment
            val fragmentTransaction = activity.supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment, StockFragment())
            // fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        // set listeners
        holder.nameText.setOnClickListener(onStockClick)
        holder.sharesPriceText.setOnClickListener(onStockClick)
        holder.valueText.setOnClickListener(onStockClick)
        holder.changeText.setOnClickListener(onStockClick)
        holder.logoImage.setOnClickListener(onStockClick)
    }

    override fun getItemCount(): Int {
        return stockData.size
    }

}