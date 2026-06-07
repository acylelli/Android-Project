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
            name = "랭스터디카페 성신여대점",
            category = PlaceCategory.STUDY_CAFE,
            address = "서울 성북구 동소문로20길 37-6 2층, 3층",
            distanceKm = 1.0,
            tags = listOf("WiFi", "음료", "프라이빗존"),
            occupancy = OccupancyLevel.BUSY,
            occupancyPercent = 90,
            totalSeats = 40,
            emptySeats = 4,
            inUse = 36,
            rating = 4.5f,
            imageResId = R.drawable.studycafe,
            hours = "00:00 - 24:00",
            fee = "2,000원 / 시간",
            amenities = listOf("무료커피", "개인사물함", "WiFi", "콘센트")
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
        Place(
            id = "6",
            name = "성북정보도서관",
            category = PlaceCategory.LIBRARY,
            address = "서울 성북구 화랑로18자길 13",
            distanceKm = 1.6,
            tags = listOf("구립도서관", "열람실", "좌석현황"),
            occupancy = OccupancyLevel.BUSY,
            occupancyPercent = 63,
            totalSeats = 194,
            emptySeats = 71,
            inUse = 123,
            rating = 4.6f,
            imageResId = R.drawable.library,
            hours = "09:00 - 22:00",
            fee = "무료",
            amenities = listOf("일반열람실", "자료실", "WiFi", "노트북존")
        ),
        Place(
            id = "7",
            name = "아리랑도서관",
            category = PlaceCategory.LIBRARY,
            address = "서울 성북구 아리랑로 82",
            distanceKm = 1.4,
            tags = listOf("구립도서관", "자료실", "학습석"),
            occupancy = OccupancyLevel.AVAILABLE,
            occupancyPercent = 58,
            totalSeats = 112,
            emptySeats = 47,
            inUse = 65,
            rating = 4.5f,
            imageResId = R.drawable.library,
            hours = "09:00 - 22:00",
            fee = "무료",
            amenities = listOf("종합자료실", "열람석", "WiFi")
        ),
        Place(
            id = "8",
            name = "해오름도서관",
            category = PlaceCategory.LIBRARY,
            address = "서울 성북구 성북로4길 52",
            distanceKm = 1.9,
            tags = listOf("구립도서관", "열람석", "어린이자료실"),
            occupancy = OccupancyLevel.BUSY,
            occupancyPercent = 68,
            totalSeats = 84,
            emptySeats = 27,
            inUse = 57,
            rating = 4.4f,
            imageResId = R.drawable.library,
            hours = "09:00 - 18:00",
            fee = "무료",
            amenities = listOf("열람석", "자료실", "문화프로그램")
        ),
    )

    val filterChips = PlaceCategory.entries

    val timeSlots = listOf(
        "09:00", "10:00", "11:00", "12:00",
        "13:00", "14:00", "15:00", "16:00",
    )

    // 기존 세미나실 데이터 복구 (101~112호)
    fun seminarRooms(): List<SeminarRoom> = (101..112).map { num ->
        val status = when (num) {
            101, 103, 105, 108, 111, 112 -> RoomStatus.AVAILABLE
            104, 106, 107, 109, 110 -> RoomStatus.OCCUPIED
            else -> RoomStatus.AVAILABLE
        }
        SeminarRoom(num, status)
    }

    // 학술정보관 전용 좌석 데이터 (1~50번)
    fun studyRoomSeats(): List<SeminarRoom> = (1..50).map { num ->
        val availableSeats = setOf(1, 24, 43)
        val status = if (num in availableSeats) RoomStatus.AVAILABLE else RoomStatus.OCCUPIED
        SeminarRoom(num, status)
    }

    fun studyCafeSeatLayout(): List<StudyCafeSeat?> {
        val availableSeats = setOf(7, 18, 31, 37)
        val seatByNumber = (1..40).associateWith { number ->
            val zone = when {
                number <= 6 -> StudyCafeZone.STUDY_ROOM
                number <= 28 -> StudyCafeZone.SOLO
                else -> StudyCafeZone.COUNTER
            }
            val status = if (number in availableSeats) RoomStatus.AVAILABLE else RoomStatus.OCCUPIED
            StudyCafeSeat(
                number = number,
                zone = zone,
                status = status,
                remainingSeconds = if (status == RoomStatus.OCCUPIED) randomStudyCafeRemainingSeconds(number) else 0L,
            )
        }

        val layout = listOf(
            listOf(1, 2, 3, null, 7, 8, 9, 10, null, 29, 30, 31, null, null, null, null, null, null),
            listOf(4, 5, 6, null, 11, 12, 13, 14, null, 32, 33, 34, null, null, null, null, null, null),
            listOf(null, null, null, null, 15, 16, 17, 18, null, 35, 36, 37, null, null, null, null, null, null),
            listOf(null, null, null, null, 19, 20, 21, 22, null, 38, 39, 40, null, null, null, null, null, null),
            listOf(null, null, null, null, 23, 24, 25, 26, null, null, null, null, null, null, null, null, null, null),
            listOf(null, null, null, null, 27, 28, null, null, null, null, null, null, null, null, null, null, null, null),
            listOf(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null),
            listOf(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null),
        )

        return layout.flatten().map { number -> number?.let { seatByNumber.getValue(it) } }
    }

    private fun randomStudyCafeRemainingSeconds(seatNumber: Int): Long {
        val mixed = (seatNumber * 1_741 + seatNumber * seatNumber * 97 + 2_039) % 10_620
        return 180L + mixed
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
        NotificationEvent("14:20", "대기 신청이 완료되었습니다.", "자리가 나면 알려드려요."),
        NotificationEvent("14:35", "앞에 5명 남았습니다.", "조금만 더 기다려 주세요."),
        NotificationEvent("14:50", "앞에 3명 남았습니다. 준비해 주세요.", "입장 가능 알림을 놓치지 마세요."),
    )
}
