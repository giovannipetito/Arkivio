package it.giovanni.arkivio.persistence

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import androidx.room.Room
import io.reactivex.android.schedulers.AndroidSchedulers

import it.giovanni.arkivio.persistence.user.UserPreferencesEntity

class UserPreferencesRepository(context: Context) {

    private val dbName = "giovannideveloper.db"
    private var prefereces: List<UserPreferencesEntity> = ArrayList()
    var appRoomDatabase: AppRoomDatabase? = null

    companion object {
        // key
        var KEY_PREFERENCE_CONTACT: String = "CONTACT"
    }

    init {
        appRoomDatabase = Room.databaseBuilder(context, AppRoomDatabase::class.java, dbName).build()
    }

    @SuppressLint("CheckResult")
    fun loadPreferences() {
        appRoomDatabase?.userPreferencesDao()?.getPreferences()?.observeOn(AndroidSchedulers.mainThread())?.subscribe {
            setPreferences(it)
        }
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

        val userPreference = UserPreferencesEntity(key, value)

        object : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg voids: Void): Void? {
                appRoomDatabase?.userPreferencesDao()?.insertPreference(userPreference)
                return null
            }
        }.execute()
    }

    private fun setPreferences(preferenceList: List<UserPreferencesEntity>) {
        this.prefereces = preferenceList
    }
}