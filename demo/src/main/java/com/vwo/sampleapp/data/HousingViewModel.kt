package com.vwo.sampleapp.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vwo.sampleapp.R
import com.vwo.sampleapp.models.House
import com.vwo.sampleapp.models.HouseListing

/**
 * Created by aman on Tue 17/07/18 15:08.
 */
class HousingViewModel : ViewModel() {

    val housesLiveData = MutableLiveData<List<HouseListing>>()

    init {
        val listing: MutableList<HouseListing> = mutableListOf()
        listing.apply {
            val houses1: MutableList<House> = mutableListOf()

            this.add(HouseListing(houses1.apply {
                add(House(1,"Sai Enclave", 2_000,1,  R.drawable.h1))
                add(House(2,"Zero One", 7_000,1,  R.drawable.h2, House.Type.COMMERCIAL))
                add(House(3,"Siddhartha Enclave", 3_000,1,  R.drawable.h3))
                add(House(4,"Waterfront", 4_000,1,  R.drawable.h4))
            }, 1))

            val houses2: MutableList<House> = mutableListOf()


            this.add(HouseListing(houses2.apply{
                add(House(1,"Panchsheel", 3_000,2,  R.drawable.h5))
                add(House(2,"Marvel", 4_000,2,  R.drawable.h6))
                add(House(3,"Aurum", 5_000,2,  R.drawable.h7, House.Type.COMMERCIAL))
                add(House(4,"Blue Bells", 700,2,  R.drawable.h8))
            }, 2))

            val houses3: MutableList<House> = mutableListOf()

            this.add(HouseListing(houses3.apply {
                add(House(1,"Trump Towers", 5_000,3,  R.drawable.h9))
                add(House(2,"ABIL", 7_000,3,  R.drawable.h10))
                add(House(3,"Radhe Shaam", 4_500,3,  R.drawable.h11))
                add(House(4,"DSK", 3_400,3,  R.drawable.h12, House.Type.COMMERCIAL))
            }, 3))
        }

        housesLiveData.value = listing
    }
}