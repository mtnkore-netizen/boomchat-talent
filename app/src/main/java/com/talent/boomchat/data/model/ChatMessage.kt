package com.talent.boomchat.data.model

import com.google.firebase.firestore.IgnoreExtraProperties

enum class MessageType {
    TEXT,
    VOICE_NOTE,
    IMAGE,
    VIDEO,
    AUDIO,
    DOCUMENT,
    SCRIPTURE_SHARE
}

enum class MessageStatus {
    SENDING,
    SENT,
    DELIVERED,
    READ
}

@IgnoreExtraProperties
data class ChatMessage(
    val messageId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val recipientId: String = "",
    val messageText: String = "",
    val mediaUrl: String = "",
    val fileName: String = "",
    val fileSizeBytes: Long = 0L,
    val durationSeconds: Int = 0,
    val type: MessageType = MessageType.TEXT,
    val status: MessageStatus = MessageStatus.SENT,
    val timestamp: Long = System.currentTimeMillis()
)

@IgnoreExtraProperties
data class ChatRoom(
    val roomId: String = "",
    val participants: List<String> = emptyList(),
    val participantNames: Map<String, String> = emptyMap(),
    val participantPhotos: Map<String, String> = emptyMap(),
    val lastMessage: String = "",
    val lastMessageTimestamp: Long = 0L,
    val unreadCount: Map<String, Int> = emptyMap()
)
