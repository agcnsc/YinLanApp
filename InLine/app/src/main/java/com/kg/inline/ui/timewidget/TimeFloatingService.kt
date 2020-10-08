package com.kg.inline.ui.timewidget

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.os.IInterface
import android.os.Parcel
import android.view.Gravity
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.TextView
import com.kg.inline.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.FileDescriptor
import java.text.SimpleDateFormat
import java.util.*


class TimeFloatingService : Service() {
    private lateinit var mWindowManager: WindowManager
    private lateinit var wmParams: WindowManager.LayoutParams
    private lateinit var mFloatingLayout: TextView
    private lateinit var timeThread: Thread

    private var x = 0
    private var y = 0

    companion object {
        var isStarted = false
    }


    override fun onBind(intent: Intent): IBinder {
        return MyBinder()
    }

    override fun onCreate() {
        super.onCreate()
        isStarted = true

        mWindowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wmParams = createInitialParams()

        mFloatingLayout = makeFloatingView()

        mWindowManager.addView(mFloatingLayout, wmParams)

        timeThread = makeThread()
        timeThread.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        mWindowManager.removeView(mFloatingLayout)
        isStarted = false
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    private fun createInitialParams(): WindowManager.LayoutParams {
        val wpm = WindowManager.LayoutParams()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            wpm.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            wpm.type = WindowManager.LayoutParams.TYPE_PHONE
        }
        wpm.format = PixelFormat.RGBA_8888
        wpm.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
        wpm.width = WindowManager.LayoutParams.WRAP_CONTENT
        wpm.height = WindowManager.LayoutParams.WRAP_CONTENT
        wpm.gravity = Gravity.START or Gravity.TOP
        wpm.x = 400
        wpm.y = 80

        return wpm
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun makeFloatingView(): TextView {
        val view = TextView(this.applicationContext)
        view.text = getNow()
        view.isFocusableInTouchMode = true

        view.setTextColor(android.graphics.Color.WHITE)

        view.setOnClickListener {
            val dialogIntent: Intent = Intent(baseContext, MainActivity::class.java)
            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(dialogIntent)
        }

        view.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    x = event.rawX.toInt()
                    y = event.rawY.toInt()
                }
                MotionEvent.ACTION_MOVE -> {
                    val nowX = event.rawX.toInt()
                    val nowY = event.rawY.toInt()
                    val movedX: Int = nowX - x
                    val movedY: Int = nowY - y
                    x = nowX
                    y = nowY
                    wmParams.x = wmParams.x + movedX
                    wmParams.y = wmParams.y + movedY
                    mWindowManager.updateViewLayout(mFloatingLayout, wmParams)
                }
                else -> {
                    //ignore
                }
            }
            return@setOnTouchListener false
        }
        return view
    }

    private fun makeThread(): Thread {
        return Thread(Runnable {
            while (isStarted) {
                Thread.sleep(100)

                GlobalScope.launch(Dispatchers.Main) {
                    mFloatingLayout.text = getNow()
                }
            }
        })
    }


    private fun getNow(): String {
        return SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(Date())
    }


    class MyBinder : IBinder {
        override fun getInterfaceDescriptor(): String? {
            return null
        }

        override fun isBinderAlive(): Boolean {
            return false
        }

        override fun linkToDeath(recipient: IBinder.DeathRecipient, flags: Int) {
        }

        override fun queryLocalInterface(descriptor: String): IInterface? {
            return null
        }

        override fun transact(code: Int, data: Parcel, reply: Parcel?, flags: Int): Boolean {
            return false
        }

        override fun dumpAsync(fd: FileDescriptor, args: Array<out String>?) {
        }

        override fun dump(fd: FileDescriptor, args: Array<out String>?) {
        }

        override fun unlinkToDeath(recipient: IBinder.DeathRecipient, flags: Int): Boolean {
            return false
        }

        override fun pingBinder(): Boolean {
            return false
        }

    }
}

