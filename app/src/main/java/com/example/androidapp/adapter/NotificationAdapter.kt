package com.example.androidapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.androidapp.data.NotificationEvent
import com.example.androidapp.databinding.ItemNotificationBinding

class NotificationAdapter : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    private var items: List<NotificationEvent> = emptyList()

    fun submitList(list: List<NotificationEvent>) {
        items = list
        notifyDataSetChanged()
    }

    fun addItem(event: NotificationEvent) {
        items = listOf(event) + items
        notifyItemInserted(0)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class NotificationViewHolder(
        private val binding: ItemNotificationBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(event: NotificationEvent) {
            binding.tvNotificationTime.text = event.time
            binding.tvNotificationMessage.text = event.message
            binding.tvNotificationDetail.text = event.detail
        }
    }
}
