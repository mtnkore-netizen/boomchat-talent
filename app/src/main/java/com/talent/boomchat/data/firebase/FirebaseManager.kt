package com.talent.boomchat.data.firebase

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.talent.boomchat.data.model.*
import kotlinx.coroutines.tasks.await
import java.util.UUID

object FirebaseManager {

    val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    val storage: FirebaseStorage by lazy { FirebaseStorage.getInstance() }

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    val currentUserId: String
        get() = auth.currentUser?.uid ?: ""

    // ==========================================
    // AUTHENTICATION & PROFILE
    // ==========================================

    suspend fun createUserProfile(user: User): Boolean {
        return try {
            firestore.collection("users").document(user.uid).set(user).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getUserProfile(uid: String): User? {
        return try {
            val snapshot = firestore.collection("users").document(uid).get().await()
            snapshot.toObject(User::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun uploadProfileImage(uri: Uri): String? {
        return try {
            val ref = storage.reference.child("profile_images/${currentUserId}_${System.currentTimeMillis()}.jpg")
            ref.putFile(uri).await()
            ref.downloadUrl.await().toString()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // ==========================================
    // CHAT & MESSAGING
    // ==========================================

    suspend fun sendMessage(roomId: String, message: ChatMessage): Boolean {
        return try {
            val messageRef = firestore.collection("chats")
                .document(roomId)
                .collection("messages")
                .document()

            val msgWithId = message.copy(messageId = messageRef.id)
            messageRef.set(msgWithId).await()

            // Update room last message
            firestore.collection("chats").document(roomId).update(
                mapOf(
                    "lastMessage" to if (message.type == MessageType.TEXT) message.messageText else "[Media/Voice]",
                    "lastMessageTimestamp" to message.timestamp
                )
            ).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun uploadChatMedia(uri: Uri, folder: String): String? {
        return try {
            val ref = storage.reference.child("chat_media/$folder/${UUID.randomUUID()}")
            ref.putFile(uri).await()
            ref.downloadUrl.await().toString()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // ==========================================
    // STATUS / STORIES
    // ==========================================

    suspend fun postStatus(status: UserStatus): Boolean {
        return try {
            val docRef = firestore.collection("statuses").document()
            docRef.set(status.copy(statusId = docRef.id)).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // ==========================================
    // SPIRITUAL HUB: PRAYERS, SCRIPTURES, DEVOTIONS, MUSIC, GODLY VIDEOS
    // ==========================================

    suspend fun uploadDevotional(devotional: DevotionalItem): Boolean {
        return try {
            val docRef = firestore.collection("devotionals").document()
            docRef.set(devotional.copy(id = docRef.id)).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun uploadPrayer(prayer: GuidedPrayerItem): Boolean {
        return try {
            val docRef = firestore.collection("guided_prayers").document()
            docRef.set(prayer.copy(id = docRef.id)).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun uploadScriptureAudio(scripture: GuidedScriptureItem): Boolean {
        return try {
            val docRef = firestore.collection("guided_scriptures").document()
            docRef.set(scripture.copy(id = docRef.id)).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun uploadGodlyVideo(video: GodlyVideoItem): Boolean {
        return try {
            val docRef = firestore.collection("godly_videos").document()
            docRef.set(video.copy(id = docRef.id)).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun uploadChristianMusic(music: ChristianMusicItem): Boolean {
        return try {
            val docRef = firestore.collection("christian_music").document()
            docRef.set(music.copy(id = docRef.id)).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun uploadMediaToStorage(uri: Uri, path: String): String? {
        return try {
            val ref = storage.reference.child(path)
            ref.putFile(uri).await()
            ref.downloadUrl.await().toString()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
