package com.amarchaud.travelcar

import android.app.Application
import com.google.android.libraries.places.api.Places
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class TravelCarApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        val apiKey = getString(R.string.api_key)
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, apiKey)
        }
    }
}