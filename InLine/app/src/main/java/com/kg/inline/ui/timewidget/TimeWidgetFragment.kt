package com.kg.inline.ui.timewidget

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.kg.inline.R
import kotlinx.android.synthetic.main.fragment_timewidget.*

class TimeWidgetFragment : Fragment() {
    private lateinit var timeWidgetViewModel: TimeWidgetViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_timewidget, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        timeWidgetViewModel = ViewModelProvider(this).get(TimeWidgetViewModel::class.java)

        changeShowTimeButtonText()
        showTimeWindow.setOnClickListener {
            onClickShowTime()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1122) {
            context?.let {
                if (Settings.canDrawOverlays(it)) {
                    startShowTime()
                } else {
                    Toast.makeText(it, "No Permission", Toast.LENGTH_LONG).show()

                }
            }
        }
    }

    private fun onClickShowTime() {
        context?.let {
            if (!Settings.canDrawOverlays(it)) {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${activity?.packageName}"))
                startActivityForResult(intent, 1122)
            } else {
                startShowTime()
            }
        }
    }

    private fun startShowTime() {
        context?.applicationContext?.let {
            val intentOne = Intent(it, TimeFloatingService::class.java)

            if (showTimeWindow.text == it.getString(R.string.hide_time_window)) {

                it.stopService(intentOne)
                TimeFloatingService.isStarted = false
                PreferenceManager.getDefaultSharedPreferences(it).edit().putBoolean("time.floating.window.open", false).apply()
            } else {

                it.startService(intentOne)
                TimeFloatingService.isStarted = true
                PreferenceManager.getDefaultSharedPreferences(it).edit().putBoolean("time.floating.window.open", true).apply()
            }

            changeShowTimeButtonText()
        }
    }

    private fun changeShowTimeButtonText() {
        context?.let {
            if(TimeFloatingService.isStarted) {
                showTimeWindow.text = it.getString(R.string.hide_time_window)
            } else {
                showTimeWindow.text = it.getString(R.string.show_time_window)
            }
        }
    }

}