package com.talent.boomchat.ui.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.talent.boomchat.databinding.ActivityAboutDeveloperBinding

class AboutDeveloperActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutDeveloperBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutDeveloperBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }
    }
}
