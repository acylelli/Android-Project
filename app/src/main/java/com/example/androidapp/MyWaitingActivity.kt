package com.example.androidapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.androidapp.data.MockData
import com.example.androidapp.databinding.ActivityMyWaitingBinding

class MyWaitingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyWaitingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyWaitingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val info = MockData.waitingInfo
        binding.tvWaitingPlace.text = "${info.placeName}\n${info.roomLabel}"
        binding.tvQueuePosition.text = getString(R.string.queue_position_format, info.queuePosition)
        binding.tvEstimatedWait.text = getString(R.string.estimated_minutes_format, info.estimatedMinutes)
        binding.tvPlannedUse.text = getString(R.string.planned_hours_format, info.plannedHours)

        binding.btnBack.setOnClickListener { finish() }

        binding.ivChatbot.setOnClickListener {
            Toast.makeText(this, "AI 헬프봇: 대기 현황을 안내해 드립니다.", Toast.LENGTH_SHORT).show()
        }

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
}
