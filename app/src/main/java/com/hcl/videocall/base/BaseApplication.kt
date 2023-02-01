package com.hcl.videocall.base

import android.app.Application

class BaseApplication : Application() {

    companion object{
        lateinit var APPLICATION : BaseApplication
    }

    override fun onCreate() {
        super.onCreate()
        APPLICATION =this@BaseApplication
    }

}