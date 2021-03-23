package com.project.stonktracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PortfolioAdapter(private val dataSet: ArrayList<PurchaseHistory>) :
    RecyclerView.Adapter<PortfolioAdapter.ViewHolder>() {

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val companyName: TextView
        val totalShares: TextView
        val totalValue: TextView
        val logo: ImageView
        val pl: TextView

        init {
            companyName = view.findViewById(R.id.nameText)
            totalShares = view.findViewById(R.id.totalSharesCurrentPrice)
            totalValue = view.findViewById(R.id.totalValue)
            logo = view.findViewById(R.id.imageViewLogo)
            pl = view.findViewById(R.id.textViewPL)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.portfolio_view, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // get element from dataset at this position and replace the
        // content of the view with that element

        // TODO get info from ViewModel!

    }

    override fun getItemCount() = dataSet.size

}