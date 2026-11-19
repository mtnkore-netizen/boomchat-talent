package com.talent.boomchat.ui.spiritual.music

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.exoplayer.ExoPlayer
import com.talent.boomchat.databinding.ActivityChristianMusicBinding

class ChristianMusicActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChristianMusicBinding
    private var exoPlayer: ExoPlayer? = null
    private var isPlaying = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChristianMusicBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.fabUploadMusic.setOnClickListener {
            startActivity(Intent(this, UploadMusicActivity::class.java))
        }

        binding.btnMusicPlayPause.setOnClickListener {
            isPlaying = !isPlaying
            binding.btnMusicPlayPause.setImageResource(
                if (isPlaying) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play
            )
            Toast.makeText(this, if (isPlaying) "Playing Christian Music" else "Music Paused", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer?.release()
    }
}
