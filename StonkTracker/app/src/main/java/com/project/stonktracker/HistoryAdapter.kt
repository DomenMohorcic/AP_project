package com.project.stonktracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar

class HistoryAdapter(private val dataSet: ArrayList<PurchaseHistory>) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val viewTicker: TextView
        val viewShares: TextView
        val viewDate: TextView
        val logo: ImageView
        val total: TextView

        init {
            viewTicker = view.findViewById(R.id.typeAndTicker)
            viewShares = view.findViewById(R.id.typeAndTicker)
            viewDate = view.findViewById(R.id.typeAndTicker)
            logo = view.findViewById(R.id.imageViewLogo)
            total = view.findViewById(R.id.totalPrice)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.history_view, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // get element from dataset at this position and replace the
        // content of the view with that element

        // TODO get info from ViewModel!

    }

    override fun getItemCount() = dataSet.size

}