package it.giovanni.kotlin.utils

import android.app.Application

class App : Application() {
    /*
    companion object {
        private var userPreferencesRepository: UserPreferencesRepository? = null

        @SuppressLint("StaticFieldLeak")
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
            .addNormal(Typekit.createFromAsset(this, "fonts/fira_sans_light.ttf"))
            .addBold(Typekit.createFromAsset(this, "fonts/fira_sans_medium.ttf"))
            .addItalic(Typekit.createFromAsset(this, "fonts/fira_sans_regular.ttf"))
    }
    */
}