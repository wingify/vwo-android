package com.vwo.sampleapp.app

import android.app.Application

/**
 * Created by aman on 16/08/17.
 */

class VWOApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        //        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
        //                .detectAll()
        //                .build());
    }
}
