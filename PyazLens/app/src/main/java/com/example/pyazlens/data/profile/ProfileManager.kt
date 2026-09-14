package com.example.pyazlens.data.profile

import android.content.Context
import android.content.SharedPreferences

object ProfileManager {

    private const val PREFS_NAME = "pyazlens_user_prefs"
    private const val KEY_PROFILE_ID = "user_profile_id"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_USER_PHONE = "user_phone"
    private const val KEY_USER_ADDRESS = "user_address"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveProfile(
        context: Context,
        profileId: Long,
        name: String,
        phone: String?,
        address: String?
    ) {
        getPrefs(context).edit()
            .putLong(KEY_PROFILE_ID, profileId)
            .putString(KEY_USER_NAME, name.trim())
            .putString(KEY_USER_PHONE, phone?.trim() ?: "")
            .putString(KEY_USER_ADDRESS, address?.trim() ?: "")
            .apply()
    }

    fun getProfileId(context: Context): Long {
        return getPrefs(context).getLong(KEY_PROFILE_ID, -1L)
    }

    fun getUserName(context: Context): String {
        return getPrefs(context).getString(KEY_USER_NAME, "") ?: ""
    }

    fun getUserPhone(context: Context): String {
        return getPrefs(context).getString(KEY_USER_PHONE, "") ?: ""
    }

    fun getUserAddress(context: Context): String {
        return getPrefs(context).getString(KEY_USER_ADDRESS, "") ?: ""
    }

    fun hasSavedProfile(context: Context): Boolean {
        val id = getProfileId(context)
        val name = getUserName(context)
        return id > 0L && name.isNotBlank()
    }

    fun clearProfile(context: Context) {
        getPrefs(context).edit().clear().apply()
    }
}
