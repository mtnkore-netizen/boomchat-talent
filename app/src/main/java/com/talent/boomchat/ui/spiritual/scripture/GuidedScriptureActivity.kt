package com.talent.boomchat.ui.spiritual.scripture

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.talent.boomchat.databinding.ActivityGuidedScriptureBinding

class GuidedScriptureActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGuidedScriptureBinding
    private var isPlaying = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGuidedScriptureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.fabUploadScripture.setOnClickListener {
            startActivity(Intent(this, UploadScriptureActivity::class.java))
        }

        binding.btnScripturePlay.setOnClickListener {
            isPlaying = !isPlaying
            binding.btnScripturePlay.setImageResource(
                if (isPlaying) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play
            )
            Toast.makeText(this, if (isPlaying) "Playing Scripture Audio" else "Paused", Toast.LENGTH_SHORT).show()
        }
    }
}
