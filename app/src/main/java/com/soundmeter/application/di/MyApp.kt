package com.soundmeter.application.di

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import com.orhanobut.hawk.Hawk
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class MyApp: Application() {
    
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        AndroidThreeTen.init(this)
        Hawk.init(this).build()
    }
}