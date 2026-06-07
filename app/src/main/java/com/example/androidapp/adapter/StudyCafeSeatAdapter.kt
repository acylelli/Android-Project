package com.example.androidapp.adapter

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.androidapp.data.RoomStatus
import com.example.androidapp.data.StudyCafeSeat
import com.example.androidapp.databinding.ItemSeminarRoomBinding

class StudyCafeSeatAdapter(
    private val onSeatClick: (StudyCafeSeat) -> Unit,
) : RecyclerView.Adapter<StudyCafeSeatAdapter.SeatViewHolder>() {

    private val handler = Handler(Looper.getMainLooper())
    private var seats: List<StudyCafeSeat?> = emptyList()
    private var selectedSeatNumber: Int = 0
    private var reservedSeatNumber: Int? = null
    private var reservedRemainingSeconds: Long = 0L

    private val countdownRunnable = object : Runnable {
        override fun run() {
            seats.filterNotNull().forEach { seat ->
                if (seat.status == RoomStatus.OCCUPIED && seat.remainingSeconds > 0L) {
                    seat.remainingSeconds -= 1L
                }
            }
            if (reservedRemainingSeconds > 0L) {
                reservedRemainingSeconds -= 1L
            }
            notifyDataSetChanged()
            handler.postDelayed(this, 1_000L)
        }
    }

    fun submitList(list: List<StudyCafeSeat?>, selected: Int) {
        seats = list
        selectedSeatNumber = selected
        notifyDataSetChanged()
    }

    fun updateSelection(seatNumber: Int) {
        selectedSeatNumber = seatNumber
        notifyDataSetChanged()
    }

    fun markReservedSeat(seatNumber: Int, durationSeconds: Long) {
        reservedSeatNumber = seatNumber
        reservedRemainingSeconds = durationSeconds
        selectedSeatNumber = seatNumber
        notifyDataSetChanged()
    }

    fun moveReservedSeat(toSeatNumber: Int) {
        reservedSeatNumber = toSeatNumber
        selectedSeatNumber = toSeatNumber
        notifyDataSetChanged()
    }

    fun extendReservedTime(extraSeconds: Long) {
        reservedRemainingSeconds += extraSeconds
        notifyDataSetChanged()
    }

    fun hasReservedSeat(): Boolean = reservedSeatNumber != null

    fun reservedSeatNumber(): Int? = reservedSeatNumber

    fun reservedRemainingSeconds(): Long = reservedRemainingSeconds

    fun isReservedSeat(seatNumber: Int): Boolean = reservedSeatNumber == seatNumber

    fun selectedSeat(): StudyCafeSeat? = seats.filterNotNull().firstOrNull { it.number == selectedSeatNumber }

    fun startCountdown() {
        handler.removeCallbacks(countdownRunnable)
        handler.post(countdownRunnable)
    }

    fun stopCountdown() {
        handler.removeCallbacks(countdownRunnable)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeatViewHolder {
        val binding = ItemSeminarRoomBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SeatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SeatViewHolder, position: Int) {
        holder.bind(seats[position])
    }

    override fun getItemCount(): Int = seats.size

    inner class SeatViewHolder(
        private val binding: ItemSeminarRoomBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(seat: StudyCafeSeat?) {
            val density = binding.root.resources.displayMetrics.density
            val params = binding.tvRoomNumber.layoutParams as ViewGroup.MarginLayoutParams
            params.width = (58 * density).toInt()
            params.height = (56 * density).toInt()
            params.setMargins((3 * density).toInt(), (3 * density).toInt(), (3 * density).toInt(), (3 * density).toInt())
            binding.tvRoomNumber.layoutParams = params

            if (seat == null) {
                binding.tvRoomNumber.visibility = View.INVISIBLE
                binding.tvRoomNumber.background = null
                binding.root.setOnClickListener(null)
                return
            }

            binding.tvRoomNumber.visibility = View.VISIBLE
            binding.tvRoomNumber.gravity = Gravity.CENTER
            binding.tvRoomNumber.textSize = 9.5f
            binding.tvRoomNumber.typeface = Typeface.DEFAULT_BOLD
            binding.tvRoomNumber.text = buildSeatText(seat)
            binding.tvRoomNumber.setTextColor(textColorFor(seat))
            binding.tvRoomNumber.background = backgroundFor(seat, density)
            binding.root.setOnClickListener { onSeatClick(seat) }
        }

        private fun buildSeatText(seat: StudyCafeSeat): String {
            return if (seat.number == reservedSeatNumber) {
                "${seat.number}번\n내 자리\n${formatRemainingTime(reservedRemainingSeconds)}"
            } else if (seat.status == RoomStatus.AVAILABLE) {
                "${seat.number}번\n예약가능"
            } else {
                "${seat.number}번\n${formatRemainingTime(seat.remainingSeconds)}"
            }
        }

        private fun textColorFor(seat: StudyCafeSeat): Int = when {
            seat.number == reservedSeatNumber -> Color.WHITE
            seat.number == selectedSeatNumber -> Color.WHITE
            seat.status == RoomStatus.AVAILABLE -> Color.parseColor("#00927A")
            else -> Color.parseColor("#777777")
        }

        private fun backgroundFor(seat: StudyCafeSeat, density: Float): GradientDrawable {
            return GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 8 * density
                when {
                    seat.number == reservedSeatNumber -> {
                        setColor(Color.parseColor("#222222"))
                        setStroke((1 * density).toInt(), Color.parseColor("#222222"))
                    }
                    seat.number == selectedSeatNumber -> {
                        setColor(Color.parseColor("#00927A"))
                        setStroke((1 * density).toInt(), Color.parseColor("#00927A"))
                    }
                    seat.status == RoomStatus.AVAILABLE -> {
                        setColor(Color.WHITE)
                        setStroke((1 * density).toInt(), Color.parseColor("#D1E9E2"))
                    }
                    else -> {
                        setColor(Color.parseColor("#F5F5F3"))
                        setStroke((1 * density).toInt(), Color.parseColor("#EBEBEB"))
                    }
                }
            }
        }
    }

    private fun formatRemainingTime(totalSeconds: Long): String {
        val hours = totalSeconds / 3_600
        val minutes = (totalSeconds % 3_600) / 60
        val seconds = totalSeconds % 60
        return "%d:%02d:%02d".format(hours, minutes, seconds)
    }
}
