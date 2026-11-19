package com.talent.boomchat.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.talent.boomchat.data.local.UserSessionManager
import com.talent.boomchat.databinding.ActivitySplashBinding
import com.talent.boomchat.ui.MainActivity
import com.talent.boomchat.ui.auth.ProfileSetupActivity
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
            // Show splash and developer credentials for 2 seconds
            delay(2000)
            routeNextScreen()
        }
    }

    private fun routeNextScreen() {
        val sessionManager = UserSessionManager(this)

        if (!sessionManager.isProfileComplete) {
            // First time use -> Go directly to Profile Setup (No Sign In / Sign Up needed!)
            startActivity(Intent(this@SplashActivity, ProfileSetupActivity::class.java))
        } else {
            // Already setup -> Go straight into the main app!
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        }
        finish()
    }
}
