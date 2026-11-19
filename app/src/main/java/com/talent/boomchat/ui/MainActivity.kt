package com.talent.boomchat.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.talent.boomchat.R
import com.talent.boomchat.databinding.ActivityMainBinding
import com.talent.boomchat.ui.chat.ChatListFragment
import com.talent.boomchat.ui.chat.StatusListFragment
import com.talent.boomchat.ui.files.FileManagerFragment
import com.talent.boomchat.ui.settings.SettingsActivity
import com.talent.boomchat.ui.spiritual.SpiritualHubFragment
import com.talent.boomchat.util.PermissionsHelper

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        // Request all phone permissions on launch as requested
        if (!PermissionsHelper.hasAllPermissions(this)) {
            PermissionsHelper.requestAllPermissions(this)
        }

        setupBottomNavigation()

        // Default screen is Chats
        if (savedInstanceState == null) {
            loadFragment(ChatListFragment())
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_chats -> {
                    loadFragment(ChatListFragment())
                    binding.toolbar.title = getString(R.string.app_name)
                    true
                }
                R.id.nav_status -> {
                    loadFragment(StatusListFragment())
                    binding.toolbar.title = "Status"
                    true
                }
                R.id.nav_spiritual -> {
                    loadFragment(SpiritualHubFragment())
                    binding.toolbar.title = "Spiritual Hub"
                    true
                }
                R.id.nav_files -> {
                    loadFragment(FileManagerFragment())
                    binding.toolbar.title = "File Explorer"
                    true
                }
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    false
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
