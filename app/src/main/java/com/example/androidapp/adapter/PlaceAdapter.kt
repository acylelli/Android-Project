package com.example.androidapp.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.androidapp.R
import com.example.androidapp.data.OccupancyLevel
import com.example.androidapp.data.Place
import com.example.androidapp.data.PlaceCategory
import com.example.androidapp.databinding.ItemPlaceBinding

class PlaceAdapter(
    private val onItemClick: (Place) -> Unit,
) : RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder>() {

    private var items: List<Place> = emptyList()

    fun submitList(list: List<Place>) {
        items = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val binding = ItemPlaceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class PlaceViewHolder(
        private val binding: ItemPlaceBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(place: Place) {
            val context = binding.root.context
            binding.tvPlaceName.text = place.name
            binding.tvPlaceCategory.text = place.category.label
            binding.tvPlaceAddress.text = context.getString(
                R.string.place_address_format,
                place.address,
                place.distanceKm,
            )
            binding.tvTotalSeats.text = context.getString(R.string.total_seats_format, place.totalSeats)
            binding.tvUsageRate.text = context.getString(R.string.usage_rate_format, place.occupancyPercent)
            binding.progressOccupancy.progress = place.occupancyPercent

            when (place.occupancy) {
                OccupancyLevel.FULL -> {
                    binding.tvStatusBadge.text = context.getString(R.string.status_full)
                    binding.tvStatusBadge.setBackgroundResource(R.drawable.bg_status_full)
                    binding.tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.status_full_text))
                    binding.progressOccupancy.progressTintList =
                        ColorStateList.valueOf(ContextCompat.getColor(context, R.color.occupancy_full))
                }
                OccupancyLevel.BUSY -> {
                    binding.tvStatusBadge.text = context.getString(R.string.status_remaining, place.emptySeats)
                    binding.tvStatusBadge.setBackgroundResource(R.drawable.bg_status_busy)
                    binding.tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.status_busy_text))
                    binding.progressOccupancy.progressTintList =
                        ColorStateList.valueOf(ContextCompat.getColor(context, R.color.occupancy_busy))
                }
                OccupancyLevel.AVAILABLE -> {
                    binding.tvStatusBadge.text = context.getString(R.string.status_remaining, place.emptySeats)
                    binding.tvStatusBadge.setBackgroundResource(R.drawable.bg_status_available)
                    binding.tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.status_available_text))
                    binding.progressOccupancy.progressTintList =
                        ColorStateList.valueOf(ContextCompat.getColor(context, R.color.occupancy_available))
                }
            }

            bindTags(place.tags)

            // 세미나실, 도서관, 스터디카페만 웨이팅 정보 노출 (카페, 학교 열람실은 선착순이라 제외)
            val canHaveWaiting = place.isSeminar || 
                                place.category == PlaceCategory.LIBRARY || 
                                place.category == PlaceCategory.STUDY_CAFE

            if (canHaveWaiting && place.waiting > 0) {
                binding.tvWaitingInfo.visibility = android.view.View.VISIBLE
                binding.tvWaitingInfo.text = context.getString(R.string.waiting_count_format, place.waiting)
            } else {
                binding.tvWaitingInfo.visibility = android.view.View.GONE
            }

            binding.root.setOnClickListener { onItemClick(place) }
        }

        private fun bindTags(tags: List<String>) {
            binding.layoutTags.removeAllViews()
            val context = binding.root.context
            val marginEnd = (6 * context.resources.displayMetrics.density).toInt()
            val padH = (8 * context.resources.displayMetrics.density).toInt()
            val padV = (3 * context.resources.displayMetrics.density).toInt()

            tags.forEach { tag ->
                val tagView = TextView(context).apply {
                    text = tag
                    setTextColor(ContextCompat.getColor(context, R.color.jari_green))
                    textSize = 11f
                    setBackgroundResource(R.drawable.bg_place_tag)
                    setPadding(padH, padV, padH, padV)
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                    ).apply {
                        this.marginEnd = marginEnd
                    }
                }
                binding.layoutTags.addView(tagView)
            }
        }
    }
}
