package com.talent.boomchat.ui.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.talent.boomchat.data.firebase.FirebaseManager
import com.talent.boomchat.data.model.User
import com.talent.boomchat.databinding.ActivityProfileSetupBinding
import com.talent.boomchat.ui.MainActivity
import kotlinx.coroutines.launch

class ProfileSetupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileSetupBinding
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

        setLoading(true)
        lifecycleScope.launch {
            try {
                val currentUid = FirebaseManager.currentUserId
                val currentUser = FirebaseManager.currentUser

                var photoUrl = ""
                if (selectedImageUri != null) {
                    photoUrl = FirebaseManager.uploadProfileImage(selectedImageUri!!) ?: ""
                }

                val userProfile = User(
                    uid = currentUid,
                    email = currentUser?.email ?: "",
                    displayName = fullName,
                    photoUrl = photoUrl,
                    spiritualMotto = if (bio.isNotEmpty()) bio else "Living rightly with God and fellow people",
                    isEmailVerified = true,
                    isProfileComplete = true,
                    onlineStatus = true
                )

                FirebaseManager.createUserProfile(userProfile)
                Toast.makeText(this@ProfileSetupActivity, "Profile setup complete!", Toast.LENGTH_SHORT).show()

                startActivity(Intent(this@ProfileSetupActivity, MainActivity::class.java))
                finish()
            } catch (e: Exception) {
                Toast.makeText(this@ProfileSetupActivity, "Failed to update profile: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            } finally {
                setLoading(false)
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        binding.btnCompleteProfile.isEnabled = !loading
    }
}
