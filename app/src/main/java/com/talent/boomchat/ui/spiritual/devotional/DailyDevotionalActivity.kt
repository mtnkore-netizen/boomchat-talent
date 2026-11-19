package com.talent.boomchat.ui.spiritual.devotional

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.talent.boomchat.databinding.ActivityDailyDevotionalBinding

class DailyDevotionalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDailyDevotionalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDailyDevotionalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.fabUploadDevotional.setOnClickListener {
            startActivity(Intent(this, UploadDevotionalActivity::class.java))
        }
    }
}
