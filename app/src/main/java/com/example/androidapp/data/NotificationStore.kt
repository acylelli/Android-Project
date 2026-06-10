package com.example.androidapp.data

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object NotificationStore {
    private val notifications = mutableListOf<NotificationEvent>()

    fun saveSeatAlert(context: Context, seatNumber: Int) {
        saveSeatAlert(context, "좌석", seatNumber)
    }

    fun saveSeatAlert(context: Context, placeName: String, seatNumber: Int) {
        val message = "$placeName 좌석 알림 신청"
        val detail = "${seatNumber}번 좌석이 공석이 되면 알려드릴게요."
        addNotification(message, detail)
    }

    fun saveSeatAvailableAlert(context: Context, placeName: String, seatNumber: Int) {
        val message = "$placeName 좌석 공석 알림"
        val detail = "${seatNumber}번 좌석이 비었습니다. 지금 확인해 보세요."
        addNotification(message, detail)
    }

    fun saveLibrarySeatAlert(context: Context, placeName: String, seatNumber: Int) {
        saveSeatAlert(context, placeName, seatNumber)
    }

    fun saveReservationAlert(context: Context, placeName: String, room: String, startTime: String) {
        val roomNumber = room.filter { it.isDigit() }
        val readableRoom = if (roomNumber.isNotBlank()) "${roomNumber}번 세미나실" else room
        val message = "세미나실 예약 신청 완료"
        val detail = "$readableRoom ($startTime) 예약이 완료되었습니다."
        addNotification(message, detail)
    }

    fun savePaymentCompleteAlert(context: Context, placeName: String, seatLabel: String, startTime: String) {
        val message = "$placeName 결제 완료"
        val detail = "$seatLabel 결제가 완료되었습니다. $startTime 에 맞춰 이용해 주세요."
        addNotification(message, detail)
    }

    fun saveSeatMoveAlert(context: Context, placeName: String, fromSeatLabel: String, toSeatLabel: String) {
        val message = "$placeName 자리 이동 완료"
        val detail = "좌석이 $fromSeatLabel 에서 $toSeatLabel 로 변경되었습니다."
        addNotification(message, detail)
    }

    fun saveSeatExtensionAlert(context: Context, placeName: String, seatLabel: String, extraHours: Int) {
        val message = "$placeName 이용 시간 연장 완료"
        val detail = "$seatLabel 이용 시간이 ${extraHours}시간 연장되었습니다."
        addNotification(message, detail)
    }

    fun getNotifications(context: Context): List<NotificationEvent> {
        return notifications.toList()
    }

    private fun addNotification(message: String, detail: String) {
        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        val newNotification = NotificationEvent(currentTime, message, detail)
        notifications.add(0, newNotification)
    }
}
