package com.vwo.sample.extensions

import android.app.Activity
import android.content.SharedPreferences

/**
 * Created by aman on Mon 16/07/18 17:24.
 */
fun Activity.getVWOPrefs(): SharedPreferences = getSharedPreferences("VWO_shared_prefs", Activity.MODE_PRIVATE)