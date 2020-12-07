@file:Suppress("DEPRECATION")

package it.giovanni.arkivio.utils

import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.google.gson.GsonBuilder
import it.giovanni.arkivio.App
import it.giovanni.arkivio.bean.SelectedDaysResponse
import it.giovanni.arkivio.bean.user.UserResponse

class SharedPreferencesManager {

    companion object {

        private var preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.context)

        private const val DARK_MODE = "DARK_MODE"
        private const val REMEMBER_ME = "REMEMBER_ME"
        private const val COMPRESS = "COMPRESS"
        private const val SELECTED_DATE = "SELECTED_DATE"
        private const val USERS = "USERS"

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

        fun saveSelectedDaysToPreferences(response: SelectedDaysResponse?) {
            val editor: SharedPreferences.Editor = preferences.edit()
            val builder = GsonBuilder()
            val gson = builder.serializeNulls().create()
            val responseString = gson.toJson(response)
            editor.putString(SELECTED_DATE, responseString)
            editor.apply()
        }

        fun loadSelectedDaysFromPreferences(): SelectedDaysResponse? {
            val responseString = preferences.getString(SELECTED_DATE, null)
            var response: SelectedDaysResponse? = null
            if (responseString != null && responseString != "") {
                val builder = GsonBuilder()
                val gson = builder.serializeNulls().create()
                response = gson.fromJson(responseString, SelectedDaysResponse::class.java)
            }
            return response
        }

        fun resetSelectedDays() {
            val editor = preferences.edit()
            editor.putString(SELECTED_DATE, "")
            editor.apply()
        }

        fun saveUsersToPreferences(response: UserResponse?) {
            val editor: SharedPreferences.Editor = preferences.edit()
            val builder = GsonBuilder()
            val gson = builder.serializeNulls().create()
            val responseString = gson.toJson(response)
            editor.putString(USERS, responseString)
            editor.apply()
        }

        fun loadUsersFromPreferences(): UserResponse? {
            val responseString = preferences.getString(USERS, null)
            var response: UserResponse? = null
            if (responseString != null && responseString != "") {
                val builder = GsonBuilder()
                val gson = builder.serializeNulls().create()
                response = gson.fromJson(responseString, UserResponse::class.java)
            }
            return response
        }
    }
}