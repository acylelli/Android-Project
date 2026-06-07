package com.example.androidapp

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.androidapp.adapter.SeminarRoomAdapter
import com.example.androidapp.data.MockData
import com.example.androidapp.data.NotificationStore
import com.example.androidapp.databinding.ActivitySeminarRoomBinding

class SeminarRoomActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySeminarRoomBinding
    private lateinit var roomAdapter: SeminarRoomAdapter

    private var selectedRoom = 102
    private var isSeatMode = false
    private var selectedDate = Pair("월", "1")
    private var selectedTime = "11:00"

    private val juneDates = ArrayList<Pair<String, String>>()
    private val weekdays = listOf("월", "화", "수", "목", "금", "토", "일")
    private val timeSlots = listOf("09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00")

    // 임의의 예약 데이터 제거 (모든 시간 활성화)
    private val inUseTimeSlots = emptyList<String>()

    // 피그마 스펙 정밀 컬러 정의 (사진에 맞춰 테라색으로 수정)
    private val colorSelectedTheme = Color.parseColor("#00927A")   // 선택됨 (배경/테두리/텍스트)
    private val colorAvailableBorder = Color.parseColor("#00927A") // 잔여석 (테두리)
    private val colorInUseBorder = Color.parseColor("#EBEBEB")     // 사용중 (테두리)
    private val colorInUseFill = Color.parseColor("#F5F5F3")       // 사용중 (배경)
    private val colorInUseText = Color.parseColor("#CCCCCC")       // 사용중 (텍스트)
    private val colorRangeMiddleFill = Color.parseColor("#E6F5F2") // 범위 중간 배경 (연한 녹색)
    private val colorDisabledText = Color.parseColor("#BDBDBD")    // 선택 불가 텍스트

    // 요일 컬러 정의
    private val colorSat = Color.parseColor("#4000FF")             // 토요일
    private val colorSun = Color.parseColor("#F10000")             // 일요일
    private val colorTextPrimary = Color.parseColor("#222222")     // 평일 텍스트
    private val colorTextSecondary = Color.parseColor("#888888")   // 요일 텍스트

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySeminarRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isSeatMode = intent.getBooleanExtra("is_seat_selection", false)
        if (isSeatMode) {
            setupSeatModeUI()
        }

        generateJuneCalendar()

        binding.btnBack.setOnClickListener { finish() }

        roomAdapter = SeminarRoomAdapter(isSeatMode) { roomNumber ->
            selectedRoom = roomNumber
            roomAdapter.updateSelection(roomNumber)
            updateSummary()
        }

        val initialSpanCount = if (isSeatMode) 5 else 2
        binding.rvRooms.layoutManager = GridLayoutManager(this, initialSpanCount)
        binding.rvRooms.adapter = roomAdapter
        
        // [수정] 모드에 따라 데이터 분리: 세미나실(101~112호) vs 열람실 좌석(1~50번)
        val data = if (isSeatMode) MockData.studyRoomSeats() else MockData.seminarRooms()
        val initialSelected = if (isSeatMode) 1 else 102
        selectedRoom = initialSelected
        
        roomAdapter.submitList(data, selectedRoom)

        if (isSeatMode) {
            setupSeatModeUI()
        } else {
            setupSeminarModeUI()
            setupDateViews()
            setupTimeViews()
            updateSummary()
        }

        binding.btnRegisterWaiting.setOnClickListener {
            if (isSeatMode) {
                NotificationStore.saveSeatAlert(this, selectedRoom)
                startActivity(Intent(this, NotificationActivity::class.java))
                return@setOnClickListener
            }
            NotificationStore.saveReservationAlert(
                context = this,
                placeName = "한성대 공대 A동 세미나실",
                room = "${selectedRoom}호 세미나실",
                startTime = selectedTime,
            )
            startActivity(createReservationIntent())
        }
    }

    private fun generateJuneCalendar() {
        juneDates.clear()
        for (day in 1..30) {
            val weekday = weekdays[(day - 1) % 7]
            juneDates.add(Pair(weekday, day.toString()))
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    // [수정] 상태별(선택됨, 잔여, 사용중, 범위중간) 동적 드로어블 생성 함수
    private fun createBoxDrawable(state: String, radiusDp: Int): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = dpToPx(radiusDp).toFloat()
            when (state) {
                "SELECTED" -> {
                    setColor(colorSelectedTheme)
                    setStroke(dpToPx(1), colorSelectedTheme)
                }
                "AVAILABLE" -> {
                    setColor(Color.WHITE)
                    setStroke(dpToPx(1), colorAvailableBorder) // 잔여 테두리 컬러
                }
                "IN_USE" -> {
                    setColor(colorInUseFill)                   // 사용중 배경 컬러
                    setStroke(dpToPx(1), colorInUseBorder)     // 사용중 테두리 컬러
                }
                "RANGE_MIDDLE" -> {
                    setColor(colorRangeMiddleFill)             // 범위 중간 배경 (연한 녹색)
                    setStroke(dpToPx(1), colorAvailableBorder)
                }
            }
        }
    }

    private fun setupDateViews() {
        binding.layoutDates.removeAllViews()

        juneDates.forEach { datePair ->
            val isWeekend = datePair.first == "토" || datePair.first == "일"
            val cellLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(dpToPx(52), dpToPx(70)).apply {
                    setMargins(0, 0, dpToPx(8), 0)
                }

                val tvWeek = TextView(context).apply {
                    text = datePair.first
                    textSize = 11f
                    gravity = Gravity.CENTER
                }

                val tvDay = TextView(context).apply {
                    text = datePair.second
                    textSize = 15f
                    // [수정] 모든 예약 일자 숫자는 기본적으로 볼드체 적용
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(0, dpToPx(4), 0, 0)
                    }
                    gravity = Gravity.CENTER
                }

                if (isWeekend) {
                    background = createBoxDrawable("IN_USE", 12)
                    tvWeek.setTextColor(colorDisabledText)
                    tvDay.setTextColor(colorDisabledText)
                    isEnabled = false
                    alpha = 0.9f
                } else if (datePair == selectedDate) {
                    background = createBoxDrawable("SELECTED", 12)
                    tvWeek.setTextColor(Color.WHITE)
                    tvDay.setTextColor(Color.WHITE)
                } else {
                    background = createBoxDrawable("AVAILABLE", 12)
                    tvWeek.setTextColor(colorTextSecondary)

                    tvDay.setTextColor(colorTextPrimary)
                }

                addView(tvWeek)
                addView(tvDay)

                if (!isWeekend) {
                    setOnClickListener {
                        selectedDate = datePair
                        setupDateViews()
                        updateSummary()
                    }
                }
            }
            binding.layoutDates.addView(cellLayout)
        }
    }

    private fun createReservationIntent(): Intent {
        val hour = selectedTime.substringBefore(":").toInt()
        val endTime = "${String.format("%02d", hour + 2)}:00"
        return Intent(this, MyReservationActivity::class.java).apply {
            putExtra(AppConstants.EXTRA_RESERVATION_PLACE, "한성대 공대 A동 세미나실")
            putExtra(AppConstants.EXTRA_RESERVATION_ROOM, "${selectedRoom}호 세미나실")
            putExtra(AppConstants.EXTRA_RESERVATION_DATE, "6월 ${selectedDate.second}일 (${selectedDate.first})")
            putExtra(AppConstants.EXTRA_RESERVATION_TIME, "$selectedTime ~ $endTime")
        }
    }

    private fun setupTimeViews() {
        binding.gridLayoutTime.removeAllViews()

        val startHour = selectedTime.substringBefore(":").toInt()
        val endHour = startHour + 2
        val endTime = "${String.format("%02d", endHour)}:00"

        timeSlots.forEach { slot ->
            val tvTime = TextView(this).apply {
                text = slot
                gravity = Gravity.CENTER
                textSize = 14f

                val params = GridLayout.LayoutParams().apply {
                    width = 0
                    height = dpToPx(44)
                    columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                    setMargins(dpToPx(4), dpToPx(5), dpToPx(4), dpToPx(5))
                }
                layoutParams = params

                val slotHour = slot.substringBefore(":").toInt()

                // [수정] 상태별(사용중, 시작/종료 선택됨, 범위중간, 잔여) 컬러 및 클릭 이벤트 분기
                if (slot in inUseTimeSlots) {
                    background = createBoxDrawable("IN_USE", 10)
                    setTextColor(colorInUseText)
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                    setOnClickListener(null)
                } else if (slot == selectedTime || slot == endTime) {
                    background = createBoxDrawable("SELECTED", 10)
                    setTextColor(Color.WHITE)
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                } else if (slotHour > startHour && slotHour < endHour) {
                    background = createBoxDrawable("RANGE_MIDDLE", 10)
                    setTextColor(colorSelectedTheme) // 연한 녹색 배경엔 진한 녹색 텍스트
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                } else {
                    background = createBoxDrawable("AVAILABLE", 10)
                    setTextColor(colorTextPrimary)
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)

                    setOnClickListener {
                        selectedTime = slot
                        setupTimeViews()
                        updateSummary()
                    }
                }
            }
            binding.gridLayoutTime.addView(tvTime)
        }
    }

    private fun setupSeminarModeUI() {
        binding.tvTitle.text = "세미나실 예약"
        
        // 세미나실 예약 시 필요한 UI들 다시 보이기
        binding.tvDateLabel.visibility = android.view.View.VISIBLE
        binding.hsvDates.visibility = android.view.View.VISIBLE
        binding.tvTimeLabel.visibility = android.view.View.VISIBLE
        binding.gridLayoutTime.visibility = android.view.View.VISIBLE
        binding.layoutSummary.visibility = android.view.View.VISIBLE
        binding.divider1.visibility = android.view.View.VISIBLE
        binding.divider2.visibility = android.view.View.VISIBLE
        binding.layoutLegend.visibility = android.view.View.VISIBLE
        
        // SCREEN 표시 숨기기 (세미나실은 필요 없음)
        binding.layoutScreen.visibility = android.view.View.GONE
        
        binding.tvLegendAvailable.text = "잔여"
        val tvButton = binding.btnRegisterWaiting.getChildAt(0) as? android.widget.TextView
        tvButton?.text = "예약 신청하기"
        
        updateSpanCount()
    }

    private fun setupSeatModeUI() {
        binding.tvTitle.text = "좌석 선택"
        
        // 날짜, 시간, 요약 영역 및 구분선 완전히 숨기기
        binding.tvDateLabel.visibility = android.view.View.GONE
        binding.hsvDates.visibility = android.view.View.GONE
        binding.tvTimeLabel.visibility = android.view.View.GONE
        binding.gridLayoutTime.visibility = android.view.View.GONE
        binding.layoutSummary.visibility = android.view.View.GONE
        binding.divider1.visibility = android.view.View.GONE
        binding.divider2.visibility = android.view.View.GONE
        binding.layoutLegend.visibility = android.view.View.GONE
        
        // SCREEN 표시 보이기
        binding.layoutScreen.visibility = android.view.View.VISIBLE

        // Legend text for seats
        binding.tvLegendAvailable.text = "공석"

        // 버튼 텍스트 변경
        val tvButton = binding.btnRegisterWaiting.getChildAt(0) as? android.widget.TextView
        tvButton?.text = "선택한 자리 알림받기"

        updateSpanCount()
    }

    private fun updateSpanCount() {
        val orientation = resources.configuration.orientation
        val spanCount = if (isSeatMode) {
            // 열람실 좌석 모드: 세로 5열, 가로 10열 (영화관 스타일)
            if (orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE) 10 else 5
        } else {
            // 세미나실 모드: 기본 2열 (가로모드는 4열)
            if (orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE) 4 else 2
        }
        binding.rvRooms.layoutManager = androidx.recyclerview.widget.GridLayoutManager(this, spanCount)
    }

    override fun onConfigurationChanged(newConfig: android.content.res.Configuration) {
        super.onConfigurationChanged(newConfig)
        updateSpanCount()
    }

    private fun updateSummary() {
        val suffix = if (isSeatMode) "번" else "호"
        binding.tvSummaryRoom.text = "${selectedRoom}$suffix"
        binding.tvSummaryDate.text = "6월 ${selectedDate.second}일 (${selectedDate.first})"

        val hour = selectedTime.substringBefore(":").toInt()
        val endTime = "${String.format("%02d", hour + 2)}:00"
        binding.tvSummaryTime.text = "$selectedTime ~ $endTime"

        binding.tvSummaryRoom.setTextColor(colorSelectedTheme)
        binding.tvSummaryDate.setTextColor(colorSelectedTheme)
        binding.tvSummaryTime.setTextColor(colorSelectedTheme)
    }
}
