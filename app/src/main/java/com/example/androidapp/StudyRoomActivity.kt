package com.example.androidapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.androidapp.adapter.StudyCafeSeatAdapter
import com.example.androidapp.data.MockData
import com.example.androidapp.data.NotificationStore
import com.example.androidapp.data.RoomStatus
import com.example.androidapp.data.StudyCafeSeat
import com.example.androidapp.databinding.ActivityStudyRoomBinding

class StudyRoomActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStudyRoomBinding
    private lateinit var seatAdapter: StudyCafeSeatAdapter
    private var selectedSeatNumber: Int = 7

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudyRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val placeId = intent.getStringExtra(AppConstants.EXTRA_PLACE_ID)
        val place = placeId?.let { MockData.placeById(it) }
        binding.tvTitle.text = if (place?.category == com.example.androidapp.data.PlaceCategory.STUDY_CAFE) {
            "스터디카페 좌석"
        } else {
            "좌석 선택"
        }

        setupRecyclerView()

        binding.btnBack.setOnClickListener { finish() }
        binding.btnRegister.setOnClickListener {
            val selectedSeat = seatAdapter.selectedSeat() ?: return@setOnClickListener
            if (selectedSeat.status == RoomStatus.AVAILABLE) {
                NotificationStore.saveReservationAlert(
                    context = this,
                    placeName = place?.name ?: "스터디카페",
                    room = "${selectedSeat.number}번 좌석",
                    startTime = "즉시",
                )
            } else {
                NotificationStore.saveSeatAlert(this, selectedSeat.number)
            }
            startActivity(Intent(this, NotificationActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        seatAdapter.startCountdown()
    }

    override fun onStop() {
        seatAdapter.stopCountdown()
        super.onStop()
    }

    private fun setupRecyclerView() {
        val seats = MockData.studyCafeSeatLayout()
        selectedSeatNumber = seats.filterNotNull()
            .firstOrNull { it.status == RoomStatus.AVAILABLE }
            ?.number ?: 1

        seatAdapter = StudyCafeSeatAdapter { seat ->
            selectedSeatNumber = seat.number
            seatAdapter.updateSelection(seat.number)
            updateSelectedSeatInfo(seat)
        }

        binding.rvSeats.layoutManager = GridLayoutManager(this, SEAT_SPAN_COUNT)
        binding.rvSeats.adapter = seatAdapter
        seatAdapter.submitList(seats, selectedSeatNumber)
        seatAdapter.selectedSeat()?.let(::updateSelectedSeatInfo)
    }

    private fun updateSelectedSeatInfo(seat: StudyCafeSeat) {
        binding.tvSelectedSeatInfo.text = "${seat.number}번 ${seat.zone.label} 좌석"
        if (seat.status == RoomStatus.AVAILABLE) {
            binding.tvSelectedActionInfo.text = "지금 예약할 수 있는 좌석입니다."
            binding.tvRegisterText.text = "선택한 좌석 예약하기"
        } else {
            binding.tvSelectedActionInfo.text = "사용 중인 좌석입니다. 자리가 나면 알림을 받을 수 있습니다."
            binding.tvRegisterText.text = "선택한 자리 웨이팅하기"
        }
    }

    companion object {
        private const val SEAT_SPAN_COUNT = 18
    }
}
