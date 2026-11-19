package com.talent.boomchat.data.model

import com.google.firebase.firestore.IgnoreExtraProperties

/**
 * Daily Devotional Model
 */
@IgnoreExtraProperties
data class DevotionalItem(
    val id: String = "",
    val title: String = "",
    val date: String = "",
    val scriptureVerse: String = "",
    val reflectionText: String = "",
    val prayerPoint: String = "",
    val actionStep: String = "",
    val author: String = "TALENT BRUICE",
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Daily Guided Audio Prayer with matching text
 */
@IgnoreExtraProperties
data class GuidedPrayerItem(
    val id: String = "",
    val title: String = "",
    val theme: String = "",
    val audioUrl: String = "",
    val prayerText: String = "",
    val durationSeconds: Int = 0,
    val uploaderId: String = "",
    val uploaderName: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Daily Guided Audio Scripture with matching text
 */
@IgnoreExtraProperties
data class GuidedScriptureItem(
    val id: String = "",
    val bookAndChapter: String = "",
    val theme: String = "",
    val audioUrl: String = "",
    val scriptureText: String = "",
    val reflectionNotes: String = "",
    val durationSeconds: Int = 0,
    val uploaderId: String = "",
    val uploaderName: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Godly Short Video (TikTok-style Godly video)
 */
@IgnoreExtraProperties
data class GodlyVideoItem(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val scriptureTag: String = "",
    val videoUrl: String = "",
    val thumbnailUrl: String = "",
    val uploaderId: String = "",
    val uploaderName: String = "",
    val uploaderPhoto: String = "",
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val likedByUsers: List<String> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Christian Music (Audio only without text)
 */
@IgnoreExtraProperties
data class ChristianMusicItem(
    val id: String = "",
    val title: String = "",
    val artist: String = "",
    val album: String = "",
    val audioUrl: String = "",
    val durationSeconds: Int = 0,
    val coverArtUrl: String = "",
    val uploaderId: String = "",
    val uploaderName: String = "",
    val playsCount: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Live Bible Study Session (teaching bible in bits alongside text)
 */
@IgnoreExtraProperties
data class LiveBibleStudySession(
    val id: String = "",
    val topic: String = "",
    val currentScriptureRef: String = "",
    val currentScriptureText: String = "",
    val teacherExposition: String = "",
    val teacherId: String = "",
    val teacherName: String = "TALENT BRUICE",
    val isLive: Boolean = true,
    val liveChatMessages: List<StudyChatMessage> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
)

@IgnoreExtraProperties
data class StudyChatMessage(
    val id: String = "",
    val senderName: String = "",
    val message: String = "",
    val isQuestion: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Lectio Divina 4-Step Contemplation Model
 */
data class LectioDivinaSession(
    val scriptureReference: String,
    val scriptureText: String,
    val lectioPrompt: String = "Lectio (Read): Read slowly, noticing words that catch your spirit.",
    val meditatioPrompt: String = "Meditatio (Meditate): Reflect on what God is whispering to your soul.",
    val oratioPrompt: String = "Oratio (Pray): Pour out your candid prayer in response to the Word.",
    val contemplatioPrompt: String = "Contemplatio (Contemplate): Rest quietly in His Holy Presence."
)
