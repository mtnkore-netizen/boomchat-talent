package com.talent.boomchat.ui.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.talent.boomchat.data.local.UserSessionManager
import com.talent.boomchat.data.model.User
import com.talent.boomchat.databinding.ActivityProfileSetupBinding
import com.talent.boomchat.ui.MainActivity

class ProfileSetupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileSetupBinding
    private lateinit var sessionManager: UserSessionManager
    private var selectedImageUri: Uri? = null

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            Glide.with(this).load(uri).circleCrop().into(binding.ivAvatar)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileSetupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = UserSessionManager(this)

        // Pre-fill with developer name & motto by default
        binding.etFullName.setText(sessionManager.displayName)
        binding.etBio.setText(sessionManager.spiritualMotto)

        binding.avatarFrame.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        binding.btnCompleteProfile.setOnClickListener {
            saveProfile()
        }
    }

    private fun saveProfile() {
        val fullName = binding.etFullName.text.toString().trim()
        val bio = binding.etBio.text.toString().trim()

        if (fullName.isEmpty()) {
            binding.etFullName.error = "Please enter your name"
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        binding.btnCompleteProfile.isEnabled = false

        val user = User(
            uid = sessionManager.userId,
            email = "user@boomchat.org",
            displayName = fullName,
            photoUrl = selectedImageUri?.toString() ?: sessionManager.photoUrl,
            spiritualMotto = if (bio.isNotEmpty()) bio else "Living rightly with God and fellow people",
            isEmailVerified = true,
            isProfileComplete = true,
            onlineStatus = true
        )

        sessionManager.saveUserProfile(user)

        Toast.makeText(this, "Welcome, $fullName! Profile ready.", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
