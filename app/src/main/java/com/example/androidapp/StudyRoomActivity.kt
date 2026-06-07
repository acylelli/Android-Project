package com.example.androidapp

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

        setupRecyclerView()
        
        binding.btnBack.setOnClickListener { finish() }
        binding.btnRegister.setOnClickListener {
            NotificationStore.saveSeatAlert(this, selectedSeat)
            finish()
            startActivity(android.content.Intent(this, NotificationActivity::class.java))
        }
        
        updateSpanCount()
    }

    private fun setupRecyclerView() {
        roomAdapter = SeminarRoomAdapter(isSeatMode = true) { seatNumber ->
            selectedSeat = seatNumber
            roomAdapter.updateSelection(seatNumber)
        }

        binding.rvSeats.layoutManager = GridLayoutManager(this, SEAT_SPAN_COUNT)
        binding.rvSeats.adapter = roomAdapter
        
        // 50개 좌석 데이터 로드
        roomAdapter.submitList(MockData.studyRoomSeats(), selectedSeat)
    }

    private fun updateSpanCount() {
        binding.rvSeats.layoutManager = GridLayoutManager(this, SEAT_SPAN_COUNT)
    }

    override fun onConfigurationChanged(newConfig: android.content.res.Configuration) {
        super.onConfigurationChanged(newConfig)
        updateSpanCount()
    }

    companion object {
        private const val SEAT_SPAN_COUNT = 8
    }
}
