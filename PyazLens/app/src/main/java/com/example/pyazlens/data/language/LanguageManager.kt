package com.example.pyazlens.data.language

import android.content.Context
import android.content.SharedPreferences

object LanguageManager {

    private const val PREFS_NAME = "pyazlens_prefs"
    private const val KEY_LANGUAGE = "selected_language"

    const val LANG_ENGLISH = "en"
    const val LANG_HINDI = "hi"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getSavedLanguage(context: Context): String {
        val lang = getPrefs(context).getString(KEY_LANGUAGE, LANG_ENGLISH) ?: LANG_ENGLISH
        return if (lang == LANG_HINDI || lang == "Hindi" || lang == "हिंदी") LANG_HINDI else LANG_ENGLISH
    }

    fun saveLanguage(context: Context, langCode: String) {
        val cleanCode = if (langCode == LANG_HINDI || langCode == "Hindi" || langCode == "हिंदी") LANG_HINDI else LANG_ENGLISH
        getPrefs(context).edit().putString(KEY_LANGUAGE, cleanCode).apply()
    }

    fun hasSavedLanguage(context: Context): Boolean {
        return getPrefs(context).contains(KEY_LANGUAGE)
    }

    fun getLanguageDisplayName(langCode: String): String {
        return if (langCode == LANG_HINDI || langCode == "Hindi" || langCode == "हिंदी") "हिंदी" else "English"
    }
}
