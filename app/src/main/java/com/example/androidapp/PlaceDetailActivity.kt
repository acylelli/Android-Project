package com.example.androidapp

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.androidapp.data.MockData
import com.example.androidapp.data.OccupancyLevel
import com.example.androidapp.data.PlaceCategory
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

        // 숫자 색상 분기 (잔여석 수에 따른 색상 변경)
        val emptySeats = place.emptySeats
        val colorRes = when {
            emptySeats >= 4 -> R.color.jari_green
            emptySeats == 3 -> R.color.seat_orange
            emptySeats == 2 -> R.color.seat_yellow
            else -> R.color.status_full_text // 빨간색
        }
        binding.tvEmptyCount.setTextColor(ContextCompat.getColor(this, colorRes))
        binding.tvInUseCount.setTextColor(ContextCompat.getColor(this, R.color.text_primary))

        // 대기자 표시 분기: 세미나, 도서관, 스터디카페만 노출
        // (PlaceCategory에 SEMINAR가 따로 없으므로 isSeminar 플래그와 카테고리 병행 체크)
        val showWaiting = place.isSeminar || when (place.category) {
            PlaceCategory.LIBRARY, PlaceCategory.STUDY_CAFE -> true
            else -> false
        }

        if (showWaiting) {
            binding.tvWaitingCount.visibility = View.VISIBLE
            binding.tvWaitingLabel.visibility = View.VISIBLE
            binding.tvWaitingCount.text = place.waiting.toString()
            binding.tvWaitingCount.setTextColor(ContextCompat.getColor(this, R.color.text_primary))
            
            // 3개 요소가 있을 때 chain balance를 위해 제약 조건 재설정 (이미 XML에서 3개일 때를 고려해 chain 설정함)
        } else {
            binding.tvWaitingCount.visibility = View.GONE
            binding.tvWaitingLabel.visibility = View.GONE
            // 2개 요소일 때 정렬을 위해 tvInUseCount의 제약 조건 수정
            val params = binding.tvInUseCount.layoutParams as ConstraintLayout.LayoutParams
            params.endToStart = ConstraintLayout.LayoutParams.UNSET
            params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            binding.tvInUseCount.layoutParams = params
        }

        if (isSeminar) {
            // 세미나실: 전용 버튼 레이아웃 사용 (세미나실 선택 / 다른 장소 찾기)
            binding.layoutGeneralButtons.visibility = View.GONE
            binding.layoutSeminarButtons.visibility = View.VISIBLE
            
            binding.btnSelectSeminar.setOnClickListener {
                startActivity(Intent(this, SeminarRoomActivity::class.java))
            }
            binding.btnFindOtherSeminar.setOnClickListener {
                finish()
            }
        } else {
            // 그 외 모든 장소 (카페, 도서관, 학교 열람실 등): "자리나면 알림받기" 버튼 표시
            binding.layoutSeminarButtons.visibility = View.GONE
            binding.layoutGeneralButtons.visibility = View.VISIBLE
            
            // 버튼 스타일 유지 (빨간색 "자리나면 알림받기")
            binding.btnNotify.text = getString(R.string.notify_when_free)
            binding.btnNotify.setBackgroundResource(R.drawable.bg_cancel_button)
            binding.btnNotify.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FF4B4B"))
            binding.btnNotify.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_nav_alert, 0, 0, 0)
            
            binding.btnNotify.setOnClickListener { openWaitingScreen() }
            binding.btnFindOther.setOnClickListener { finish() }
        }
    }

    private fun openWaitingScreen() {
        startService(Intent(this, WaitingMonitorService::class.java))
        startActivity(Intent(this, MyWaitingActivity::class.java))
    }
}