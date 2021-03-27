package com.project.stonktracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HistoryFragmentAdapter(private val historyInfo: ArrayList<PurchaseHistory>) :
    RecyclerView.Adapter<HistoryFragmentAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val viewTicker: TextView = view.findViewById(R.id.typeAndTicker)
        val viewShares: TextView = view.findViewById(R.id.sharesAndPrice)
        val viewDate: TextView = view.findViewById(R.id.dateText)
        val logo: ImageView = view.findViewById(R.id.imageViewLogo)
        val total: TextView = view.findViewById(R.id.totalPrice)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.history_view, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // get element from dataset at this position and replace the
        // content of the view with that element

        // TODO get info from ViewModel!
        val ph: PurchaseHistory = historyInfo[position]

        // TODO order before display! (can do it via DB call...)

        val price = 120.65 // TODO API call

        // TODO get stock info from TICKER!
        // val si: StockInfo = siGetTicker(ph.ticker)

        val transaction = if (ph.buy) "Buy" else "Sell"
        val transToken = if (ph.buy) "+" else "-"

        /*Glide.with(holder.logo.context as Activity)
            .load("https://logo.clearbit.com/${si.webURL}")
            .into(holder.logo)*/
        holder.viewTicker.text = "$transaction ${ph.ticker}"
        holder.viewShares.text = "${ph.quantity} shares @ ${ph.price}€"
        holder.viewDate.text = ph.date
        holder.total.text = "$transToken${String.format("%.2f", (ph.quantity * ph.price))}€"
    }

    override fun getItemCount() = historyInfo.size

}