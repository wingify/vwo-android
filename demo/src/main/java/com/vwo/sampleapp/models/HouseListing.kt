package com.vwo.sampleapp.models

/**
 * Created by aman on Tue 17/07/18 16:55.
 */
class HouseListing(val houses: MutableList<House>, type: Int) {
    val type: String = type.toString()
        get() = "${field}BHK"
}