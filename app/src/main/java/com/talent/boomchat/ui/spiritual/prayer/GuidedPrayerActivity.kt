package com.talent.boomchat.ui.spiritual.prayer

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.exoplayer.ExoPlayer
import com.talent.boomchat.databinding.ActivityGuidedPrayerBinding

class GuidedPrayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGuidedPrayerBinding
    private var exoPlayer: ExoPlayer? = null
    private var isPlaying = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGuidedPrayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.fabUploadPrayer.setOnClickListener {
            startActivity(Intent(this, UploadPrayerActivity::class.java))
        }

        binding.btnPrayerPlay.setOnClickListener {
            isPlaying = !isPlaying
            binding.btnPrayerPlay.setImageResource(
                if (isPlaying) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play
            )
            Toast.makeText(this, if (isPlaying) "Playing Guided Audio Prayer" else "Paused", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer?.release()
    }
}
