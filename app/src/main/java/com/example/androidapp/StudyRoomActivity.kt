package com.example.androidapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.androidapp.adapter.SeminarRoomAdapter
import com.example.androidapp.data.MockData
import com.example.androidapp.data.NotificationStore
import com.example.androidapp.databinding.ActivityStudyRoomBinding

class StudyRoomActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStudyRoomBinding
    private lateinit var roomAdapter: SeminarRoomAdapter
    private var selectedSeat: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudyRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvTitle.text = "좌석 선택"
        setupRecyclerView()

        binding.btnBack.setOnClickListener { finish() }
        binding.btnRegister.setOnClickListener {
            NotificationStore.saveSeatAlert(this, selectedSeat)
            startActivity(Intent(this, NotificationActivity::class.java))
        }
    }

    private fun setupRecyclerView() {
        roomAdapter = SeminarRoomAdapter(isSeatMode = true) { seatNumber ->
            selectedSeat = seatNumber
            roomAdapter.updateSelection(seatNumber)
            updateSelectedSeatInfo()
        }

        binding.rvSeats.layoutManager = GridLayoutManager(this, SEAT_SPAN_COUNT)
        binding.rvSeats.adapter = roomAdapter
        roomAdapter.submitList(MockData.studyRoomSeats(), selectedSeat)
        updateSelectedSeatInfo()
    }

    private fun updateSelectedSeatInfo() {
        binding.tvSelectedSeatInfo.text = "창의열람실 ${selectedSeat}번 좌석"
        binding.tvSelectedActionInfo.text = "사용 중인 좌석도 선택할 수 있습니다. 자리가 나면 알림을 보내드려요."
        binding.tvRegisterText.text = "선택한 자리 알림받기"
    }

    companion object {
        private const val SEAT_SPAN_COUNT = 8
    }
}
