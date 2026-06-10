package com.example.androidapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import com.example.androidapp.databinding.ActivityLoginBinding
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val credentialManager by lazy { CredentialManager.create(this) }
    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mockLoginListener = {
            completeLogin()
        }

        binding.btnKakaoLogin.setOnClickListener {
            Toast.makeText(this, "구글 로그인 버튼을 사용해 주세요.", Toast.LENGTH_SHORT).show()
        }
        binding.btnAppleLogin.setOnClickListener { mockLoginListener() }
        binding.btnFacebookLogin.setOnClickListener { mockLoginListener() }
        binding.btnGoogleLogin.setOnClickListener { loginWithGoogle() }
    }

    private fun loginWithGoogle() {
        val webClientId = getString(R.string.google_web_client_id)
        if (webClientId.startsWith("YOUR_WEB_CLIENT_ID")) {
            Toast.makeText(this, "Google Web Client ID를 먼저 설정해야 합니다.", Toast.LENGTH_LONG).show()
            return
        }

        val googleOption = GetSignInWithGoogleOption.Builder(webClientId)
            .build()
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleOption)
            .build()

        lifecycleScope.launch {
            try {
                val result = credentialManager.getCredential(
                    context = this@LoginActivity,
                    request = request,
                )
                val credential = result.credential
                if (
                    credential is CustomCredential &&
                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {
                    val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    Log.d(TAG, "Google login succeeded: ${googleCredential.id}")
                    signInWithFirebase(googleCredential.idToken)
                } else {
                    Log.e(TAG, "Unexpected Google credential type: ${credential::class.java.name}")
                    Toast.makeText(this@LoginActivity, "구글 로그인 응답을 처리할 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            } catch (error: GoogleIdTokenParsingException) {
                Log.e(TAG, "Invalid Google ID token.", error)
                Toast.makeText(this@LoginActivity, "구글 로그인 토큰이 올바르지 않습니다.", Toast.LENGTH_SHORT).show()
            } catch (error: GetCredentialException) {
                Log.e(TAG, "Google login failed.", error)
                Toast.makeText(this@LoginActivity, "구글 로그인이 취소되었거나 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signInWithFirebase(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener {
                completeLogin()
            }
            .addOnFailureListener { error ->
                Log.e(TAG, "Firebase Google login failed.", error)
                Toast.makeText(this, "Firebase 구글 로그인에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun completeLogin() {
        Toast.makeText(this, R.string.toast_login, Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}
