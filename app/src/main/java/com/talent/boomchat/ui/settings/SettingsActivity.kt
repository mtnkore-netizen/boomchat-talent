package com.talent.boomchat.ui.settings

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.talent.boomchat.data.firebase.FirebaseManager
import com.talent.boomchat.databinding.ActivitySettingsBinding
import com.talent.boomchat.ui.auth.AuthActivity
import com.talent.boomchat.util.PermissionsHelper

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.itemDeveloperCreds.setOnClickListener {
            startActivity(Intent(this, AboutDeveloperActivity::class.java))
        }

        binding.itemStoragePerms.setOnClickListener {
            PermissionsHelper.requestManageStoragePermission(this)
            Toast.makeText(this, "Opening Storage Permissions Manager", Toast.LENGTH_SHORT).show()
        }

        binding.itemAllPerms.setOnClickListener {
            PermissionsHelper.requestAllPermissions(this)
            Toast.makeText(this, "Requesting All Phone Permissions", Toast.LENGTH_SHORT).show()
        }

        binding.itemSignOut.setOnClickListener {
            FirebaseManager.auth.signOut()
            val intent = Intent(this, AuthActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }
    }
}
