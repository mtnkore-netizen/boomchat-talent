package com.talent.boomchat.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.talent.boomchat.data.firebase.FirebaseManager
import com.talent.boomchat.databinding.ActivitySplashBinding
import com.talent.boomchat.ui.MainActivity
import com.talent.boomchat.ui.auth.AuthActivity
import com.talent.boomchat.ui.auth.ProfileSetupActivity
import com.talent.boomchat.ui.auth.VerifyEmailActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            // Animate or hold splash for 2 seconds to showcase credentials & app identity
            delay(2000)
            checkAuthAndRoute()
        }
    }

    private suspend fun checkAuthAndRoute() {
        val currentUser = FirebaseManager.currentUser
        if (currentUser == null) {
            // No user signed in -> Go to Auth
            startActivity(Intent(this@SplashActivity, AuthActivity::class.java))
            finish()
            return
        }

        // Check email verification
        currentUser.reload()
        if (!currentUser.isEmailVerified) {
            startActivity(Intent(this@SplashActivity, VerifyEmailActivity::class.java))
            finish()
            return
        }

        // Check profile setup completion
        val profile = FirebaseManager.getUserProfile(currentUser.uid)
        if (profile == null || !profile.isProfileComplete) {
            startActivity(Intent(this@SplashActivity, ProfileSetupActivity::class.java))
            finish()
            return
        }

        // All set -> Go to Main App
        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        finish()
    }
}
