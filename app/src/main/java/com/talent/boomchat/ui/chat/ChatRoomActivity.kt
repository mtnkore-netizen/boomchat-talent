package com.talent.boomchat.ui.chat

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.talent.boomchat.R
import com.talent.boomchat.data.firebase.FirebaseManager
import com.talent.boomchat.data.model.CallType
import com.talent.boomchat.data.model.ChatMessage
import com.talent.boomchat.data.model.MessageType
import com.talent.boomchat.databinding.ActivityChatRoomBinding
import com.talent.boomchat.util.AudioRecorderHelper
import kotlinx.coroutines.launch
import java.io.File

class ChatRoomActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatRoomBinding
    private lateinit var audioRecorder: AudioRecorderHelper

    private var recipientId: String = ""
    private var recipientName: String = ""
    private var chatRoomId: String = ""
    private var isRecording = false

    private val filePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            handleSelectedAttachment(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recipientId = intent.getStringExtra(EXTRA_RECIPIENT_ID) ?: ""
        recipientName = intent.getStringExtra(EXTRA_RECIPIENT_NAME) ?: "Believer"
        val myUid = FirebaseManager.currentUserId
        chatRoomId = if (myUid < recipientId) "${myUid}_$recipientId" else "${recipientId}_$myUid"

        audioRecorder = AudioRecorderHelper(this)

        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        binding.tvRecipientName.text = recipientName
        binding.btnBack.setOnClickListener { finish() }

        binding.rvMessages.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }

        // Voice Call click
        binding.btnVoiceCall.setOnClickListener {
            val intent = Intent(this, CallActivity::class.java).apply {
                putExtra(CallActivity.EXTRA_RECIPIENT_ID, recipientId)
                putExtra(CallActivity.EXTRA_RECIPIENT_NAME, recipientName)
                putExtra(CallActivity.EXTRA_CALL_TYPE, CallType.VOICE.name)
            }
            startActivity(intent)
        }

        // Video Call click
        binding.btnVideoCall.setOnClickListener {
            val intent = Intent(this, CallActivity::class.java).apply {
                putExtra(CallActivity.EXTRA_RECIPIENT_ID, recipientId)
                putExtra(CallActivity.EXTRA_RECIPIENT_NAME, recipientName)
                putExtra(CallActivity.EXTRA_CALL_TYPE, CallType.VIDEO.name)
            }
            startActivity(intent)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupListeners() {
        // Toggle Send / Mic button depending on text length
        binding.etMessage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.trim()?.isNotEmpty() == true) {
                    binding.fabAction.setImageResource(android.R.drawable.ic_menu_send)
                } else {
                    binding.fabAction.setImageResource(android.R.drawable.ic_btn_speak_now)
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Paperclip Attachment Picker (images, videos, documents, audio)
        binding.btnAttach.setOnClickListener {
            filePickerLauncher.launch("*/*")
        }

        // Handle Touch for Voice Note Recording (Hold to record, release to send)
        binding.fabAction.setOnTouchListener { _, event ->
            val text = binding.etMessage.text.toString().trim()
            if (text.isNotEmpty()) {
                if (event.action == MotionEvent.ACTION_UP) {
                    sendTextMessage(text)
                    binding.etMessage.text.clear()
                }
                return@setOnTouchListener true
            }

            // Voice Note Hold handling
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startVoiceRecording()
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    if (isRecording) {
                        finishAndSendVoiceRecording()
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun startVoiceRecording() {
        isRecording = true
        binding.layoutRecordingIndicator.visibility = View.VISIBLE
        audioRecorder.startRecording()
    }

    private fun finishAndSendVoiceRecording() {
        isRecording = false
        binding.layoutRecordingIndicator.visibility = View.GONE
        val audioFile = audioRecorder.stopRecording()
        if (audioFile != null && audioFile.exists() && audioFile.length() > 0) {
            sendVoiceMessage(audioFile)
        } else {
            Toast.makeText(this, "Voice note too short", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendTextMessage(text: String) {
        val message = ChatMessage(
            senderId = FirebaseManager.currentUserId,
            recipientId = recipientId,
            messageText = text,
            type = MessageType.TEXT
        )
        lifecycleScope.launch {
            FirebaseManager.sendMessage(chatRoomId, message)
        }
    }

    private fun sendVoiceMessage(audioFile: File) {
        lifecycleScope.launch {
            Toast.makeText(this@ChatRoomActivity, "Uploading Voice Note…", Toast.LENGTH_SHORT).show()
            val uri = Uri.fromFile(audioFile)
            val downloadUrl = FirebaseManager.uploadChatMedia(uri, "voice_notes") ?: ""
            val message = ChatMessage(
                senderId = FirebaseManager.currentUserId,
                recipientId = recipientId,
                mediaUrl = downloadUrl,
                type = MessageType.VOICE_NOTE,
                durationSeconds = 10
            )
            FirebaseManager.sendMessage(chatRoomId, message)
            Toast.makeText(this@ChatRoomActivity, "Voice note sent!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleSelectedAttachment(uri: Uri) {
        lifecycleScope.launch {
            Toast.makeText(this@ChatRoomActivity, "Sharing file…", Toast.LENGTH_SHORT).show()
            val downloadUrl = FirebaseManager.uploadChatMedia(uri, "shared_files") ?: ""
            val message = ChatMessage(
                senderId = FirebaseManager.currentUserId,
                recipientId = recipientId,
                mediaUrl = downloadUrl,
                fileName = uri.lastPathSegment ?: "Shared File",
                type = MessageType.DOCUMENT
            )
            FirebaseManager.sendMessage(chatRoomId, message)
            Toast.makeText(this@ChatRoomActivity, "File shared successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val EXTRA_RECIPIENT_ID = "extra_recipient_id"
        const val EXTRA_RECIPIENT_NAME = "extra_recipient_name"
    }
}
