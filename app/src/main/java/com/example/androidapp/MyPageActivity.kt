package com.example.androidapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.androidapp.databinding.ActivityMyPageBinding
import com.google.firebase.auth.FirebaseAuth

class MyPageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bindUserProfile()
        setupListeners()
    }

    private fun bindUserProfile() {
        val user = FirebaseAuth.getInstance().currentUser
        binding.tvNickname.text = user?.displayName?.takeIf { it.isNotBlank() } ?: "자리타임 유저"
        binding.tvEmail.text = user?.email?.takeIf { it.isNotBlank() } ?: "jaritime@hansung.ac.kr"
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }
        binding.ivChatbot.setOnClickListener {
            startActivity(Intent(this, HelpBotActivity::class.java))
        }

        binding.menuWaiting.setOnClickListener {
            startActivity(Intent(this, MyWaitingActivity::class.java))
        }

        binding.menuFavorites.setOnClickListener {
            Toast.makeText(this, "즐겨찾는 장소로 이동합니다.", Toast.LENGTH_SHORT).show()
        }

        binding.menuLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }
    }
}
