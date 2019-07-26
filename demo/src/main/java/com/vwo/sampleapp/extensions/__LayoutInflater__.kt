package com.vwo.sample.extensions

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes

/**
 * Created by aman on Tue 10/07/18 16:19.
 */
fun Context.inflate(@LayoutRes id: Int, viewGroup: ViewGroup?): View = LayoutInflater.from(this).inflate(id, viewGroup, false)