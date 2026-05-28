package com.example.androidapp

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
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

        filterPlaces()
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
        val list = if (selectedCategoryIndex == 0) {
            MockData.places
        } else {
            val category = MockData.filterChips[selectedCategoryIndex]
            MockData.places.filter { it.category == category }
        }
        placeAdapter.submitList(list)
    }

    private fun setupBottomNav() {
        binding.navAlert.setOnClickListener {
            startActivity(Intent(this, MyWaitingActivity::class.java))
        }
    }
}
