package com.project.stonktracker

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
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
                    val arr = response.getJSONArray("bestMatches")
                    var str = ""
                    for(i in 0 until arr.length()) {
                        var obj = arr.getJSONObject(i)
                        str += "${i}: ${obj.getString("1. symbol")} - ${obj.getString("2. name")} (${obj.getString("4. region")})\n"
                    }
                    binding.textView.text = str
                },
                { error -> binding.textView.text = error.toString() }))
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // TODO: Use the ViewModel
    }
}