package com.talent.boomchat.data.model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val photoUrl: String = "",
    val spiritualMotto: String = "Living rightly with God and fellow people",
    val isEmailVerified: Boolean = false,
    val isProfileComplete: Boolean = false,
    val onlineStatus: Boolean = false,
    val lastSeenTimestamp: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
)
