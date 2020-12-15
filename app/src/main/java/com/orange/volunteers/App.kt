package com.orange.volunteers

import android.app.Application
import com.facebook.stetho.Stetho
import com.orange.volunteers.instances.Instances

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)
        }
        Instances.init(this)
        Instances.create(this)

    }
}