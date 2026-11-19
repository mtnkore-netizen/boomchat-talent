package com.talent.boomchat.ui.spiritual.lectio

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.talent.boomchat.databinding.ActivityLectioDivinaBinding

class LectioDivinaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLectioDivinaBinding
    private var currentStage = 0

    private val stages = listOf(
        Pair("1. Lectio (Read)", "Read the scripture passage gently and slowly. Listen for words or phrases that capture your heart."),
        Pair("2. Meditatio (Meditate)", "Reflect on how this message touches your life today. Ponder on God's personal call to you."),
        Pair("3. Oratio (Pray)", "Respond to God in heartfelt conversation. Speak to Him candidly with praise, petition, and thanksgiving."),
        Pair("4. Contemplatio (Contemplate)", "Rest silently in the presence of God. Allow His Holy Spirit to minister peace to your soul.")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLectioDivinaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }

        updateStageUI()

        binding.btnNextStage.setOnClickListener {
            if (currentStage < stages.size - 1) {
                currentStage++
                updateStageUI()
            } else {
                Toast.makeText(this, "Contemplation complete. Walk in God's peace!", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    private fun updateStageUI() {
        val (title, description) = stages[currentStage]
        binding.tvStageTitle.text = title
        binding.tvStageDescription.text = description
        binding.btnNextStage.text = if (currentStage == stages.size - 1) "Finish Meditation" else "Proceed to Next Stage"
    }
}
