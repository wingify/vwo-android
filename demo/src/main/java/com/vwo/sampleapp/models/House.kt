package com.vwo.sampleapp.models

import androidx.annotation.DrawableRes

/**
 * Created by aman on Tue 10/07/18 15:58.
 */
class House(
        val id: Int = 0,
        val name: String,
        val price: Int,
        val BHK: Int,
        @DrawableRes
        val image: Int,
        val type: Type = Type.RESIDENTIAL,
        val description: String = "Apartment for rent",
        val units: String = "$"
) {
    enum class Type(val type: String) {
        RESIDENTIAL("Residential"),
        COMMERCIAL("Commercial")

    }

}
