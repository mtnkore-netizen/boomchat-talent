package com.talent.boomchat.ui.chat

import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.talent.boomchat.data.model.CallType
import com.talent.boomchat.databinding.ActivityCallBinding

class CallActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCallBinding
    private var isMuted = false
    private var isSpeakerOn = false
    private var callType = CallType.VOICE
    private var callSeconds = 0

    private val handler = Handler(Looper.getMainLooper())
    private val callTimerRunnable = object : Runnable {
        override fun run() {
            callSeconds++
            val minutes = callSeconds / 60
            val secs = callSeconds % 60
            binding.tvCallTimer.text = String.format("%02d:%02d", minutes, secs)
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCallBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val recipientName = intent.getStringExtra(EXTRA_RECIPIENT_NAME) ?: "Believer"
        val callTypeStr = intent.getStringExtra(EXTRA_CALL_TYPE) ?: CallType.VOICE.name
        callType = CallType.valueOf(callTypeStr)

        binding.tvCallName.text = recipientName
        binding.tvCallStatus.text = if (callType == CallType.VIDEO) "BoomChat Video Calling…" else "BoomChat Voice Calling…"

        if (callType == CallType.VIDEO) {
            binding.cardPip.visibility = View.VISIBLE
            binding.fabSpeakerOrCamera.setImageResource(android.R.drawable.ic_menu_camera)
        }

        // Simulate connection after 2 seconds
        handler.postDelayed({
            binding.tvCallStatus.text = "Connected"
            binding.tvCallTimer.visibility = View.VISIBLE
            handler.post(callTimerRunnable)
        }, 2000)

        setupButtons()
    }

    private fun setupButtons() {
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        binding.fabMute.setOnClickListener {
            isMuted = !isMuted
            audioManager.isMicrophoneMute = isMuted
            binding.fabMute.setImageResource(
                if (isMuted) android.R.drawable.ic_lock_silent_mode else android.R.drawable.ic_btn_speak_now
            )
            Toast.makeText(this, if (isMuted) "Microphone Muted" else "Microphone Unmuted", Toast.LENGTH_SHORT).show()
        }

        binding.fabSpeakerOrCamera.setOnClickListener {
            if (callType == CallType.VIDEO) {
                Toast.makeText(this, "Flipping Camera", Toast.LENGTH_SHORT).show()
            } else {
                isSpeakerOn = !isSpeakerOn
                audioManager.isSpeakerphoneOn = isSpeakerOn
                Toast.makeText(this, if (isSpeakerOn) "Speakerphone On" else "Earpiece Audio", Toast.LENGTH_SHORT).show()
            }
        }

        binding.fabEndCall.setOnClickListener {
            endCall()
        }
    }

    private fun endCall() {
        handler.removeCallbacks(callTimerRunnable)
        Toast.makeText(this, "Call Ended", Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(callTimerRunnable)
    }

    companion object {
        const val EXTRA_RECIPIENT_ID = "extra_call_recipient_id"
        const val EXTRA_RECIPIENT_NAME = "extra_call_recipient_name"
        const val EXTRA_CALL_TYPE = "extra_call_type"
    }
}
