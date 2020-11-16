@file:Suppress("DEPRECATION")

package it.giovanni.arkivio.utils

import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.google.gson.GsonBuilder
import it.giovanni.arkivio.App
import it.giovanni.arkivio.bean.SelectedDaysResponse

class SharedPreferencesManager {

    companion object {

        private var preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.context)

        private const val DARK_MODE = "DARK_MODE"
        private const val REMEMBER_ME = "REMEMBER_ME"
        private const val COMPRESS = "COMPRESS"
        private const val SELECTED_DATE = "SELECTED_DATE"

        fun saveDarkModeStateToPreferences(isDarkMode: Boolean) {
            val editor: SharedPreferences.Editor = preferences.edit()
            editor.putBoolean(DARK_MODE, isDarkMode)
            editor.apply()
        }

        fun loadDarkModeStateFromPreferences(): Boolean {
            return preferences.getBoolean(DARK_MODE, false)
        }

        fun saveRememberMeToPreferences(rememberMe: Boolean) {
            val editor = preferences.edit()
            editor.putBoolean(REMEMBER_ME, rememberMe)
            editor.apply()
        }

        fun loadRememberMeFromPreferences(): Boolean {
            return preferences.getBoolean(REMEMBER_ME, false)
        }

        fun saveCompressStateToPreferences(compress: Boolean) {
            val editor = preferences.edit()
            editor.putBoolean(COMPRESS, compress)
            editor.apply()
        }

        fun loadCompressStateFromPreferences(): Boolean {
            return preferences.getBoolean(COMPRESS, false)
        }

        fun saveSelectedDaysToPreferences(selectedDaysResponse: SelectedDaysResponse?) {
            val editor: SharedPreferences.Editor = preferences.edit()
            val builder = GsonBuilder()
            val gson = builder.serializeNulls().create()
            val responseString = gson.toJson(selectedDaysResponse)
            editor.putString(SELECTED_DATE, responseString)
            editor.apply()
        }

        fun loadSelectedDaysFromPreferences(): SelectedDaysResponse? {
            val responseString = preferences.getString(SELECTED_DATE, null)
            var selectedDaysResponse: SelectedDaysResponse? = null
            if (responseString != null && responseString != "") {
                val builder = GsonBuilder()
                val gson = builder.serializeNulls().create()
                selectedDaysResponse = gson.fromJson(responseString, SelectedDaysResponse::class.java)
            }
            return selectedDaysResponse
        }
    }
}