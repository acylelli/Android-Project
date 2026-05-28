package com.example.androidapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.androidapp.adapter.SeminarRoomAdapter
import com.example.androidapp.data.MockData
import com.example.androidapp.databinding.ActivitySeminarRoomBinding
import com.google.android.material.chip.Chip

class SeminarRoomActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySeminarRoomBinding
    private lateinit var roomAdapter: SeminarRoomAdapter

    private var selectedRoom = 107
    private var selectedDate = "수 21"
    private var selectedTime = "14:00"

    private val dates = listOf("월 19", "화 20", "수 21", "목 22", "금 23")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySeminarRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }

        roomAdapter = SeminarRoomAdapter { roomNumber ->
            selectedRoom = roomNumber
            roomAdapter.updateSelection(roomNumber)
            updateSummary()
        }
        binding.rvRooms.layoutManager = GridLayoutManager(this, 5)
        binding.rvRooms.adapter = roomAdapter
        roomAdapter.submitList(MockData.seminarRooms(), selectedRoom)

        setupDateChips()
        setupTimeChips()
        updateSummary()

        binding.btnRegisterWaiting.setOnClickListener {
            startService(Intent(this, WaitingMonitorService::class.java))
            startActivity(Intent(this, MyWaitingActivity::class.java))
        }
    }

    private fun setupDateChips() {
        binding.chipGroupDate.removeAllViews()
        dates.forEach { date ->
            val chip = Chip(this).apply {
                text = date
                isCheckable = true
                isChecked = date == selectedDate
                setOnClickListener {
                    selectedDate = date
                    updateSummary()
                }
            }
            binding.chipGroupDate.addView(chip)
        }
    }

    private fun setupTimeChips() {
        binding.chipGroupTime.removeAllViews()
        MockData.timeSlots.forEach { slot ->
            val chip = Chip(this).apply {
                text = slot
                isCheckable = true
                isChecked = slot == selectedTime
                setOnClickListener {
                    selectedTime = slot
                    updateSummary()
                }
            }
            binding.chipGroupTime.addView(chip)
        }
    }

    private fun updateSummary() {
        binding.tvSelectedRoom.text = getString(R.string.room_number_format, selectedRoom)
        binding.tvSelectedDateTime.text = "$selectedDate · $selectedTime"
    }
}
