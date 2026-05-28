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
            binding.tvRoomNumber.text = room.number.toString()

            val bgColor = when {
                room.number == selectedRoom -> R.color.room_selected
                room.status == RoomStatus.AVAILABLE -> R.color.jari_green
                room.status == RoomStatus.OCCUPIED -> R.color.room_occupied
                room.status == RoomStatus.UNAVAILABLE -> R.color.room_unavailable
                room.status == RoomStatus.SELECTED -> R.color.room_selected
                else -> R.color.surface_gray
            }
            val textColor = if (room.status == RoomStatus.UNAVAILABLE && room.number != selectedRoom) {
                R.color.text_secondary
            } else {
                R.color.white
            }

            val drawable = GradientDrawable().apply {
                cornerRadius = 24f
                setColor(ContextCompat.getColor(context, bgColor))
            }
            binding.tvRoomNumber.background = drawable
            binding.tvRoomNumber.setTextColor(ContextCompat.getColor(context, textColor))

            binding.root.setOnClickListener {
                if (room.status == RoomStatus.AVAILABLE || room.status == RoomStatus.SELECTED) {
                    onRoomClick(room.number)
                }
            }
        }
    }
}
