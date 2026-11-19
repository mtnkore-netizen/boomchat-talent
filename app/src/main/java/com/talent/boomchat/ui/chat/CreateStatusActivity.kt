package com.talent.boomchat.ui.chat

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.talent.boomchat.data.firebase.FirebaseManager
import com.talent.boomchat.data.model.StatusMediaType
import com.talent.boomchat.data.model.UserStatus
import com.talent.boomchat.databinding.ActivityCreateStatusBinding
import kotlinx.coroutines.launch

class CreateStatusActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateStatusBinding
    private val bgColors = listOf("#0E3A53", "#075E54", "#6A1B9A", "#AD1457", "#C62828", "#1565C0", "#2E7D32")
    private var colorIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateStatusBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCancel.setOnClickListener { finish() }

        // Change background color
        binding.btnColorPalette.setOnClickListener {
            colorIndex = (colorIndex + 1) % bgColors.size
            binding.statusContainer.setBackgroundColor(Color.parseColor(bgColors[colorIndex]))
        }

        binding.fabPostStatus.setOnClickListener {
            val text = binding.etStatusText.text.toString().trim()
            if (text.isEmpty()) {
                Toast.makeText(this, "Status cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            postStatus(text)
        }
    }

    private fun postStatus(caption: String) {
        lifecycleScope.launch {
            val status = UserStatus(
                userId = FirebaseManager.currentUserId,
                userName = FirebaseManager.currentUser?.displayName ?: "Believer",
                caption = caption,
                mediaType = StatusMediaType.TEXT,
                backgroundColorHex = bgColors[colorIndex]
            )

            val success = FirebaseManager.postStatus(status)
            if (success) {
                Toast.makeText(this@CreateStatusActivity, "Status posted!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this@CreateStatusActivity, "Failed to post status", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        const val EXTRA_IS_MEDIA = "extra_is_media"
    }
}
