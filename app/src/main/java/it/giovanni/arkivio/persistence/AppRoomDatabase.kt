package it.giovanni.arkivio.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import it.giovanni.arkivio.persistence.user.UserPreferencesEntity
import it.giovanni.arkivio.persistence.user.UserPreferencesDao

/**
 * Il database Room che contiene la tabella Users.
 * Room Database rappresenta l’astrazione del database su cui vogliamo lavorare.
 * Verrà creata una classe di questo tipo che costituirà il punto di accesso ai
 * dati: qualsiasi operazione da svolgere passerà di qui.
 */
@Database(entities = [UserPreferencesEntity::class], version = 1, exportSchema = false)
abstract class AppRoomDatabase : RoomDatabase() {
    abstract fun userPreferencesDao(): UserPreferencesDao
}