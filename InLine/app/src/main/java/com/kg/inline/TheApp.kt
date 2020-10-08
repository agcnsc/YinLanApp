package com.kg.inline

import android.app.Application

class TheApp: Application() {
    companion object {
        @Volatile private var instance: TheApp? = null

        fun getInstance() =
            instance
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

}