package com.example.androidapp

import android.content.Intent
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLibrarySeatsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val placeId = intent.getStringExtra(AppConstants.EXTRA_PLACE_ID)
        val place = MockData.placeById(placeId.orEmpty())
        val placeName = place?.name ?: "도서관"
        val remainingSeats = place?.emptySeats ?: REMAINING_SEATS
        val inUseSeats = place?.inUse ?: IN_USE_SEATS
        val waitingCount = place?.waiting ?: WAITING_COUNT

        binding.tvTitle.text = "$placeName 좌석"
        binding.tvTotalSeats.text = remainingSeats.toString()
        binding.tvInUseSeats.text = inUseSeats.toString()
        binding.tvAvailableSeats.text = waitingCount.toString()

        binding.btnBack.setOnClickListener { finish() }
        binding.btnNotifyLibrarySeat.setOnClickListener {
            NotificationStore.saveLibrarySeatAlert(this, placeName, selectedSeat)
            startActivity(Intent(this, NotificationActivity::class.java))
        }

        renderSeatMap()
        updateSelectedInfo()
    }

    private fun renderSeatMap() {
        binding.gridLibrarySeats.removeAllViews()
        binding.gridLibrarySeats.columnCount = MAP_COLS

        for (row in 0 until MAP_ROWS) {
            for (col in 0 until MAP_COLS) {
                val number = seatMap[row][col]
                val view = if (number == null) {
                    Space(this).apply { layoutParams = cellLayoutParams(row, col) }
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
            layoutParams = cellLayoutParams(row, col)
            setOnClickListener {
                selectedSeat = number
                renderSeatMap()
                updateSelectedInfo()
            }
        }
    }

    private fun updateSelectedInfo() {
        binding.tvSelectedInfo.text = "선택 좌석: ${selectedSeat}번"
        binding.tvSelectedDetail.text = "사용 중인 좌석도 선택할 수 있습니다. 자리가 나면 알림을 보내드려요."
        binding.tvNotifyLibrarySeat.text = "${selectedSeat}번 좌석 알림받기"
    }

    private fun cellLayoutParams(row: Int, col: Int): GridLayout.LayoutParams {
        val density = resources.displayMetrics.density
        return GridLayout.LayoutParams(GridLayout.spec(row), GridLayout.spec(col)).apply {
            width = (34 * density).toInt()
            height = (34 * density).toInt()
            setMargins((4 * density).toInt(), (4 * density).toInt(), (4 * density).toInt(), (4 * density).toInt())
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
        return if (number in AVAILABLE_SEATS) RoomStatus.AVAILABLE else RoomStatus.OCCUPIED
    }

    companion object {
        private const val MAP_ROWS = 18
        private const val MAP_COLS = 29
        private const val TOTAL_SEATS = 174
        private const val REMAINING_SEATS = 71
        private const val IN_USE_SEATS = 123
        private const val WAITING_COUNT = 0

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

            fun putRow(row: Int, startCol: Int, numbers: IntProgression) {
                numbers.forEachIndexed { index, number -> put(row, startCol + index, number) }
            }

            val leftTopStarts = listOf(1, 31, 61, 91, 121)
            val leftBottomStarts = listOf(30, 60, 90, 120, 150)
            val rightTopStarts = listOf(7, 37, 67, 97, 127)
            val rightBottomStarts = listOf(24, 54, 84, 114, 144)

            for (block in 0..4) {
                val row = block * 3
                putRow(row, 0, leftTopStarts[block]..leftTopStarts[block] + 5)
                putRow(row + 1, 0, leftBottomStarts[block] downTo leftBottomStarts[block] - 5)
                putRow(row, 8, rightTopStarts[block]..rightTopStarts[block] + 8)
                putRow(row + 1, 8, rightBottomStarts[block] downTo rightBottomStarts[block] - 8)
            }

            val bottomBlocks = listOf(
                172 to 14,
                166 to 18,
                160 to 22,
                154 to 26,
            )
            bottomBlocks.forEach { (start, col) ->
                put(15, col, start)
                put(15, col + 1, start - 1)
                put(16, col, start + 1)
                put(16, col + 1, start - 2)
                put(17, col, start + 2)
                put(17, col + 1, start - 3)
            }

            return map
        }
    }
}
