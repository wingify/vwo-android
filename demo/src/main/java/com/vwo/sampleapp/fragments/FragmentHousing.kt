package com.vwo.sampleapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.vwo.sampleapp.R
import com.vwo.sampleapp.adapters.HousingRecylerAdapter
import com.vwo.sampleapp.data.HousingViewModel
import com.vwo.sampleapp.models.HouseListing
import kotlinx.android.synthetic.main.fragment_house_listing.view.*

/**
 * Created by aman on Tue 17/07/18 12:18.
 */
class FragmentHousing : Fragment() {
    val linearLayoutManager: LinearLayoutManager = LinearLayoutManager(context)
    val FRAGMENT_ID = "fragmentId"
    var fragmentId: Int = -1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_house_listing, container, false)

        view.listing_recycler_view.layoutManager = linearLayoutManager

        val housingViewModel = ViewModelProviders.of(this).get(HousingViewModel::class.java)

        housingViewModel.housesLiveData.observe(this, Observer<List<HouseListing>> { housesListing ->
            val adapter = HousingRecylerAdapter(housesListing = housesListing, context = context!!)
            view.listing_recycler_view.adapter = adapter
        })

        return view
    }

    companion object {
        @JvmStatic
        fun getInstance(fragmentId: Int): FragmentHousing = FragmentHousing().apply {
            val bundle = Bundle()
            bundle.putInt(FRAGMENT_ID, fragmentId)
            arguments = bundle
        }
    }
}