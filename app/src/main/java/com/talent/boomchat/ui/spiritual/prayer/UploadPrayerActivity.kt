package com.talent.boomchat.ui.spiritual.prayer

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.talent.boomchat.data.firebase.FirebaseManager
import com.talent.boomchat.data.model.GuidedPrayerItem
import com.talent.boomchat.databinding.ActivityUploadPrayerBinding
import kotlinx.coroutines.launch

class UploadPrayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadPrayerBinding
    private var selectedAudioUri: Uri? = null

    private val audioPickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            selectedAudioUri = uri
            binding.tvSelectedAudio.text = "Audio Selected: ${uri.lastPathSegment}"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadPrayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.btnSelectAudio.setOnClickListener {
            audioPickerLauncher.launch("audio/*")
        }

        binding.btnSubmitPrayer.setOnClickListener {
            val title = binding.etPrayerTitle.text.toString().trim()
            val theme = binding.etPrayerTheme.text.toString().trim()
            val text = binding.etPrayerText.text.toString().trim()

            if (title.isEmpty() || text.isEmpty()) {
                Toast.makeText(this, "Title and Prayer text are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                Toast.makeText(this@UploadPrayerActivity, "Uploading Guided Prayer…", Toast.LENGTH_SHORT).show()
                var audioUrl = ""
                if (selectedAudioUri != null) {
                    audioUrl = FirebaseManager.uploadMediaToStorage(selectedAudioUri!!, "guided_prayers/${System.currentTimeMillis()}.mp3") ?: ""
                }

                val prayer = GuidedPrayerItem(
                    title = title,
                    theme = theme,
                    audioUrl = audioUrl,
                    prayerText = text,
                    uploaderId = FirebaseManager.currentUserId,
                    uploaderName = FirebaseManager.currentUser?.displayName ?: "TALENT BRUICE"
                )

                val success = FirebaseManager.uploadPrayer(prayer)
                if (success) {
                    Toast.makeText(this@UploadPrayerActivity, "Guided Prayer Uploaded Successfully!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@UploadPrayerActivity, "Failed to upload prayer", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
