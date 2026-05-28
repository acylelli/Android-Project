package com.example.androidapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.androidapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val loginListener = {
            Toast.makeText(this, R.string.toast_login, Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.btnKakaoLogin.setOnClickListener { loginListener() }
        binding.btnAppleLogin.setOnClickListener { loginListener() }
        binding.btnFacebookLogin.setOnClickListener { loginListener() }
        binding.btnGoogleLogin.setOnClickListener { loginListener() }
    }
}
