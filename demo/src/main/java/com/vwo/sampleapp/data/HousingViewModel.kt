package com.vwo.sampleapp.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vwo.sampleapp.models.House

/**
 * Created by aman on Tue 17/07/18 15:08.
 */
class HousingViewModel : ViewModel() {

    val housesLiveData = MutableLiveData<List<House>>()

    init {

    }
}