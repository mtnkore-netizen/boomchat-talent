package com.talent.boomchat.ui.spiritual.devotional

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.talent.boomchat.data.firebase.FirebaseManager
import com.talent.boomchat.data.model.DevotionalItem
import com.talent.boomchat.databinding.ActivityUploadDevotionalBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UploadDevotionalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadDevotionalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadDevotionalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.btnSubmitDevotional.setOnClickListener {
            val title = binding.etTitle.text.toString().trim()
            val scripture = binding.etScriptureVerse.text.toString().trim()
            val reflection = binding.etReflection.text.toString().trim()
            val prayerPoint = binding.etPrayerPoint.text.toString().trim()
            val actionStep = binding.etActionStep.text.toString().trim()

            if (title.isEmpty() || reflection.isEmpty()) {
                Toast.makeText(this, "Title and Reflection are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dateStr = dateFormat.format(Date())

            lifecycleScope.launch {
                val item = DevotionalItem(
                    title = title,
                    date = dateStr,
                    scriptureVerse = scripture,
                    reflectionText = reflection,
                    prayerPoint = prayerPoint,
                    actionStep = actionStep,
                    author = FirebaseManager.currentUser?.displayName ?: "TALENT BRUICE"
                )

                val success = FirebaseManager.uploadDevotional(item)
                if (success) {
                    Toast.makeText(this@UploadDevotionalActivity, "Devotional published!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@UploadDevotionalActivity, "Failed to publish", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
