package com.talent.boomchat.data.model

import com.google.firebase.firestore.IgnoreExtraProperties

enum class StatusMediaType {
    TEXT,
    IMAGE,
    VIDEO
}

@IgnoreExtraProperties
data class UserStatus(
    val statusId: String = "",
    val userId: String = "",
    val userName: String = "",
    val userPhoto: String = "",
    val caption: String = "",
    val mediaUrl: String = "",
    val mediaType: StatusMediaType = StatusMediaType.TEXT,
    val backgroundColorHex: String = "#0E3A53",
    val timestamp: Long = System.currentTimeMillis(),
    val expiresAt: Long = System.currentTimeMillis() + (24 * 60 * 60 * 1000L), // 24 hours
    val viewers: List<String> = emptyList()
)

enum class CallType {
    VOICE,
    VIDEO
}

enum class CallState {
    RINGING,
    ACCEPTED,
    REJECTED,
    ENDED,
    MISSED
}

@IgnoreExtraProperties
data class CallSession(
    val callId: String = "",
    val callerId: String = "",
    val callerName: String = "",
    val callerPhoto: String = "",
    val receiverId: String = "",
    val receiverName: String = "",
    val receiverPhoto: String = "",
    val callType: CallType = CallType.VOICE,
    val callState: CallState = CallState.RINGING,
    val sdpOffer: String = "",
    val sdpAnswer: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
