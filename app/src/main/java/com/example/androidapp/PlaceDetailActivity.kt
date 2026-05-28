package com.example.androidapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.androidapp.data.MockData
import com.example.androidapp.data.OccupancyLevel
import com.example.androidapp.databinding.ActivityPlaceDetailBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior

class PlaceDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlaceDetailBinding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaceDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val placeId = intent.getStringExtra(AppConstants.EXTRA_PLACE_ID) ?: return finish()
        val place = MockData.placeById(placeId) ?: return finish()
        val isSeminar = intent.getBooleanExtra(AppConstants.EXTRA_IS_SEMINAR, place.isSeminar)

        // [수정] 내리고 올릴 수만 있는 바텀시트 설정
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)

        bottomSheetBehavior.isHideable = false       // 아무리 내려도 완전히 숨겨지지 않음 (창 안 나가짐)
        bottomSheetBehavior.skipCollapsed = false    // 내렸을 때 바닥에 걸치는 상태(Collapsed)를 사용함

        // 처음 화면이 켜졌을 때 완전히 펼쳐진 상태로 시작하고 싶다면 아래 줄 유지,
        // 접힌 상태로 시작하고 싶다면 STATE_COLLAPSED로 변경하세요.
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        // [수정] 바텀시트 상태 변화 감지 리스너 (닫기 기능 제거)
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        // 완전히 위로 펼쳐졌을 때의 처리
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        // 아래로 내려서 바닥에 걸쳐있을 때의 처리
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // 슬라이드 애니메이션 필요 시 작성
            }
        })

        // 지도 (현재 주석 처리됨)
//        binding.mapView.start(
//            object : MapLifeCycleCallback() {
//                override fun onMapDestroy() {}
//                override fun onMapError(error: Exception) {}
//            },
//            object : KakaoMapReadyCallback() {
//                override fun onMapReady(kakaoMap: KakaoMap) {
//                    kakaoMap.moveCamera(
//                        CameraUpdateFactory.newCenterPosition(
//                            LatLng.from(37.5826, 127.0105)
//                        )
//                    )
//                }
//            }
//        )

        binding.btnBack.setOnClickListener { finish() }
        binding.tvPlaceName.text = place.name
        binding.tvPlaceCategory.text = place.category.label
        binding.tvPlaceAddress.text = place.address
        binding.tvOpenStatus.text = "영업중  ${place.hours}"
        binding.tvRating.text = "${place.rating} (리뷰 128개) ★★★★☆"
        binding.tvEmptyCount.text = place.emptySeats.toString()
        binding.tvInUseCount.text = place.inUse.toString()
        binding.tvWaitingCount.text = place.waiting.toString()
        binding.tvHours.text = place.hours
        binding.tvFee.text = place.fee
        binding.tvAmenities.text = place.amenities.joinToString(" · ")

        // 숫자 색상 분기
        when (place.occupancy) {
            OccupancyLevel.FULL -> {
                binding.tvEmptyCount.setTextColor(ContextCompat.getColor(this, R.color.status_full_text))
                binding.tvInUseCount.setTextColor(ContextCompat.getColor(this, R.color.text_primary))
                binding.tvWaitingCount.setTextColor(ContextCompat.getColor(this, R.color.status_busy_text))
            }
            OccupancyLevel.BUSY -> {
                binding.tvEmptyCount.setTextColor(ContextCompat.getColor(this, R.color.status_busy_text))
                binding.tvInUseCount.setTextColor(ContextCompat.getColor(this, R.color.text_primary))
                binding.tvWaitingCount.setTextColor(ContextCompat.getColor(this, R.color.status_busy_text))
            }
            OccupancyLevel.AVAILABLE -> {
                binding.tvEmptyCount.setTextColor(ContextCompat.getColor(this, R.color.jari_green))
                binding.tvInUseCount.setTextColor(ContextCompat.getColor(this, R.color.text_primary))
                binding.tvWaitingCount.setTextColor(ContextCompat.getColor(this, R.color.status_busy_text))
            }
        }

        if (isSeminar) {
            binding.btnNotify.visibility = View.GONE
            binding.btnFindOther.visibility = View.GONE
            binding.layoutSeminarButtons.visibility = View.VISIBLE
            binding.btnSelectSeminar.setOnClickListener {
                startActivity(Intent(this, SeminarRoomActivity::class.java))
            }
            binding.btnRegisterWaitingSeminar.setOnClickListener {
                openWaitingScreen()
            }
        } else {
            binding.layoutSeminarButtons.visibility = View.GONE
            binding.btnNotify.setOnClickListener { openWaitingScreen() }
            binding.btnFindOther.setOnClickListener { finish() }
        }
    }

    private fun openWaitingScreen() {
        startService(Intent(this, WaitingMonitorService::class.java))
        startActivity(Intent(this, MyWaitingActivity::class.java))
    }
}