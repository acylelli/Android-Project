package com.example.androidapp.data

object MockData {
    val places = listOf(
        Place(
            id = "1",
            name = "한성대학교 학술정보관",
            category = PlaceCategory.LIBRARY,
            address = "서울 성북구",
            distanceKm = 1.1,
            tags = listOf("독서실", "WIFI", "콘센트"),
            occupancy = OccupancyLevel.BUSY,
            occupancyPercent = 83,
            totalSeats = 50,
            emptySeats = 3,
            inUse = 47,
            waiting = 2,
            rating = 4.7f,
        ),
        Place(
            id = "2",
            name = "성북 스터디카페 프리미엄",
            category = PlaceCategory.STUDY_CAFE,
            address = "서울 성북구",
            distanceKm = 0.8,
            tags = listOf("WiFi", "음료"),
            occupancy = OccupancyLevel.AVAILABLE,
            occupancyPercent = 60,
            totalSeats = 40,
            emptySeats = 4,
            inUse = 36,
            waiting = 1,
        ),
        Place(
            id = "3",
            name = "한성대 공대 A동 세미나실",
            category = PlaceCategory.SCHOOL_STUDY,
            address = "서울 성북구",
            distanceKm = 0.5,
            tags = listOf("프로젝터", "화이트보드"),
            occupancy = OccupancyLevel.AVAILABLE,
            occupancyPercent = 40,
            totalSeats = 10,
            isSeminar = true,
            emptySeats = 4,
            inUse = 6,
            waiting = 0,
            rating = 4.4f,
        ),
        Place(
            id = "4",
            name = "안암 독서실 24",
            category = PlaceCategory.READING_ROOM,
            address = "서울 성북구",
            distanceKm = 1.2,
            tags = listOf("24시", "콘센트"),
            occupancy = OccupancyLevel.FULL,
            occupancyPercent = 100,
            totalSeats = 30,
            emptySeats = 0,
            inUse = 30,
            waiting = 5,
        ),
        Place(
            id = "5",
            name = "카페 모모 (스터디존)",
            category = PlaceCategory.CAFE,
            address = "서울 성북구",
            distanceKm = 0.4,
            tags = listOf("WiFi", "조용함"),
            occupancy = OccupancyLevel.BUSY,
            occupancyPercent = 72,
            totalSeats = 25,
            emptySeats = 3,
            inUse = 22,
            waiting = 3,
        ),
    )

    val filterChips = PlaceCategory.entries

    val timeSlots = listOf(
        "09:00", "10:00", "11:00", "12:00",
        "13:00", "14:00", "15:00", "16:00",
    )

    fun seminarRooms(): List<SeminarRoom> = (101..110).map { num ->
        val status = when (num) {
            101, 104, 107 -> RoomStatus.AVAILABLE
            102, 105 -> RoomStatus.OCCUPIED
            103 -> RoomStatus.UNAVAILABLE
            106 -> RoomStatus.SELECTED
            else -> RoomStatus.AVAILABLE
        }
        SeminarRoom(num, status)
    }

    val waitingInfo = WaitingInfo(
        placeName = "한성대 공대 A동",
        roomLabel = "세미나실 107",
        queuePosition = 1,
        estimatedMinutes = 5,
        plannedHours = 1,
    )

    val notificationHistory = listOf(
        NotificationEvent("14:02", "웨이팅 등록이 완료되었습니다."),
        NotificationEvent("14:05", "앞 대기 2명이 취소되었습니다."),
        NotificationEvent("14:08", "2분 후 자리가 비워집니다."),
        NotificationEvent("14:10", "곧 입실 안내가 발송됩니다."),
    )

    fun placeById(id: String): Place? = places.find { it.id == id }
}
