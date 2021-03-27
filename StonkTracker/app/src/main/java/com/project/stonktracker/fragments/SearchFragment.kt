package com.project.stonktracker

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.*
import com.project.stonktracker.databinding.SearchFragmentBinding
import org.json.JSONArray

class SearchFragment : Fragment() {
    companion object {
        fun newInstance() = SearchFragment()
    }

    // binding
    private var _binding: SearchFragmentBinding? = null
    private val binding get() = _binding!!

    // for Volley requests
    private lateinit var queue: RequestQueue
    private var searchResults: ArrayList<String> = ArrayList()
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        container?.removeAllViews() // else previous fragment is visible in background
        _binding = SearchFragmentBinding.inflate(inflater, container, false)

        queue = Volley.newRequestQueue(this.context)

        // makes API call - https://www.alphavantage.co/documentation/#symbolsearch
        binding.button.setOnClickListener {
            val q = binding.searchBox.text
            val url = "https://www.alphavantage.co/query?function=SYMBOL_SEARCH&keywords=${q}&apikey=RTUYSN1G309FMPH2"
            queue.add(JsonObjectRequest(Request.Method.GET, url, null,
                { response ->
                    if(response.has("bestMatches")) {
                        val arr = response.getJSONArray("bestMatches")
                        var str = ""
                        for(i in 0 until arr.length()) {
                            var obj = arr.getJSONObject(i)
                            var textToAdd = "${i}: ${obj.getString("1. symbol")} - ${obj.getString("2. name")} (${obj.getString("4. region")})\n"
                            str += textToAdd

                            searchResults.add("${obj.getString("1. symbol")} - ${obj.getString("2. name")}")
                        }
                        // binding.textView.text = str
                        // RecyclerView for showing portfolio data
                        recyclerView = binding.recyclerViewSearch
                        recyclerView.setHasFixedSize(true)
                        recyclerView.layoutManager = GridLayoutManager(view?.context, 1)
                        recyclerView.adapter = SearchFragmentAdapter(searchResults)

                        searchResults = ArrayList()
                    }
                },
                { error -> Log.e("request_error", "this b*tch empty") }))
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // TODO: Use the ViewModel
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}