package com.talent.boomchat.ui.spiritual.godlyvideos

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.exoplayer.ExoPlayer
import com.talent.boomchat.databinding.ActivityGodlyTiktokBinding

class GodlyTikTokActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGodlyTiktokBinding
    private var exoPlayer: ExoPlayer? = null
    private var isLiked = false
    private var likesCount = 1200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGodlyTiktokBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }

        binding.btnLikeVideo.setOnClickListener {
            isLiked = !isLiked
            likesCount += if (isLiked) 1 else -1
            binding.tvLikeCount.text = likesCount.toString()
            binding.btnLikeVideo.setColorFilter(
                if (isLiked) getColor(android.R.color.holo_red_light) else getColor(android.R.color.white)
            )
            Toast.makeText(this, if (isLiked) "Blessed by this Godly video!" else "Unliked", Toast.LENGTH_SHORT).show()
        }

        binding.btnShareVideo.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Godly Video on BoomChat Talent")
                putExtra(Intent.EXTRA_TEXT, "Watch this inspiring spiritual message on BoomChat Talent: Living rightly with God and fellow people!")
            }
            startActivity(Intent.createChooser(shareIntent, "Share Godly Video"))
        }

        binding.btnUploadGodlyVideo.setOnClickListener {
            startActivity(Intent(this, UploadGodlyVideoActivity::class.java))
        }

        initPlayer()
    }

    private fun initPlayer() {
        exoPlayer = ExoPlayer.Builder(this).build()
        binding.videoPlayerView.player = exoPlayer
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer?.release()
    }
}
