package com.talent.boomchat.data.local

import android.content.Context
import android.content.SharedPreferences
import com.talent.boomchat.data.model.User
import java.util.UUID

class UserSessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    var userId: String
        get() {
            var id = prefs.getString(KEY_USER_ID, null)
            if (id == null) {
                id = UUID.randomUUID().toString()
                prefs.edit().putString(KEY_USER_ID, id).apply()
            }
            return id
        }
        set(value) = prefs.edit().putString(KEY_USER_ID, value).apply()

    var displayName: String
        get() = prefs.getString(KEY_DISPLAY_NAME, "TALENT BRUICE") ?: "TALENT BRUICE"
        set(value) = prefs.edit().putString(KEY_DISPLAY_NAME, value).apply()

    var email: String
        get() = prefs.getString(KEY_EMAIL, "talentbruice@boomchat.org") ?: "talentbruice@boomchat.org"
        set(value) = prefs.edit().putString(KEY_EMAIL, value).apply()

    var photoUrl: String
        get() = prefs.getString(KEY_PHOTO_URL, "") ?: ""
        set(value) = prefs.edit().putString(KEY_PHOTO_URL, value).apply()

    var spiritualMotto: String
        get() = prefs.getString(KEY_SPIRITUAL_MOTTO, "Living rightly with God and fellow people") ?: "Living rightly with God and fellow people"
        set(value) = prefs.edit().putString(KEY_SPIRITUAL_MOTTO, value).apply()

    var isProfileComplete: Boolean
        get() = prefs.getBoolean(KEY_PROFILE_COMPLETE, false)
        set(value) = prefs.edit().putBoolean(KEY_PROFILE_COMPLETE, value).apply()

    fun saveUserProfile(user: User) {
        prefs.edit().apply {
            putString(KEY_USER_ID, user.uid)
            putString(KEY_DISPLAY_NAME, user.displayName)
            putString(KEY_EMAIL, user.email)
            putString(KEY_PHOTO_URL, user.photoUrl)
            putString(KEY_SPIRITUAL_MOTTO, user.spiritualMotto)
            putBoolean(KEY_PROFILE_COMPLETE, true)
            apply()
        }
    }

    fun getCurrentUser(): User {
        return User(
            uid = userId,
            email = email,
            displayName = displayName,
            photoUrl = photoUrl,
            spiritualMotto = spiritualMotto,
            isEmailVerified = true,
            isProfileComplete = isProfileComplete,
            onlineStatus = true
        )
    }

    companion object {
        private const val PREF_NAME = "boomchat_user_session"
        private const val KEY_USER_ID = "key_user_id"
        private const val KEY_DISPLAY_NAME = "key_display_name"
        private const val KEY_EMAIL = "key_email"
        private const val KEY_PHOTO_URL = "key_photo_url"
        private const val KEY_SPIRITUAL_MOTTO = "key_spiritual_motto"
        private const val KEY_PROFILE_COMPLETE = "key_profile_complete"
    }
}
