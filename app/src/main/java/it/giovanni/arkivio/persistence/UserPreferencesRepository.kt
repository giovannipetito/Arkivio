package it.giovanni.arkivio.persistence

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import androidx.room.Room
import io.reactivex.android.schedulers.AndroidSchedulers

import it.giovanni.arkivio.persistence.user.UserPreferences

class UserPreferencesRepository(context: Context) {

    private val DB_NAME = "giovannideveloper.db"
    private var prefereces: List<UserPreferences> = ArrayList()
    var appDatabase: AppDatabase? = null

    companion object {
        // key
        var KEY_PREFERENCE_SEDE: String = "SEDE"
        var KEY_FINGERPRINT_ENABLED: String = "FINGERPRINT_ENABLED"
        var KEY_FCM_TOPICS: String = "KEY_FCM_TOPICS"

        var KEY_PREFERENCE_CONTACT: String = "CONTACT"
    }

    init {
        appDatabase = Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME).build()
    }

    @SuppressLint("CheckResult")
    fun loadPreferences() {
        appDatabase!!.userPreferencesDao()
            .getPreferences()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {setPreferences(it)}
    }

    fun getPreference(key: String): String {
        val iterator = prefereces.iterator()
        while (iterator.hasNext()) {
            val preference = iterator.next()
            if (key == preference.key)
                return preference.value
        }
        return ""
    }

    @SuppressLint("StaticFieldLeak")
    fun setPreference(key: String, value: String) {

        val userPreference = UserPreferences(key, value)

        object : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg voids: Void): Void? {
                appDatabase!!.userPreferencesDao().insertPreference(userPreference)
                return null
            }
        }.execute()
    }

    private fun setPreferences(preferenceList: List<UserPreferences>) {
        this.prefereces = preferenceList
    }
}