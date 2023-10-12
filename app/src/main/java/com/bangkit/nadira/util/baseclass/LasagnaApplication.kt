package com.bangkit.nadira.util.baseclass

import android.app.Application
import com.google.firebase.FirebaseApp
import timber.log.Timber

class LasagnaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        FirebaseApp.initializeApp(this)
    }
}