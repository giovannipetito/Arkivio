package it.giovanni.arkivio.fragments.detail.nearby.beacons

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import com.google.android.gms.nearby.messages.Message
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class NearbyBeaconsUtils {

    companion object {

        const val KEY_CACHED_MESSAGES = "cachedMessages"

        // Return  A list (possibly empty) containing message strings.
        fun getCachedMessages(context: Context): List<String> {
            val sharedPrefs = getSharedPreferences(context)
            val cachedMessagesJson = sharedPrefs.getString(KEY_CACHED_MESSAGES, "")
            return if (TextUtils.isEmpty(cachedMessagesJson)) {
                emptyList()
            } else {
                val type = object : TypeToken<List<String?>?>() {}.type
                Gson().fromJson(cachedMessagesJson, type)
            }
        }

        // The Message whose payload (as string) is saved to SharedPreferences.
        fun saveFoundMessage(context: Context, message: Message) {
            val cachedMessages = ArrayList(getCachedMessages(context))
            val cachedMessagesSet: Set<String> = HashSet(cachedMessages)
            val messageString = String(message.content)

            if (!cachedMessagesSet.contains(messageString)) {
                cachedMessages.add(0, String(message.content))
                getSharedPreferences(context)
                    .edit()
                    .putString(KEY_CACHED_MESSAGES, Gson().toJson(cachedMessages))
                    .apply()
            }
        }

        // The Message whose payload (as string) is removed from SharedPreferences.
        fun removeLostMessage(context: Context, message: Message) {
            val cachedMessages = ArrayList(getCachedMessages(context))
            cachedMessages.remove(String(message.content))
            getSharedPreferences(context)
                .edit()
                .putString(KEY_CACHED_MESSAGES, Gson().toJson(cachedMessages))
                .apply()
        }

        // Gets the SharedPReferences object that is used for persisting data in this application.
        // Return The single instance that can be used to retrieve and modify values.
        private fun getSharedPreferences(context: Context): SharedPreferences {
            return context.getSharedPreferences(
                context.applicationContext.packageName,
                Context.MODE_PRIVATE
            )
        }
    }
}