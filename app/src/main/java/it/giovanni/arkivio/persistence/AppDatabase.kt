package it.giovanni.arkivio.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import it.giovanni.arkivio.persistence.user.UserPreferences
import it.giovanni.arkivio.persistence.user.UserPreferencesDao

/**
 * The Room database that contains the Users table
 */
@Database(entities = [UserPreferences::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userPreferencesDao(): UserPreferencesDao
}