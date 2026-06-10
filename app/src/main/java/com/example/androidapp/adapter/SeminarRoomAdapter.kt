package com.example.androidapp.adapter

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.androidapp.data.RoomStatus
import com.example.androidapp.data.SeminarRoom
import com.example.androidapp.databinding.ItemSeminarRoomBinding

class SeminarRoomAdapter(
    private val isSeatMode: Boolean = false,
    private val onRoomClick: (Int) -> Unit,
) : RecyclerView.Adapter<SeminarRoomAdapter.RoomViewHolder>() {

    private var rooms: List<SeminarRoom?> = emptyList()
    private var selectedRoom: Int = 102
    private var reservedRoom: Int? = null

    fun submitList(list: List<SeminarRoom>, selected: Int) {
        rooms = if (isSeatMode) buildStudyRoomSeatLayout(list) else buildSeminarRoomLayout(list)
        selectedRoom = selected
        notifyDataSetChanged()
    }

    fun updateSelection(roomNumber: Int) {
        selectedRoom = roomNumber
        notifyDataSetChanged()
    }

    fun updateReservation(roomNumber: Int?) {
        reservedRoom = roomNumber
        if (roomNumber != null) {
            selectedRoom = roomNumber
        }
        notifyDataSetChanged()
    }

    fun markAvailable(roomNumber: Int) {
        rooms = rooms.map { room ->
            if (room?.number == roomNumber) {
                room.copy(status = RoomStatus.AVAILABLE)
            } else {
                room
            }
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val binding = ItemSeminarRoomBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RoomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        holder.bind(rooms[position])
    }

    override fun getItemCount(): Int = rooms.size

    inner class RoomViewHolder(
        private val binding: ItemSeminarRoomBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(room: SeminarRoom?) {
            val density = binding.root.context.resources.displayMetrics.density
            val suffix = if (isSeatMode) "번" else "호"

            binding.tvRoomNumber.text = room?.let { "${it.number}$suffix" } ?: ""

            val marginDp = if (isSeatMode) 2 else 3
            val fontSize = if (isSeatMode) 11f else 12f
            val cornerRadiusDp = if (isSeatMode) 8 else 10
            val layoutParams = binding.tvRoomNumber.layoutParams as ViewGroup.MarginLayoutParams
            val marginPx = (marginDp * density).toInt()

            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            layoutParams.height = ((if (isSeatMode) 38 else 46) * density).toInt()
            layoutParams.setMargins(marginPx, marginPx, marginPx, marginPx)
            binding.tvRoomNumber.layoutParams = layoutParams
            binding.tvRoomNumber.textSize = fontSize

            if (room == null) {
                binding.tvRoomNumber.visibility = View.INVISIBLE
                binding.tvRoomNumber.background = null
                binding.root.setOnClickListener(null)
                return
            }

            binding.tvRoomNumber.visibility = View.VISIBLE

            val colorTealMain = android.graphics.Color.parseColor("#00927A")
            val colorAvailableBorder = android.graphics.Color.parseColor("#D1E9E2")
            val colorInUseFill = android.graphics.Color.parseColor("#F5F5F3")
            val colorInUseBorder = android.graphics.Color.parseColor("#EBEBEB")
            val colorInUseText = android.graphics.Color.parseColor("#CCCCCC")
            val colorMyReservation = android.graphics.Color.parseColor("#222222")

            val drawable = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = cornerRadiusDp * density

                when {
                    room.number == reservedRoom -> {
                        setColor(colorMyReservation)
                        setStroke((1 * density).toInt(), colorMyReservation)
                        binding.tvRoomNumber.setTextColor(android.graphics.Color.WHITE)
                    }

                    room.number == selectedRoom -> {
                        setColor(colorTealMain)
                        setStroke((1 * density).toInt(), colorTealMain)
                        binding.tvRoomNumber.setTextColor(android.graphics.Color.WHITE)
                    }

                    room.status == RoomStatus.AVAILABLE -> {
                        setColor(android.graphics.Color.WHITE)
                        setStroke((1 * density).toInt(), colorAvailableBorder)
                        binding.tvRoomNumber.setTextColor(colorTealMain)
                    }

                    room.status == RoomStatus.OCCUPIED -> {
                        setColor(colorInUseFill)
                        setStroke((1 * density).toInt(), colorInUseBorder)
                        binding.tvRoomNumber.setTextColor(colorInUseText)
                    }

                    room.status == RoomStatus.UNAVAILABLE -> {
                        setColor(colorMyReservation)
                        setStroke((1 * density).toInt(), colorMyReservation)
                        binding.tvRoomNumber.setTextColor(android.graphics.Color.WHITE)
                    }

                    else -> {
                        setColor(android.graphics.Color.WHITE)
                        setStroke((1 * density).toInt(), colorInUseBorder)
                        binding.tvRoomNumber.setTextColor(colorInUseText)
                    }
                }
            }

            binding.tvRoomNumber.background = drawable
            binding.root.setOnClickListener {
                if (room.number != reservedRoom && (isSeatMode || room.status == RoomStatus.AVAILABLE)) {
                    onRoomClick(room.number)
                }
            }
        }
    }

    private fun buildSeminarRoomLayout(list: List<SeminarRoom>): List<SeminarRoom?> {
        val roomsByNumber = list.associateBy { it.number }
        val rows = listOf(
            listOf(101, null, 107),
            listOf(102, null, 108),
            listOf(103, null, 109),
            listOf(104, null, 110),
            listOf(105, null, 111),
            listOf(106, null, 112),
        )

        return rows.flatten().map { number -> number?.let { roomsByNumber[it] } }
    }

    private fun buildStudyRoomSeatLayout(list: List<SeminarRoom>): List<SeminarRoom?> {
        val seatsByNumber = list.associateBy { it.number }
        val rows = listOf(
            listOf(1, 2, null, 17, 18, null, 33, 34),
            listOf(3, 4, null, 19, 20, null, 35, 36),
            listOf(5, 6, null, 21, 22, null, 37, 38),
            listOf(7, 8, null, 23, 24, null, 39, 40),
            listOf(null, null, null, null, null, null, null, null),
            listOf(9, 10, null, 25, 26, null, 41, 42),
            listOf(11, 12, null, 27, 28, null, 43, 44),
            listOf(13, 14, null, 29, 30, null, 45, 46),
            listOf(15, 16, null, 31, 32, null, 47, 48),
            listOf(null, null, null, null, null, null, 49, 50),
        )

        return rows.flatten().map { number -> number?.let { seatsByNumber[it] } }
    }
}
