package com.talent.boomchat.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.talent.boomchat.data.firebase.FirebaseManager
import com.talent.boomchat.databinding.ActivityVerifyEmailBinding
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class VerifyEmailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerifyEmailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifyEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val user = FirebaseManager.currentUser
        binding.tvEmailTarget.text = "A verification email was sent to ${user?.email ?: "your address"}. Please click the link in your inbox to activate your account."

        binding.btnVerified.setOnClickListener {
            checkVerification()
        }

        binding.btnResend.setOnClickListener {
            resendEmail()
        }

        binding.tvSignOut.setOnClickListener {
            FirebaseManager.auth.signOut()
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        }
    }

    private fun checkVerification() {
        val user = FirebaseManager.currentUser ?: return
        lifecycleScope.launch {
            try {
                user.reload().await()
                if (user.isEmailVerified) {
                    Toast.makeText(this@VerifyEmailActivity, "Email verified successfully!", Toast.LENGTH_SHORT).show()
                    val profile = FirebaseManager.getUserProfile(user.uid)
                    if (profile == null || !profile.isProfileComplete) {
                        startActivity(Intent(this@VerifyEmailActivity, ProfileSetupActivity::class.java))
                    } else {
                        startActivity(Intent(this@VerifyEmailActivity, com.talent.boomchat.ui.MainActivity::class.java))
                    }
                    finish()
                } else {
                    Toast.makeText(this@VerifyEmailActivity, "Email not yet verified. Please check your inbox or spam folder.", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@VerifyEmailActivity, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun resendEmail() {
        val user = FirebaseManager.currentUser ?: return
        lifecycleScope.launch {
            try {
                user.sendEmailVerification().await()
                Toast.makeText(this@VerifyEmailActivity, "Verification email resent!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@VerifyEmailActivity, "Failed to send: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
