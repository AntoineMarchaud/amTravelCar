package com.amarchaud.travelcar

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import com.google.android.libraries.places.api.Places






@HiltAndroidApp
class TravelCarApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val apiKey = getString(R.string.api_key)
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, apiKey)
        }
    }
}