package com.example.maslahah

import android.app.Application
import com.example.maslahah.utils.MyPreference

class MyApp : Application() {


    override fun onCreate() {
        super.onCreate()
        MyPreference.init(this)
    }




}