package it.giovanni.kotlin.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import it.giovanni.kotlin.persistence.user.UserPreferences
import it.giovanni.kotlin.persistence.user.UserPreferencesDao

/**
 * The Room database that contains the Users table
 */
@Database(entities = [UserPreferences::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userPreferencesDao(): UserPreferencesDao
}