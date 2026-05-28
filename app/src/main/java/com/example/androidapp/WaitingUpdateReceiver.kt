package com.example.androidapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * 매니페스트에 등록된 브로드캐스트 리시버 (강의 10장 패턴).
 * 앱 내부 웨이팅 알림은 MyWaitingActivity에서 동적 등록으로 수신합니다.
 */
class WaitingUpdateReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val message = intent?.getStringExtra(AppConstants.EXTRA_UPDATE_MESSAGE) ?: return
        Log.i(TAG, "Manifest receiver: $message")
    }

    companion object {
        private const val TAG = "WaitingUpdateReceiver"
    }
}
