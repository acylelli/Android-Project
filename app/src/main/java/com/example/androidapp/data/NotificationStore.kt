package com.example.androidapp.data

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object NotificationStore {
    private val notifications = mutableListOf<NotificationEvent>()
    private var isInitialized = false

    private fun ensureInitialized() {
        if (!isInitialized) {
            notifications.addAll(MockData.notificationHistory)
            isInitialized = true
        }
    }

    fun saveSeatAlert(context: Context, seatNumber: Int) {
        ensureInitialized()
        val message = "좌석 알림 신청"
        val detail = "${seatNumber}번 좌석이 공석이 되면 알려드릴게요."
        addNotification(message, detail)
    }

    fun saveReservationAlert(context: Context, placeName: String, room: String, startTime: String) {
        ensureInitialized()
        val message = "예약 신청 완료"
        val detail = "${placeName} ${room} (${startTime}) 예약이 완료되었습니다."
        addNotification(message, detail)
    }

    fun getNotifications(context: Context): List<NotificationEvent> {
        ensureInitialized()
        return notifications.toList()
    }

    private fun addNotification(message: String, detail: String) {
        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        val newNotification = NotificationEvent(currentTime, message, detail)
        notifications.add(0, newNotification)
    }
}
