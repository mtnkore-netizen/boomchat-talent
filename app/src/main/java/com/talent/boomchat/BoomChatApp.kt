package com.talent.boomchat

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.google.firebase.FirebaseApp

class BoomChatApp : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Safe Firebase initialization
        try {
            FirebaseApp.initializeApp(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Messages Channel
            val chatChannel = NotificationChannel(
                CHANNEL_MESSAGES,
                "BoomChat Messages",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for incoming text, voice notes, and media messages"
                enableVibration(true)
            }

            // Calls Channel
            val callsChannel = NotificationChannel(
                CHANNEL_CALLS,
                "BoomChat Voice & Video Calls",
                NotificationManager.IMPORTANCE_MAX
            ).apply {
                description = "Incoming voice and video call ring notifications"
                enableVibration(true)
            }

            // Spiritual Devotions Channel
            val spiritualChannel = NotificationChannel(
                CHANNEL_SPIRITUAL,
                "Spiritual Growth & Devotions",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily guided prayer, scripture audio, and devotional reminders"
            }

            notificationManager.createNotificationChannels(listOf(chatChannel, callsChannel, spiritualChannel))
        }
    }

    companion object {
        lateinit var instance: BoomChatApp
            private set

        const val CHANNEL_MESSAGES = "channel_boomchat_messages"
        const val CHANNEL_CALLS = "channel_boomchat_calls"
        const val CHANNEL_SPIRITUAL = "channel_boomchat_spiritual"
    }
}
