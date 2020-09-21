@file:Suppress("DEPRECATION")

package it.giovanni.arkivio.utils

import android.content.SharedPreferences
import android.preference.PreferenceManager
import it.giovanni.arkivio.App

class SharedPreferencesManager {

    companion object {

        lateinit var preferences: SharedPreferences

        fun saveDarkModeStateToPreferences(isDarkMode: Boolean) {
            preferences = PreferenceManager.getDefaultSharedPreferences(App.context)
            val editor = preferences.edit()
            editor.putBoolean("DARK_MODE", isDarkMode)
            editor.apply()
        }

        fun loadDarkModeStateFromPreferences(): Boolean {
            preferences = PreferenceManager.getDefaultSharedPreferences(App.context)
            return preferences.getBoolean("DARK_MODE", false)
        }
    }
}