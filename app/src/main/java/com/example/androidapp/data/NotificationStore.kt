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

    fun saveLibrarySeatAlert(context: Context, placeName: String, seatNumber: Int) {
        ensureInitialized()
        val message = "도서관 좌석 알림 신청"
        val detail = "${placeName} ${seatNumber}번 좌석이 이용 가능해지면 알려드릴게요."
        addNotification(message, detail)
    }

    fun saveReservationAlert(context: Context, placeName: String, room: String, startTime: String) {
        ensureInitialized()
        val message = "예약 신청 완료"
        val detail = "${placeName} ${room} (${startTime}) 예약이 완료되었습니다."
        addNotification(message, detail)
    }

    fun savePaymentCompleteAlert(context: Context, placeName: String, seatLabel: String, startTime: String) {
        ensureInitialized()
        val message = "결제 완료"
        val detail = "${placeName} ${seatLabel} 결제가 완료되었습니다. ${startTime}에 맞춰 이용해 주세요."
        addNotification(message, detail)
    }

    fun saveSeatMoveAlert(context: Context, placeName: String, fromSeatLabel: String, toSeatLabel: String) {
        ensureInitialized()
        val message = "자리 이동 완료"
        val detail = "${placeName} 좌석이 ${fromSeatLabel}에서 ${toSeatLabel}로 변경되었습니다."
        addNotification(message, detail)
    }

    fun saveSeatExtensionAlert(context: Context, placeName: String, seatLabel: String, extraHours: Int) {
        ensureInitialized()
        val message = "이용 시간 연장 완료"
        val detail = "${placeName} ${seatLabel} 이용 시간이 ${extraHours}시간 연장되었습니다."
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
