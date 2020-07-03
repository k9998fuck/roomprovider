package org.codepond

import android.app.Application

class App : Application(){

    init {
        application = this
    }

    override fun onCreate() {
        super.onCreate()
    }

    companion object {
        lateinit var application: App
    }

}