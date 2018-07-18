package com.vwo.sampleapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.vwo.sample.extensions.inflate
import com.vwo.sampleapp.R
import com.vwo.sampleapp.adapters.HousingRecyclerAdapter
import com.vwo.sampleapp.data.HousingViewModel
import com.vwo.sampleapp.interfaces.NestedItemClickListener
import com.vwo.sampleapp.models.HouseListing
import com.vwo.sampleapp.utils.SharedPreferencesHelper
import kotlinx.android.synthetic.main.dialog_housing.view.*
import kotlinx.android.synthetic.main.fragment_house_listing.view.*

/**
 * Created by aman on Tue 17/07/18 12:18.
 */
class FragmentHousing : Fragment() {
    val FRAGMENT_ID = "fragmentId"
    var fragmentId: Int = -1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_house_listing, container, false)

        view.listing_recycler_view.layoutManager = LinearLayoutManager(context)

        val housingViewModel = ViewModelProviders.of(this).get(HousingViewModel::class.java)

        housingViewModel.housesLiveData.observe(this, Observer<List<HouseListing>> { housesListing ->
            val adapter = HousingRecyclerAdapter(housesListing = housesListing, context = context!!, listener = object : NestedItemClickListener {
                override fun onItemClicked(viewHolder: RecyclerView.ViewHolder, parentAdapterPosition: Int, childAdapterPosition: Int) {
                    val house = housesListing[parentAdapterPosition].houses[childAdapterPosition]

                    val builder = AlertDialog.Builder(context!!, R.style.popup_theme)

                    val viewInflated = context!!.inflate(R.layout.dialog_housing, null)
                    // Set up the input
                    val title = viewInflated.dialog_title_house

                    val message = viewInflated.dialog_message_house
                    val button = viewInflated.dialog_button_house


                    button.text = getString(R.string.dialog_house_button, house.units, house.price)
                    builder.setView(viewInflated)

                    val dialog = builder.create()
                    dialog.show()

                    button.setOnClickListener {
                        dialog.dismiss()
                    }

                }

            })
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