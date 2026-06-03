package com.example.androidapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
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

        // 바텀시트 동작(Behavior) 초기화
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)
        bottomSheetBehavior.isHideable = true        // 완전히 숨기기 활성화
        bottomSheetBehavior.skipCollapsed = true     // 중간 상태(Collapsed) 건너뛰고 바로 숨겨짐
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED // 처음엔 펼쳐진 상태로 시작

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> { /* 펼쳐졌을 때 처리 */ }
                    BottomSheetBehavior.STATE_COLLAPSED -> { /* 접혔을 때 처리 */ }
                }
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        // [수정] 뒤로가기 버튼이나 시스템 백버튼을 누르면 바텀시트 상태와 상관없이 바로 종료되도록 수정
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        binding.btnBack.setOnClickListener {
            finish()
        }

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

        // UI 데이터 바인딩
        binding.tvPlaceName.text = place.name
        binding.tvPlaceCategory.text = place.category.label
        binding.tvPlaceAddress.text = place.address
        binding.tvOpenStatus.text = "영업중  ${place.hours}"
        binding.tvRating.text = "${place.rating} (리뷰 128개) "
        binding.tvStars.text = "★★★★☆"
        binding.tvEmptyCount.text = place.emptySeats.toString()
        binding.tvInUseCount.text = place.inUse.toString()
        binding.tvHours.text = place.hours
        binding.tvFee.text = place.fee
        binding.tvAmenities.text = place.amenities.joinToString(" · ")
        binding.tvPhotoPlaceholder.setImageResource(place.imageResId)

        // 숫자 색상 분기
        when (place.occupancy) {
            OccupancyLevel.FULL -> {
                binding.tvEmptyCount.setTextColor(ContextCompat.getColor(this, R.color.status_full_text))
                binding.tvInUseCount.setTextColor(ContextCompat.getColor(this, R.color.text_primary))
            }
            OccupancyLevel.BUSY -> {
                binding.tvEmptyCount.setTextColor(ContextCompat.getColor(this, R.color.status_busy_text))
                binding.tvInUseCount.setTextColor(ContextCompat.getColor(this, R.color.text_primary))
            }
            OccupancyLevel.AVAILABLE -> {
                binding.tvEmptyCount.setTextColor(ContextCompat.getColor(this, R.color.pure_black))
                binding.tvInUseCount.setTextColor(ContextCompat.getColor(this, R.color.text_primary))
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