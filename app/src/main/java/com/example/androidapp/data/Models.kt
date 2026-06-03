package com.example.androidapp.data

import com.example.androidapp.R

enum class PlaceCategory(val label: String) {
    ALL("전체"),
    READING_ROOM("독서실"),
    CAFE("카페"),
    LIBRARY("도서관"),
    SCHOOL_STUDY("학교 열람실"),
    STUDY_CAFE("스터디카페"),
}

enum class OccupancyLevel {
    AVAILABLE,
    BUSY,
    FULL,
}

enum class RoomStatus {
    AVAILABLE,
    SELECTED,
    OCCUPIED,
    UNAVAILABLE,
}

data class Place(
    val id: String,
    val name: String,
    val category: PlaceCategory,
    val address: String,
    val distanceKm: Double,
    val tags: List<String>,
    val occupancy: OccupancyLevel,
    val occupancyPercent: Int,
    val totalSeats: Int,
    val imageResId: Int = R.drawable.hansungstudy,
    val isSeminar: Boolean = false,
    val emptySeats: Int = 0,
    val inUse: Int = 0,
    val waiting: Int = 0,
    val rating: Float = 4.5f,
    val hours: String = "09:00 - 22:00",
    val fee: String = "무료, 학생증 지참",
    val amenities: List<String> = listOf("콘센트", "WiFi"),
)

data class SeminarRoom(
    val number: Int,
    val status: RoomStatus,
)

data class WaitingInfo(
    val placeName: String,
    val roomLabel: String,
    val queuePosition: Int,
    val estimatedMinutes: Int,
    val plannedHours: Int,
)

data class NotificationEvent(
    val time: String,
    val message: String,
)
