package com.example.androidapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidapp.adapter.NotificationAdapter
import com.example.androidapp.data.NotificationStore
import com.example.androidapp.databinding.ActivityNotificationBinding

class NotificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationBinding
    private lateinit var notificationAdapter: NotificationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        updateEmptyView()

        binding.btnBack.setOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()
        loadNotifications()
    }

    private fun setupRecyclerView() {
        notificationAdapter = NotificationAdapter()
        binding.rvNotifications.layoutManager = LinearLayoutManager(this)
        binding.rvNotifications.adapter = notificationAdapter
        loadNotifications()
    }

    private fun loadNotifications() {
        notificationAdapter.submitList(NotificationStore.getNotifications(this))
        updateEmptyView()
    }

    private fun updateEmptyView() {
        val isEmpty = notificationAdapter.itemCount == 0
        binding.rvNotifications.isVisible = !isEmpty
        binding.tvEmptyNotifications.isVisible = isEmpty
    }
}
