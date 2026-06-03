package com.example.androidapp.adapter

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.androidapp.R
import com.example.androidapp.data.RoomStatus
import com.example.androidapp.data.SeminarRoom
import com.example.androidapp.databinding.ItemSeminarRoomBinding

class SeminarRoomAdapter(
    private val onRoomClick: (Int) -> Unit,
) : RecyclerView.Adapter<SeminarRoomAdapter.RoomViewHolder>() {

    private var rooms: List<SeminarRoom> = emptyList()
    private var selectedRoom: Int = 107

    fun submitList(list: List<SeminarRoom>, selected: Int) {
        rooms = list
        selectedRoom = selected
        notifyDataSetChanged()
    }

    fun updateSelection(roomNumber: Int) {
        selectedRoom = roomNumber
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

        fun bind(room: SeminarRoom) {
            val context = binding.root.context
            binding.tvRoomNumber.text = "${room.number}호"

            // 사진 스펙에 맞춘 정밀 컬러 정의
            val colorTealMain = android.graphics.Color.parseColor("#00927A")   // 테라색 (선택/잔여)
            val colorAvailableBorder = android.graphics.Color.parseColor("#D1E9E2") // 잔여석 테두리
            val colorInUseFill = android.graphics.Color.parseColor("#F5F5F3")       // 사용중 배경
            val colorInUseBorder = android.graphics.Color.parseColor("#EBEBEB")     // 사용중 테두리
            val colorInUseText = android.graphics.Color.parseColor("#CCCCCC")       // 사용중 텍스트
            val colorMyReservation = android.graphics.Color.parseColor("#222222")   // 내 예약 (검정)

            val drawable = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = (12 * context.resources.displayMetrics.density) // 12dp rounded
                
                when {
                    room.number == selectedRoom -> {
                        // 선택됨: 테라색 배경 + 흰색 텍스트
                        setColor(colorTealMain)
                        setStroke((1 * context.resources.displayMetrics.density).toInt(), colorTealMain)
                        binding.tvRoomNumber.setTextColor(android.graphics.Color.WHITE)
                    }
                    room.status == RoomStatus.AVAILABLE -> {
                        // 잔여: 흰색 배경 + 테라색 테두리 + 테라색 텍스트
                        setColor(android.graphics.Color.WHITE)
                        setStroke((1 * context.resources.displayMetrics.density).toInt(), colorAvailableBorder)
                        binding.tvRoomNumber.setTextColor(colorTealMain)
                    }
                    room.status == RoomStatus.OCCUPIED -> {
                        // 사용중: 연회색 배경 + 연회색 테두리 + 회색 텍스트
                        setColor(colorInUseFill)
                        setStroke((1 * context.resources.displayMetrics.density).toInt(), colorInUseBorder)
                        binding.tvRoomNumber.setTextColor(colorInUseText)
                    }
                    room.status == RoomStatus.UNAVAILABLE -> {
                        // 내 예약: 검정 배경 + 흰색 텍스트
                        setColor(colorMyReservation)
                        setStroke((1 * context.resources.displayMetrics.density).toInt(), colorMyReservation)
                        binding.tvRoomNumber.setTextColor(android.graphics.Color.WHITE)
                    }
                    else -> {
                        setColor(android.graphics.Color.WHITE)
                        setStroke((1 * context.resources.displayMetrics.density).toInt(), colorInUseBorder)
                        binding.tvRoomNumber.setTextColor(colorInUseText)
                    }
                }
            }
            binding.tvRoomNumber.background = drawable

            binding.root.setOnClickListener {
                if (room.status == RoomStatus.AVAILABLE) {
                    onRoomClick(room.number)
                }
            }
        }
    }
}
