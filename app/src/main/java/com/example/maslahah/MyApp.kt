package com.example.maslahah

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import com.akexorcist.localizationactivity.core.LocalizationApplicationDelegate
import com.example.maslahah.utils.MyPreference

class MyApp : Application() {

    private val localizationDelegate = LocalizationApplicationDelegate()

    override fun attachBaseContext(base: Context) {
        MyPreference.init(base)
        localizationDelegate.setDefaultLanguage(base,MyPreference.getLanguage())
        super.attachBaseContext(localizationDelegate.attachBaseContext(base))
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        localizationDelegate.onConfigurationChanged(this)
    }

    override fun getApplicationContext(): Context {
        return localizationDelegate.getApplicationContext(super.getApplicationContext())
    }

    override fun getResources(): Resources {
        return localizationDelegate.getResources(baseContext, super.getResources())
    }
    override fun onCreate() {
        super.onCreate()
        MyPreference.init(this)
    }




}