package com.examprepare.easybus.core.di

import android.app.Application
import com.examprepare.easybus.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber


@HiltAndroidApp
class EasyBusApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // 若目前在開發狀態，初始化Timber
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}