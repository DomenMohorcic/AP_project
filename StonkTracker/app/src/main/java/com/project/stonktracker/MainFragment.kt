package com.project.stonktracker

import android.app.Activity
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.stonktracker.databinding.MainFragmentBinding
import java.util.*
import kotlin.collections.ArrayList

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var recyclerView: RecyclerView

    private var _binding: MainFragmentBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = MainFragmentBinding.inflate(inflater, container, false)

        val view: View = inflater.inflate(R.layout.main_fragment, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewMain)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(view.context, 1)
        recyclerView.adapter = MainFragmentAdapter(arrayListOf(
            StockInfo("T", "AT&T", "att.com", 20),
            StockInfo("APPL", "Apple Inc.", "apple.com", 50),
            StockInfo("STB", "Starbucks", "starbucks.com", 45)
        ))

        return view //binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        // TODO: Use the ViewModel

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class MainFragmentAdapter(private val stockData: ArrayList<StockInfo>) : RecyclerView.Adapter<MainFragmentAdapter.ViewHolder>() {

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
        val price = 450 //API klic
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