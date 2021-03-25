package com.project.stonktracker

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class PortfolioFragmentAdapter(private val stockData: ArrayList<StockInfo>) : RecyclerView.Adapter<PortfolioFragmentAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val logoImage: ImageView = view.findViewById(R.id.imageViewLogo)
        val nameText: TextView = view.findViewById(R.id.nameText)
        val sharesText: TextView = view.findViewById(R.id.totalSharesCurrentPrice)
        // val priceText: TextView = view.findViewById(R.id.totalSharesCurrentPrice)
        val valueText: TextView = view.findViewById(R.id.totalValue)
        val changeText: TextView = view.findViewById(R.id.sharesPL)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.portfolio_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val si: StockInfo = stockData[position]

        val price = 450 // TODO API call

        Glide.with(holder.logoImage.context as Activity)
            .load("https://logo.clearbit.com/${si.webURL}")
            .into(holder.logoImage)
        holder.nameText.text = si.full_name
        holder.sharesText.text = si.shares.toString()
        // holder.priceText.text = price.toString()
        holder.valueText.text = (price * si.shares).toString()
        holder.changeText.text = (price * si.shares - si.shares).toString() // * price bought
    }

    override fun getItemCount(): Int {
        return stockData.size
    }
}