package com.kg.inline.ui.timewidget

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.preference.PreferenceManager

class RebornReceiver: BroadcastReceiver() {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context?, intent: Intent?) {

        context?.let {
            val isOpen = PreferenceManager.getDefaultSharedPreferences(it).getBoolean("time.floating.window.open", false)
            if (isOpen) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    it.startForegroundService(Intent(it, TimeFloatingService::class.java))

                } else {

                    it.startService(Intent(it, TimeFloatingService::class.java))
                }

            }
        }



    }
}