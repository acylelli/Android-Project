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
import com.example.androidapp.databinding.ActivitySeminarRoomBinding

class SeminarRoomActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySeminarRoomBinding
    private lateinit var roomAdapter: SeminarRoomAdapter

    private var selectedRoom = 102
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

    // 요일 컬러 정의
    private val colorSat = Color.parseColor("#4000FF")             // 토요일
    private val colorSun = Color.parseColor("#F10000")             // 일요일
    private val colorTextPrimary = Color.parseColor("#222222")     // 평일 텍스트
    private val colorTextSecondary = Color.parseColor("#888888")   // 요일 텍스트

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySeminarRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        generateJuneCalendar()

        binding.btnBack.setOnClickListener { finish() }

        roomAdapter = SeminarRoomAdapter { roomNumber ->
            selectedRoom = roomNumber
            roomAdapter.updateSelection(roomNumber)
            updateSummary()
        }

        binding.rvRooms.layoutManager = GridLayoutManager(this, 5)
        binding.rvRooms.adapter = roomAdapter
        roomAdapter.submitList(MockData.seminarRooms(), selectedRoom)

        setupDateViews()
        setupTimeViews()
        updateSummary()

        binding.btnRegisterWaiting.setOnClickListener {
            startService(Intent(this, WaitingMonitorService::class.java))
            startActivity(Intent(this, MyWaitingActivity::class.java))
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

                if (datePair == selectedDate) {
                    background = createBoxDrawable("SELECTED", 12)
                    tvWeek.setTextColor(Color.WHITE)
                    tvDay.setTextColor(Color.WHITE)
                } else {
                    background = createBoxDrawable("AVAILABLE", 12)
                    tvWeek.setTextColor(colorTextSecondary)

                    // [수정] 주말 텍스트 컬러 지정 (토: #4000FF, 일: #F10000)
                    when (datePair.first) {
                        "토" -> tvDay.setTextColor(colorSat)
                        "일" -> tvDay.setTextColor(colorSun)
                        else -> tvDay.setTextColor(colorTextPrimary)
                    }
                }

                addView(tvWeek)
                addView(tvDay)

                setOnClickListener {
                    selectedDate = datePair
                    setupDateViews()
                    updateSummary()
                }
            }
            binding.layoutDates.addView(cellLayout)
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

    private fun updateSummary() {
        binding.tvSummaryRoom.text = "${selectedRoom}호"
        binding.tvSummaryDate.text = "6월 ${selectedDate.second}일 (${selectedDate.first})"

        val hour = selectedTime.substringBefore(":").toInt()
        val endTime = "${String.format("%02d", hour + 2)}:00"
        binding.tvSummaryTime.text = "$selectedTime ~ $endTime"

        binding.tvSummaryRoom.setTextColor(colorSelectedTheme)
        binding.tvSummaryDate.setTextColor(colorSelectedTheme)
        binding.tvSummaryTime.setTextColor(colorSelectedTheme)
    }
}