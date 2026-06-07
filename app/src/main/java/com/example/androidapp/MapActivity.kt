package com.example.androidapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.androidapp.databinding.ActivityMapBinding

class MapActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMapBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.ivChatbot.setOnClickListener {
            Toast.makeText(this, "AI 헬프봇: 지도를 통해 주변 장소를 확인해 보세요.", Toast.LENGTH_SHORT).show()
        }
    }
}
