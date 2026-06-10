package com.example.androidapp

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidapp.adapter.PlaceAdapter
import com.example.androidapp.data.MockData
import com.example.androidapp.data.Place
import com.example.androidapp.data.PlaceCategory
import com.example.androidapp.databinding.ActivityMapBinding
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Marker

class MapActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMapBinding
    private lateinit var placeAdapter: PlaceAdapter
    private lateinit var chipViews: Map<PlaceCategory?, TextView>
    private var selectedCategory: PlaceCategory? = null
    private var naverMap: NaverMap? = null
    private val markers = mutableListOf<Marker>()

    private val nearbyCategories = setOf(
        PlaceCategory.READING_ROOM,
        PlaceCategory.CAFE,
        PlaceCategory.LIBRARY,
        PlaceCategory.SCHOOL_STUDY,
        PlaceCategory.STUDY_CAFE,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.mapView.onCreate(savedInstanceState)

        binding.btnBack.setOnClickListener { finish() }
        binding.ivChatbot.setOnClickListener {
            startActivity(Intent(this, HelpBotActivity::class.java))
        }

        setupRecyclerView()
        setupFilters()
        setupNaverMap()
        submitNearbyPlaces()
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        binding.mapView.onPause()
        super.onPause()
    }

    override fun onStop() {
        binding.mapView.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        binding.mapView.onDestroy()
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    private fun setupRecyclerView() {
        placeAdapter = PlaceAdapter { place -> openPlaceDetail(place) }
        binding.rvNearbyPlaces.layoutManager = LinearLayoutManager(this)
        binding.rvNearbyPlaces.adapter = placeAdapter
    }

    private fun setupFilters() {
        chipViews = mapOf(
            null to binding.chipAll,
            PlaceCategory.CAFE to binding.chipCafe,
            PlaceCategory.LIBRARY to binding.chipLibrary,
            PlaceCategory.STUDY_CAFE to binding.chipStudyCafe,
            PlaceCategory.READING_ROOM to binding.chipReadingRoom,
            PlaceCategory.SCHOOL_STUDY to binding.chipSchoolStudy,
        )

        chipViews.forEach { (category, chip) ->
            chip.setOnClickListener { selectCategory(category) }
        }

        updateChipStyles()
    }

    private fun setupNaverMap() {
        binding.mapView.getMapAsync { map ->
            naverMap = map.apply {
                minZoom = 12.0
                maxZoom = 18.0
                uiSettings.isLocationButtonEnabled = false
                uiSettings.isZoomControlEnabled = true
                uiSettings.isCompassEnabled = false
                uiSettings.isScaleBarEnabled = false
                moveCamera(CameraUpdate.scrollAndZoomTo(PlaceMapLocations.hansungUniversity, 14.7))
            }
            renderMarkers()
        }
    }

    private fun selectCategory(category: PlaceCategory?) {
        selectedCategory = category
        updateChipStyles()
        submitNearbyPlaces()
        renderMarkers()
    }

    private fun submitNearbyPlaces() {
        val places = filteredPlaces()
        placeAdapter.submitList(places)

        binding.tvNearbySubtitle.text = when (selectedCategory) {
            PlaceCategory.CAFE -> "주변 카페 ${places.size}곳"
            PlaceCategory.LIBRARY -> "주변 도서관 ${places.size}곳"
            PlaceCategory.STUDY_CAFE -> "주변 스터디카페 ${places.size}곳"
            PlaceCategory.READING_ROOM -> "주변 독서실 ${places.size}곳"
            PlaceCategory.SCHOOL_STUDY -> "학교 스터디 공간 ${places.size}곳"
            else -> "성북구 주변 공부 장소 ${places.size}곳을 거리순으로 보여드려요."
        }
    }

    private fun filteredPlaces(): List<Place> {
        return MockData.places
            .filter { it.category in nearbyCategories }
            .filter { selectedCategory == null || it.category == selectedCategory }
            .sortedBy { it.distanceKm }
    }

    private fun renderMarkers() {
        val map = naverMap ?: return
        val markerColor = ContextCompat.getColor(this, R.color.jari_green)
        markers.forEach { it.map = null }
        markers.clear()

        filteredPlaces().forEach { place ->
            val marker = Marker().apply {
                position = PlaceMapLocations.byPlaceId(place.id)
                captionText = place.name
                subCaptionText = place.category.label
                captionTextSize = 12f
                iconTintColor = markerColor
                setOnClickListener {
                    openPlaceDetail(place)
                    true
                }
                this.map = map
            }
            markers.add(marker)
        }

        val target = filteredPlaces().firstOrNull()?.let { PlaceMapLocations.byPlaceId(it.id) }
            ?: PlaceMapLocations.hansungUniversity
        map.moveCamera(CameraUpdate.scrollAndZoomTo(target, if (selectedCategory == null) 14.7 else 15.3))
    }

    private fun openPlaceDetail(place: Place) {
        val intent = Intent(this, PlaceDetailActivity::class.java).apply {
            putExtra(AppConstants.EXTRA_PLACE_ID, place.id)
            putExtra(AppConstants.EXTRA_PLACE_NAME, place.name)
            putExtra(AppConstants.EXTRA_IS_SEMINAR, place.isSeminar)
        }
        startActivity(intent)
    }

    private fun updateChipStyles() {
        val selectedText = ContextCompat.getColor(this, R.color.white)
        val normalText = ContextCompat.getColor(this, R.color.jari_green)

        chipViews.forEach { (category, chip) ->
            val isSelected = category == selectedCategory
            chip.setBackgroundResource(
                if (isSelected) R.drawable.bg_chip_selected else R.drawable.bg_chip_default
            )
            chip.setTextColor(if (isSelected) selectedText else normalText)
        }
    }
}
