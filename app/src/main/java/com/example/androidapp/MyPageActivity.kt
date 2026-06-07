package com.example.androidapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.androidapp.databinding.ActivityMyPageBinding

class MyPageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.ivChatbot.setOnClickListener {
            Toast.makeText(this, "AI 헬프봇: 계정 및 설정 관련 도움을 드릴까요?", Toast.LENGTH_SHORT).show()
        }

        // 메뉴 클릭 리스너 예시
        binding.menuWaiting.setOnClickListener {
            startActivity(Intent(this, MyWaitingActivity::class.java))
        }

        binding.menuFavorites.setOnClickListener {
            // 즐겨찾기 이동 등
            Toast.makeText(this, "즐겨찾는 장소로 이동합니다.", Toast.LENGTH_SHORT).show()
        }

        binding.menuLogout.setOnClickListener {
            // 로그아웃 로직 (LoginActivity로 이동 등)
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
        
        // 나머지 메뉴들에 대해서도 필요에 따라 리스너 추가 가능
    }
}
