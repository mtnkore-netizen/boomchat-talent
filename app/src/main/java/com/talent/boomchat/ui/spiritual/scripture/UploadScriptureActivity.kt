package com.talent.boomchat.ui.spiritual.scripture

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.talent.boomchat.data.firebase.FirebaseManager
import com.talent.boomchat.data.model.GuidedScriptureItem
import com.talent.boomchat.databinding.ActivityUploadScriptureBinding
import kotlinx.coroutines.launch

class UploadScriptureActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadScriptureBinding
    private var selectedAudioUri: Uri? = null

    private val audioPickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            selectedAudioUri = uri
            binding.tvSelectedAudio.text = "Audio: ${uri.lastPathSegment}"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadScriptureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.btnSelectAudio.setOnClickListener {
            audioPickerLauncher.launch("audio/*")
        }

        binding.btnSubmitScripture.setOnClickListener {
            val book = binding.etBookChapter.text.toString().trim()
            val theme = binding.etTheme.text.toString().trim()
            val text = binding.etScriptureText.text.toString().trim()
            val notes = binding.etReflectionNotes.text.toString().trim()

            if (book.isEmpty() || text.isEmpty()) {
                Toast.makeText(this, "Book reference and scripture text required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                Toast.makeText(this@UploadScriptureActivity, "Uploading Scripture Audio…", Toast.LENGTH_SHORT).show()
                var audioUrl = ""
                if (selectedAudioUri != null) {
                    audioUrl = FirebaseManager.uploadMediaToStorage(selectedAudioUri!!, "guided_scriptures/${System.currentTimeMillis()}.mp3") ?: ""
                }

                val item = GuidedScriptureItem(
                    bookAndChapter = book,
                    theme = theme,
                    audioUrl = audioUrl,
                    scriptureText = text,
                    reflectionNotes = notes,
                    uploaderId = FirebaseManager.currentUserId,
                    uploaderName = FirebaseManager.currentUser?.displayName ?: "TALENT BRUICE"
                )

                val success = FirebaseManager.uploadScriptureAudio(item)
                if (success) {
                    Toast.makeText(this@UploadScriptureActivity, "Uploaded Scripture!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@UploadScriptureActivity, "Upload failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
