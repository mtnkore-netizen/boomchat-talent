package com.talent.boomchat.ui.spiritual.livestudy

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.talent.boomchat.databinding.ActivityLiveBibleStudyBinding

class LiveBibleStudyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLiveBibleStudyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLiveBibleStudyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }

        // Teacher updates the current scripture bit and commentary
        binding.btnUpdateLessonBit.setOnClickListener {
            showUpdateLessonDialog()
        }

        // Audience asks a question
        binding.btnSendQuestion.setOnClickListener {
            val q = binding.etQuestion.text.toString().trim()
            if (q.isNotEmpty()) {
                Toast.makeText(this, "Question submitted to teacher!", Toast.LENGTH_SHORT).show()
                binding.etQuestion.text.clear()
            }
        }
    }

    private fun showUpdateLessonDialog() {
        val input = EditText(this).apply {
            hint = "Teach next scripture bit & exposition…"
            setPadding(48, 32, 48, 32)
        }

        AlertDialog.Builder(this)
            .setTitle("Teach Scripture in Bits")
            .setMessage("Update the active lesson bit for all learners:")
            .setView(input)
            .setPositiveButton("Publish Bit") { _, _ ->
                val newBit = input.text.toString().trim()
                if (newBit.isNotEmpty()) {
                    binding.tvExposition.text = newBit
                    Toast.makeText(this, "Live teaching bit updated!", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
