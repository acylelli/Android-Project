package com.example.androidapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidapp.adapter.NotificationAdapter
import com.example.androidapp.data.MockData
import com.example.androidapp.databinding.ActivityMyWaitingBinding

class MyWaitingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyWaitingBinding
    private lateinit var notificationAdapter: NotificationAdapter

    private val waitingReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val message = intent?.getStringExtra(AppConstants.EXTRA_UPDATE_MESSAGE) ?: return
            Toast.makeText(this@MyWaitingActivity, message, Toast.LENGTH_SHORT).show()
            notificationAdapter.addItem(
                com.example.androidapp.data.NotificationEvent(
                    time = "방금",
                    message = message,
                ),
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyWaitingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val info = MockData.waitingInfo
        binding.tvWaitingPlace.text = "${info.placeName}\n${info.roomLabel}"
        binding.tvQueuePosition.text = getString(R.string.queue_position_format, info.queuePosition)
        binding.tvEstimatedWait.text = getString(R.string.estimated_minutes_format, info.estimatedMinutes)
        binding.tvPlannedUse.text = getString(R.string.planned_hours_format, info.plannedHours)

        notificationAdapter = NotificationAdapter()
        binding.rvNotifications.layoutManager = LinearLayoutManager(this)
        binding.rvNotifications.adapter = notificationAdapter
        notificationAdapter.submitList(MockData.notificationHistory)

        binding.btnBack.setOnClickListener { finish() }
        binding.btnFindOther.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
            finish()
        }
        binding.btnCancelWaiting.setOnClickListener {
            stopService(Intent(this, WaitingMonitorService::class.java))
            Toast.makeText(this, R.string.toast_waiting_cancelled, Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter(AppConstants.ACTION_WAITING_UPDATE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(waitingReceiver, filter, RECEIVER_NOT_EXPORTED)
        } else {
            @Suppress("DEPRECATION")
            registerReceiver(waitingReceiver, filter)
        }
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(waitingReceiver)
    }
}
