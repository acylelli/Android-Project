package com.example.androidapp

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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

        setupRecyclerView()
        setupCategoryChips()
        setupBottomNav()
        setupChatbot()
        setupSearch()
        filterPlaces()
    }

    private fun setupChatbot() {
        binding.ivChatbot.setOnClickListener {
            startActivity(Intent(this, HelpBotActivity::class.java))
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
            chip.setChipBackgroundColor(ColorStateList.valueOf(if (selected) green else white))
            chip.setTextColor(if (selected) white else green)
        }
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterPlaces()
            }

            override fun afterTextChanged(s: Editable?) = Unit
        })
    }

    private fun filterPlaces() {
        val query = binding.etSearch.text.toString().trim()
        var list = if (selectedCategoryIndex == 0) {
            MockData.places
        } else {
            val category = MockData.filterChips[selectedCategoryIndex]
            MockData.places.filter { it.category == category }
        }

        if (query.isNotBlank()) {
            list = list.filter { place ->
                place.name.contains(query, ignoreCase = true) ||
                    place.category.label.contains(query, ignoreCase = true) ||
                    place.address.contains(query, ignoreCase = true) ||
                    place.tags.any { it.contains(query, ignoreCase = true) } ||
                    place.amenities.any { it.contains(query, ignoreCase = true) }
            }
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
