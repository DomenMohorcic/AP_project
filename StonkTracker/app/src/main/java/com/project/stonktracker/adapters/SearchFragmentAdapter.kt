package com.project.stonktracker

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.internal.ContextUtils.getActivity
import com.project.stonktracker.viewmodels.Company
import com.project.stonktracker.viewmodels.FragmentVM
import org.w3c.dom.Text

class SearchFragmentAdapter(private val searchResults: ArrayList<Company>, private val fragmentVM: FragmentVM) : RecyclerView.Adapter<SearchFragmentAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.textName)
        val addButton: Button = view.findViewById(R.id.buttonAdd)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val result = searchResults[position]
        holder.name.text = "${result.ticker} - ${result.name}"

        holder.addButton.setOnClickListener {
            var activity: AppCompatActivity = it.context as AppCompatActivity

            // save choice to FragmentVM
            fragmentVM.setCompany(result)

            val fragmentTransaction = activity.supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment, TransactionFragment())
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
    }

    override fun getItemCount(): Int {
        return searchResults.size
    }
}