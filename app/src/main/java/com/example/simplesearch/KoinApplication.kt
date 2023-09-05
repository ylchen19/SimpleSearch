package com.example.simplesearch

import android.app.Application
import com.example.simplesearch.di.Module
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class KoinApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin{
            androidContext(this@KoinApplication)
            modules(listOf(Module))
        }
    }
}