package com.talent.boomchat.ui.spiritual.music

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.talent.boomchat.data.firebase.FirebaseManager
import com.talent.boomchat.data.model.ChristianMusicItem
import com.talent.boomchat.databinding.ActivityUploadMusicBinding
import kotlinx.coroutines.launch

class UploadMusicActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadMusicBinding
    private var selectedAudioUri: Uri? = null

    private val audioPickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            selectedAudioUri = uri
            binding.tvSelectedMusic.text = "Audio: ${uri.lastPathSegment}"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadMusicBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.btnSelectMusic.setOnClickListener {
            audioPickerLauncher.launch("audio/*")
        }

        binding.btnSubmitMusic.setOnClickListener {
            val title = binding.etMusicTitle.text.toString().trim()
            val artist = binding.etArtistName.text.toString().trim()

            if (title.isEmpty() || artist.isEmpty()) {
                Toast.makeText(this, "Title and Artist are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                Toast.makeText(this@UploadMusicActivity, "Uploading Christian Track…", Toast.LENGTH_SHORT).show()
                var audioUrl = ""
                if (selectedAudioUri != null) {
                    audioUrl = FirebaseManager.uploadMediaToStorage(selectedAudioUri!!, "christian_music/${System.currentTimeMillis()}.mp3") ?: ""
                }

                val item = ChristianMusicItem(
                    title = title,
                    artist = artist,
                    audioUrl = audioUrl,
                    uploaderId = FirebaseManager.currentUserId,
                    uploaderName = FirebaseManager.currentUser?.displayName ?: "TALENT BRUICE"
                )

                val success = FirebaseManager.uploadChristianMusic(item)
                if (success) {
                    Toast.makeText(this@UploadMusicActivity, "Music Uploaded Successfully!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@UploadMusicActivity, "Failed to upload music", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
