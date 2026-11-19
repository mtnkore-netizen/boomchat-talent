package com.talent.boomchat.ui.spiritual.godlyvideos

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.talent.boomchat.data.firebase.FirebaseManager
import com.talent.boomchat.data.model.GodlyVideoItem
import com.talent.boomchat.databinding.ActivityUploadGodlyVideoBinding
import kotlinx.coroutines.launch

class UploadGodlyVideoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadGodlyVideoBinding
    private var selectedVideoUri: Uri? = null

    private val videoPickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            selectedVideoUri = uri
            binding.tvSelectedVideo.text = "Video Selected: ${uri.lastPathSegment}"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadGodlyVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.btnSelectVideo.setOnClickListener {
            videoPickerLauncher.launch("video/*")
        }

        binding.btnSubmitVideo.setOnClickListener {
            val title = binding.etVideoTitle.text.toString().trim()
            val desc = binding.etVideoDesc.text.toString().trim()
            val tag = binding.etScriptureTag.text.toString().trim()

            if (title.isEmpty()) {
                Toast.makeText(this, "Title is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                Toast.makeText(this@UploadGodlyVideoActivity, "Uploading Godly Video…", Toast.LENGTH_SHORT).show()
                var videoUrl = ""
                if (selectedVideoUri != null) {
                    videoUrl = FirebaseManager.uploadMediaToStorage(selectedVideoUri!!, "godly_videos/${System.currentTimeMillis()}.mp4") ?: ""
                }

                val item = GodlyVideoItem(
                    title = title,
                    description = desc,
                    scriptureTag = if (tag.isNotEmpty()) tag else "#FaithInGod",
                    videoUrl = videoUrl,
                    uploaderId = FirebaseManager.currentUserId,
                    uploaderName = FirebaseManager.currentUser?.displayName ?: "TALENT BRUICE"
                )

                val success = FirebaseManager.uploadGodlyVideo(item)
                if (success) {
                    Toast.makeText(this@UploadGodlyVideoActivity, "Godly video posted!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@UploadGodlyVideoActivity, "Upload failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
