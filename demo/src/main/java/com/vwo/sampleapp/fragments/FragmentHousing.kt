package com.vwo.sampleapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.vwo.sampleapp.R
import kotlinx.android.synthetic.main.fragment_house_listing.view.*

/**
 * Created by aman on Tue 17/07/18 12:18.
 */
class FragmentHousing: Fragment() {
    val linearLayoutManager: LinearLayoutManager = LinearLayoutManager(context)
    val FRAGMENT_ID = "fragmentId"
    var fragmentId: Int = -1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_house_listing, container, false)

        view.listing_recycler_view.layoutManager = linearLayoutManager


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