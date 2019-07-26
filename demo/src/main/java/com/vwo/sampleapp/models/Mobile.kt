package com.vwo.sampleapp.models

import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.IntRange
import kotlinx.android.parcel.Parcelize

/**
 * Created by aman on Fri 13/07/18 13:54.
 */
@Parcelize
class Mobile (val id: Int, var name: String, var price: Int, var units: String, var inStock: Boolean, var codAvailable: Boolean,
              @DrawableRes var imageId: Int, var vendor: String, var variantDetails: String,
              @param:IntRange(from = 0, to = 5) var rating: Int): Parcelable