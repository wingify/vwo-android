package com.vwo.sampleapp.models

import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.android.parcel.Parcelize

/**
 * Created by aman on Tue 17/07/18 16:48.
 */
@Parcelize
class Success(val message: String, @DrawableRes val imageId: Int): Parcelable