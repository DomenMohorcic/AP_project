package com.project.stonktracker

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.*
import com.project.stonktracker.databinding.SearchFragmentBinding
import com.project.stonktracker.viewmodels.Company
import com.project.stonktracker.viewmodels.FragmentVM
import org.json.JSONArray

class SearchFragment : Fragment() {
    companion object {
        fun newInstance() = SearchFragment()
    }

    // binding
    private var _binding: SearchFragmentBinding? = null
    private val binding get() = _binding!!

    private var searchResults: ArrayList<Company> = ArrayList()
    private lateinit var recyclerView: RecyclerView
    private val fragmentVM: FragmentVM by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        container?.removeAllViews() // else previous fragment is visible in background
        _binding = SearchFragmentBinding.inflate(inflater, container, false)

        recyclerView = binding.recyclerViewSearch
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(view?.context, 1)

        // makes API call - https://www.alphavantage.co/documentation/#symbolsearch
        binding.button.setOnClickListener {
            val q = binding.searchBox.text
            if(q.isNotEmpty()) {
                val url = "https://www.alphavantage.co/query?function=SYMBOL_SEARCH&keywords=${q}&apikey=${KEY_VANTAGE}"
                queue?.add(JsonObjectRequest(Request.Method.GET, url, null,
                    { response ->
                        if(response.has("bestMatches")) {
                            Log.i("api_search", "Response OK")
                            val arr = response.getJSONArray("bestMatches")
                            for(i in 0 until arr.length()) {
                                var obj = arr.getJSONObject(i)
                                searchResults.add(Company(obj.getString("1. symbol"), obj.getString("2. name"), "none"))
                            }
                            // RecyclerView for showing portfolio data
                            recyclerView.adapter = SearchFragmentAdapter(searchResults, fragmentVM)

                            searchResults = ArrayList()
                        } else {
                            Log.e("api_search", "Response OK but ERROR")
                            Toast.makeText(activity, "Something went wrong, please try again in a couple of seconds", Toast.LENGTH_SHORT).show()
                        }
                    },
                    { error ->
                        Log.e("api_search", error.toString())
                        Toast.makeText(activity, "Please check your internet connection", Toast.LENGTH_SHORT).show()
                    }))
            } else {
                Log.i("api_search", "Empty input text")
                Toast.makeText(activity, "Cannot search empty text...", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}