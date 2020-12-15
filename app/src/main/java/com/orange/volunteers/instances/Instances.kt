package com.orange.volunteers.instances

import android.app.Application
import android.content.Context
import com.orange.volunteers.campaigns.details.ServicesProvider
import com.orange.volunteers.instances.modules.ClientProvider
import com.orange.volunteers.instances.modules.defaultModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.dsl.koinApplication

object Instances {

    fun init(application: Application) {
        startKoin {
            androidContext(application)
            loadKoinModules(defaultModule)
        }
        ClientProvider.context = application

    }

    internal lateinit var context: Context
    internal val app: Application
        get() = context as Application

    fun create(context: Context) {
        this.context = context.applicationContext
    }

    val servicesProvider: ServicesProvider
        get() = ServicesProvider.getInstance(context)


}