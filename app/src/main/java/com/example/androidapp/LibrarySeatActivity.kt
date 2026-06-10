package com.example.androidapp

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.widget.GridLayout
import android.widget.Space
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.androidapp.data.MockData
import com.example.androidapp.data.NotificationStore
import com.example.androidapp.data.RoomStatus
import com.example.androidapp.databinding.ActivityLibrarySeatsBinding

class LibrarySeatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLibrarySeatsBinding
    private val seatMap = buildLibrarySeatMap()
    private var selectedSeat = 53
    private var placeName = "도서관"
    private var remainingSeats = REMAINING_SEATS
    private var inUseSeats = IN_USE_SEATS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLibrarySeatsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val placeId = intent.getStringExtra(AppConstants.EXTRA_PLACE_ID)
        val place = MockData.placeById(placeId.orEmpty())
        placeName = place?.name ?: "도서관"
        remainingSeats = place?.emptySeats ?: REMAINING_SEATS
        inUseSeats = place?.inUse ?: IN_USE_SEATS
        val waitingCount = place?.waiting ?: WAITING_COUNT

        binding.tvTitle.text = "$placeName 좌석"
        binding.tvTotalSeats.text = remainingSeats.toString()
        binding.tvInUseSeats.text = inUseSeats.toString()
        binding.tvAvailableSeats.text = waitingCount.toString()

        binding.btnBack.setOnClickListener { finish() }
        binding.btnNotifyLibrarySeat.setOnClickListener {
            NotificationStore.saveLibrarySeatAlert(this, placeName, selectedSeat)
            InAppNotification.show(
                activity = this,
                title = "$placeName 좌석 알림 신청",
                message = "${selectedSeat}번 좌석이 공석이 되면 알려드릴게요.",
            )
            scheduleSeatAvailableAlert(placeName, selectedSeat)
        }

        renderSeatMap()
        updateSelectedInfo(animateButton = false)
    }

    private fun renderSeatMap() {
        binding.gridLibrarySeats.removeAllViews()
        binding.gridLibrarySeats.columnCount = MAP_COLS

        for (row in 0 until MAP_ROWS) {
            for (col in 0 until MAP_COLS) {
                val number = seatMap[row][col]
                val view = if (number == null) {
                    Space(this).apply { layoutParams = spacerLayoutParams(row, col) }
                } else {
                    createSeatView(number, row, col)
                }
                binding.gridLibrarySeats.addView(view)
            }
        }
    }

    private fun createSeatView(number: Int, row: Int, col: Int): TextView {
        val status = statusFor(number)
        return TextView(this).apply {
            text = number.toString()
            gravity = Gravity.CENTER
            textSize = 11f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(textColorFor(number, status))
            background = backgroundFor(number, status)
            layoutParams = seatLayoutParams(row, col)
            setOnClickListener {
                selectedSeat = number
                renderSeatMap()
                updateSelectedInfo(animateButton = true)
            }
        }
    }

    private fun updateSelectedInfo(animateButton: Boolean) {
        binding.tvSelectedInfo.text = "선택 좌석: ${selectedSeat}번"
        binding.tvSelectedDetail.text = "사용 중인 좌석도 선택할 수 있습니다. 자리가 나면 알림을 보내드려요."
        binding.tvNotifyLibrarySeat.text = "${selectedSeat}번 좌석 알림받기"

        if (animateButton) {
            showActionButtonPopup()
        } else {
            binding.btnNotifyLibrarySeat.alpha = 1f
            binding.btnNotifyLibrarySeat.translationY = 0f
        }
    }

    private fun showActionButtonPopup() {
        binding.btnNotifyLibrarySeat.animate().cancel()
        binding.btnNotifyLibrarySeat.translationY = dp(72).toFloat()
        binding.btnNotifyLibrarySeat.alpha = 0f
        binding.btnNotifyLibrarySeat.animate()
            .translationY(0f)
            .alpha(1f)
            .setDuration(180L)
            .start()
    }

    private fun seatLayoutParams(row: Int, col: Int): GridLayout.LayoutParams {
        val density = resources.displayMetrics.density
        return GridLayout.LayoutParams(GridLayout.spec(row), GridLayout.spec(col)).apply {
            width = (36 * density).toInt()
            height = (36 * density).toInt()
            val horizontalMargin = (3 * density).toInt()
            val verticalMargin = if (row in GAP_ROWS) (2 * density).toInt() else (3 * density).toInt()
            setMargins(horizontalMargin, verticalMargin, horizontalMargin, verticalMargin)
        }
    }

    private fun spacerLayoutParams(row: Int, col: Int): GridLayout.LayoutParams {
        val density = resources.displayMetrics.density
        return GridLayout.LayoutParams(GridLayout.spec(row), GridLayout.spec(col)).apply {
            width = (8 * density).toInt()
            height = if (row in GAP_ROWS) (8 * density).toInt() else (1 * density).toInt()
        }
    }

    private fun backgroundFor(number: Int, status: RoomStatus): GradientDrawable {
        val density = resources.displayMetrics.density
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 3 * density
            when {
                number == selectedSeat -> {
                    setColor(Color.parseColor("#111827"))
                    setStroke((1 * density).toInt(), Color.parseColor("#111827"))
                }
                status == RoomStatus.AVAILABLE -> {
                    setColor(Color.WHITE)
                    setStroke((1 * density).toInt(), Color.parseColor("#00927A"))
                }
                else -> {
                    setColor(Color.parseColor("#F3F4F6"))
                    setStroke((1 * density).toInt(), Color.parseColor("#D1D5DB"))
                }
            }
        }
    }

    private fun textColorFor(number: Int, status: RoomStatus): Int {
        return when {
            number == selectedSeat -> Color.WHITE
            status == RoomStatus.AVAILABLE -> Color.parseColor("#007566")
            else -> Color.parseColor("#6B7280")
        }
    }

    private fun statusFor(number: Int): RoomStatus {
        return if (number in AVAILABLE_SEATS || SeatAvailabilityNotifier.isAvailable(placeName, number)) {
            RoomStatus.AVAILABLE
        } else {
            RoomStatus.OCCUPIED
        }
    }

    private fun scheduleSeatAvailableAlert(placeName: String, seatNumber: Int) {
        val wasOccupied = statusFor(seatNumber) == RoomStatus.OCCUPIED
        SeatAvailabilityNotifier.schedule(placeName, seatNumber) {
            if (wasOccupied) {
                remainingSeats += 1
                inUseSeats = (inUseSeats - 1).coerceAtLeast(0)
                binding.tvTotalSeats.text = remainingSeats.toString()
                binding.tvInUseSeats.text = inUseSeats.toString()
            }
            renderSeatMap()
            updateSelectedInfo(animateButton = true)
        }
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }

    companion object {
        private const val MAP_ROWS = 23
        private const val MAP_COLS = 18
        private const val TOTAL_SEATS = 279
        private const val REMAINING_SEATS = 71
        private const val IN_USE_SEATS = 123
        private const val WAITING_COUNT = 0

        private val GAP_ROWS = setOf(1, 13)

        private val AVAILABLE_SEATS = (1..TOTAL_SEATS)
            .filter { seat -> seat % 3 == 2 || seat % 7 == 0 || seat in setOf(53, 64, 65, 66, 67) }
            .take(REMAINING_SEATS)
            .toSet()

        private fun buildLibrarySeatMap(): Array<Array<Int?>> {
            val map = Array(MAP_ROWS) { arrayOfNulls<Int>(MAP_COLS) }

            fun put(row: Int, col: Int, number: Int) {
                if (row in 0 until MAP_ROWS && col in 0 until MAP_COLS) {
                    map[row][col] = number
                }
            }

            fun putSeats(row: Int, vararg seats: Pair<Int, Int>) {
                seats.forEach { (col, number) -> put(row, col, number) }
            }

            putSeats(0, 3 to 5, 4 to 4, 5 to 3, 7 to 2, 8 to 1)

            putSeats(2, 0 to 6, 3 to 13, 4 to 23, 6 to 33, 7 to 43, 10 to 53)
            putSeats(3, 0 to 7, 3 to 14, 4 to 24, 6 to 34, 7 to 44, 10 to 54)
            putSeats(4, 3 to 15, 4 to 25, 6 to 35, 7 to 45, 10 to 55, 11 to 64)

            putSeats(5, 0 to 8, 3 to 16, 4 to 26, 6 to 36, 7 to 46, 10 to 56, 11 to 65, 13 to 71, 14 to 79)
            putSeats(6, 0 to 9, 13 to 72, 14 to 80)
            putSeats(7, 0 to 10, 3 to 17, 4 to 27, 6 to 37, 7 to 47, 10 to 57, 11 to 66, 13 to 73, 14 to 81)
            putSeats(8, 3 to 18, 4 to 28, 6 to 38, 7 to 48, 10 to 58, 11 to 67, 13 to 74, 14 to 82)
            putSeats(9, 3 to 19, 4 to 29, 6 to 39, 7 to 49, 10 to 59, 11 to 68, 13 to 75, 14 to 83)
            putSeats(10, 3 to 20, 4 to 30, 6 to 40, 7 to 50, 13 to 76, 14 to 84)
            putSeats(11, 0 to 11, 3 to 21, 4 to 31, 6 to 41, 7 to 51, 10 to 60, 11 to 69, 13 to 77, 14 to 85)
            putSeats(12, 0 to 12, 3 to 22, 4 to 32, 6 to 42, 7 to 52, 10 to 61, 11 to 70, 13 to 78, 14 to 86)

            val bottomRows = listOf(
                228 to listOf(235, 241, 247, 253, 259, 265, 271, 277),
                229 to listOf(236, 242, 248, 254, 260, 266, 272, 278),
                230 to listOf(237, 243, 249, 255, 261, 267, 273, 279),
                231 to listOf(238, 244, 250, 256, 262, 268),
                232 to listOf(239, 245, 251, 257, 263, 269),
                233 to listOf(240, 246, 252, 258, 264, 270),
                234 to emptyList(),
            )
            bottomRows.forEachIndexed { index, (leftSeat, seats) ->
                val row = 15 + index
                put(row, 0, leftSeat)
                seats.forEachIndexed { seatIndex, number ->
                    val col = 3 + seatIndex + (seatIndex / 2)
                    put(row, col, number)
                }
            }

            return map
        }
    }
}
