package com.example.androidapp.data

import com.example.androidapp.R

object MockData {
    val places = listOf(
        Place(
            id = "1",
            name = "한성대학교 학술정보관 - 창의열람실",
            category = PlaceCategory.SCHOOL_STUDY,
            address = "서울 성북구 삼선교로 16길 116",
            distanceKm = 1.1,
            tags = listOf("독서실", "WIFI", "콘센트"),
            occupancy = OccupancyLevel.BUSY,
            occupancyPercent = 83,
            totalSeats = 50,
            emptySeats = 3,
            inUse = 47,
            rating = 4.7f,
            imageResId = R.drawable.hansungstudy,
            hours = "09:00 - 22:00",
            fee = "무료, 학생증 필수",
            amenities = listOf("개인석", "WiFi", "콘센트", "냉난방")
        ),
        Place(
            id = "2",
            name = "성북 스터디카페 프리미엄",
            category = PlaceCategory.STUDY_CAFE,
            address = "서울 성북구 보문로 34길",
            distanceKm = 0.8,
            tags = listOf("WiFi", "음료"),
            occupancy = OccupancyLevel.AVAILABLE,
            occupancyPercent = 60,
            totalSeats = 40,
            emptySeats = 4,
            inUse = 36,
            rating = 4.5f,
            imageResId = R.drawable.hansungstudy,
            hours = "00:00 - 24:00",
            fee = "2,000원 / 시간",
            amenities = listOf("무료커피", "개인사물함", "WiFi")
        ),
        Place(
            id = "3",
            name = "한성대 공대 A동 세미나실",
            category = PlaceCategory.SCHOOL_STUDY,
            address = "서울 성북구 삼선교로",
            distanceKm = 0.5,
            tags = listOf("프로젝터", "화이트보드"),
            occupancy = OccupancyLevel.AVAILABLE,
            occupancyPercent = 40,
            totalSeats = 10,
            isSeminar = true,
            emptySeats = 4,
            inUse = 6,
            rating = 4.4f,
            imageResId = R.drawable.seminarroom,
            hours = "09:00 - 21:00",
            fee = "무료, 사전 예약",
            amenities = listOf("프로젝터", "화이트보드", "대형모니터")
        ),
        Place(
            id = "4",
            name = "안암 독서실 24",
            category = PlaceCategory.READING_ROOM,
            address = "서울 성북구 안암로",
            distanceKm = 1.2,
            tags = listOf("24시", "콘센트"),
            occupancy = OccupancyLevel.FULL,
            occupancyPercent = 100,
            totalSeats = 30,
            emptySeats = 0,
            inUse = 30,
            rating = 4.2f,
            imageResId = R.drawable.hansungstudy,
            hours = "00:00 - 24:00",
            fee = "일권 15,000원",
            amenities = listOf("백색소음기", "WiFi", "휴게실")
        ),
        Place(
            id = "5",
            name = "카페 모모 (스터디존)",
            category = PlaceCategory.CAFE,
            address = "서울 성북구 동소문로",
            distanceKm = 0.4,
            tags = listOf("WiFi", "조용함"),
            occupancy = OccupancyLevel.BUSY,
            occupancyPercent = 72,
            totalSeats = 25,
            emptySeats = 3,
            inUse = 22,
            rating = 4.8f,
            imageResId = R.drawable.hansungstudy,
            hours = "10:00 - 23:00",
            fee = "1인 1음료 주문",
            amenities = listOf("야외테라스", "WiFi", "콘센트")
        ),
    )

    val filterChips = PlaceCategory.entries

    val timeSlots = listOf(
        "09:00", "10:00", "11:00", "12:00",
        "13:00", "14:00", "15:00", "16:00",
    )

    fun seminarRooms(): List<SeminarRoom> = (101..110).map { num ->
        val status = when (num) {
            101, 103, 105, 108 -> RoomStatus.AVAILABLE
            104, 106, 107, 109 -> RoomStatus.OCCUPIED
            110 -> RoomStatus.UNAVAILABLE // 내 예약 (검정색)
            102 -> RoomStatus.AVAILABLE   // Activity에서 선택될 방
            else -> RoomStatus.AVAILABLE
        }
        SeminarRoom(num, status)
    }

    fun placeById(id: String): Place? = places.find { it.id == id }

    val waitingInfo = WaitingInfo(
        placeName = "한성대학교 학술정보관",
        roomLabel = "창의열람실 42번 좌석",
        queuePosition = 3,
        estimatedMinutes = 15,
        plannedHours = 4,
    )

    val notificationHistory = listOf(
        NotificationEvent("14:20", "대기 신청이 완료되었습니다."),
        NotificationEvent("14:35", "앞에 5명 남았습니다."),
        NotificationEvent("14:50", "앞에 3명 남았습니다. 준비해 주세요."),
    )
}
