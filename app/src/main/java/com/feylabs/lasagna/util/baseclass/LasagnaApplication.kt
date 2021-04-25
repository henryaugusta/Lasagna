package com.feylabs.lasagna.util.baseclass

import android.app.Application
import timber.log.Timber

class LasagnaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}