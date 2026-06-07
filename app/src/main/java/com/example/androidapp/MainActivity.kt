package com.example.androidapp

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidapp.adapter.PlaceAdapter
import com.example.androidapp.data.MockData
import com.example.androidapp.databinding.ActivityMainBinding
import com.google.android.material.chip.Chip

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var placeAdapter: PlaceAdapter
    private var selectedCategoryIndex = 0
    private var isShowingFavorites = false
    private val categoryChips = mutableListOf<Chip>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        try {
            val info = packageManager.getPackageInfo(packageName, android.content.pm.PackageManager.GET_SIGNATURES)
            info.signatures?.forEach { signature ->
                val md = java.security.MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val keyHash = android.util.Base64.encodeToString(md.digest(), android.util.Base64.NO_WRAP)
                android.util.Log.d("KeyHash", "내 키 해시: $keyHash")
            }
        } catch (e: Exception) {
            android.util.Log.e("KeyHash", "키 해시 구하기 실패", e)
        }
        setupRecyclerView()
        setupCategoryChips()
        setupBottomNav()
        setupChatbot()

        filterPlaces()
    }

    private fun setupChatbot() {
        binding.ivChatbot.setOnClickListener {
            android.widget.Toast.makeText(this, "AI 헬프봇: 무엇을 도와드릴까요?", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView() {
        placeAdapter = PlaceAdapter { place ->
            val intent = Intent(this, PlaceDetailActivity::class.java).apply {
                putExtra(AppConstants.EXTRA_PLACE_ID, place.id)
                putExtra(AppConstants.EXTRA_PLACE_NAME, place.name)
                putExtra(AppConstants.EXTRA_IS_SEMINAR, place.isSeminar)
            }
            startActivity(intent)
        }
        binding.rvPlaces.layoutManager = LinearLayoutManager(this)
        binding.rvPlaces.adapter = placeAdapter
    }

    private fun setupCategoryChips() {
        binding.chipGroupCategory.removeAllViews()
        categoryChips.clear()

        MockData.filterChips.forEachIndexed { index, category ->
            val chip = Chip(this).apply {
                text = category.label
                isCheckable = true
                isChecked = index == 0
                chipStrokeWidth = 0f
                textSize = 13f
                setEnsureMinTouchTargetSize(false)
                setOnClickListener {
                    selectedCategoryIndex = index
                    updateChipStyles()
                    filterPlaces()
                }
            }
            categoryChips.add(chip)
            binding.chipGroupCategory.addView(chip)
        }
        updateChipStyles()
    }

    private fun updateChipStyles() {
        val green = ContextCompat.getColor(this, R.color.jari_green)
        val white = ContextCompat.getColor(this, R.color.white)

        categoryChips.forEachIndexed { index, chip ->
            val selected = index == selectedCategoryIndex
            if (selected) {
                chip.setChipBackgroundColor(ColorStateList.valueOf(green))
                chip.setTextColor(white)
            } else {
                chip.setChipBackgroundColor(ColorStateList.valueOf(white))
                chip.setTextColor(green)
            }
        }
    }

    private fun filterPlaces() {
        var list = if (selectedCategoryIndex == 0) {
            MockData.places
        } else {
            val category = MockData.filterChips[selectedCategoryIndex]
            MockData.places.filter { it.category == category }
        }

        if (isShowingFavorites) {
            list = list.filter { it.isFavorite }
        }

        placeAdapter.submitList(list)
    }

    private fun setupBottomNav() {
        binding.navHome.setOnClickListener {
            isShowingFavorites = false
            updateBottomNavStyles()
            filterPlaces()
        }

        binding.navFavorites.setOnClickListener {
            isShowingFavorites = true
            updateBottomNavStyles()
            filterPlaces()
        }

        binding.navMap.setOnClickListener {
            startActivity(Intent(this, MapActivity::class.java))
        }

        binding.navAlert.setOnClickListener {
            startActivity(Intent(this, NotificationActivity::class.java))
        }

        binding.navMy.setOnClickListener {
            startActivity(Intent(this, MyPageActivity::class.java))
        }

        // 초기 상태 설정
        updateBottomNavStyles()
    }

    private fun updateBottomNavStyles() {
        val activeColor = ContextCompat.getColor(this, R.color.jari_green)
        val inactiveColor = ContextCompat.getColor(this, R.color.nav_inactive)
        
        val homeIcon = binding.navHome.getChildAt(0) as ImageView
        val homeText = binding.navHome.getChildAt(1) as TextView
        val favIcon = binding.navFavorites.getChildAt(0) as ImageView
        val favText = binding.navFavorites.getChildAt(1) as TextView
        val alertIcon = binding.navAlert.getChildAt(0) as ImageView
        val alertText = binding.navAlert.getChildAt(1) as TextView
        val myIcon = binding.navMy.getChildAt(0) as ImageView
        val myText = binding.navMy.getChildAt(1) as TextView

        // 알림과 마이는 현재 페이지가 아니므로 항상 비활성 색상 (또는 필요시 확장)
        alertIcon.imageTintList = ColorStateList.valueOf(inactiveColor)
        alertText.setTextColor(inactiveColor)
        myIcon.imageTintList = ColorStateList.valueOf(inactiveColor)
        myText.setTextColor(inactiveColor)

        if (isShowingFavorites) {
            homeIcon.imageTintList = ColorStateList.valueOf(inactiveColor)
            homeText.setTextColor(inactiveColor)
            favIcon.imageTintList = ColorStateList.valueOf(activeColor)
            favText.setTextColor(activeColor)
        } else {
            homeIcon.imageTintList = ColorStateList.valueOf(activeColor)
            homeText.setTextColor(activeColor)
            favIcon.imageTintList = ColorStateList.valueOf(inactiveColor)
            favText.setTextColor(inactiveColor)
        }
    }
}
