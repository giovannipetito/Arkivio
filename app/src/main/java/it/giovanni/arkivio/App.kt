package it.giovanni.arkivio

import android.app.Application
import android.content.Context
import it.giovanni.arkivio.persistence.UserPreferencesRepository
import it.giovanni.arkivio.utils.Typekit

class App : Application() {

    companion object {
        private var userPreferencesRepository: UserPreferencesRepository? = null

        lateinit var context: Context

        fun getInstance(): Companion {
            return this
        }

        fun getRepository(): UserPreferencesRepository? {
            return userPreferencesRepository
        }
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        userPreferencesRepository = UserPreferencesRepository(this)
        Typekit.getInstance()
            .addNormal(Typekit.createFromAsset(this, "fonts/fira_light.ttf"))
            .addBold(Typekit.createFromAsset(this, "fonts/fira_medium.ttf"))
            .addItalic(Typekit.createFromAsset(this, "fonts/fira_regular.ttf"))
    }
}