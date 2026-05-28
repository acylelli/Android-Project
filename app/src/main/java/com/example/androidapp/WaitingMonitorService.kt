package com.example.androidapp

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log

class WaitingMonitorService : Service() {

    private val handler = Handler(Looper.getMainLooper())
    private var tickCount = 0

    private val broadcastRunnable = object : Runnable {
        override fun run() {
            tickCount++
            val message = when (tickCount) {
                1 -> "웨이팅 등록이 완료되었습니다."
                2 -> "앞 대기 2명이 취소되었습니다."
                3 -> "2분 후 자리가 비워집니다."
                else -> "곧 입실 안내가 발송됩니다."
            }
            Log.d(TAG, message)

            val intent = Intent(AppConstants.ACTION_WAITING_UPDATE).apply {
                putExtra(AppConstants.EXTRA_UPDATE_MESSAGE, message)
                setPackage(packageName)
            }
            sendBroadcast(intent)

            if (tickCount < 4) {
                handler.postDelayed(this, 5000L)
            } else {
                stopSelf()
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        tickCount = 0
        handler.removeCallbacks(broadcastRunnable)
        handler.postDelayed(broadcastRunnable, 2000L)
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        handler.removeCallbacks(broadcastRunnable)
        super.onDestroy()
    }

    companion object {
        private const val TAG = "WaitingMonitorService"
    }
}
