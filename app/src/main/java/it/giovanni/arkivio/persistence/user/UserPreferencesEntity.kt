package it.giovanni.arkivio.persistence.user

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity: ogni classe di questo tipo rappresenta una tabella del database.
 * All’interno delle classi Entity predisporremo tante variabili d’istanza quanti sono i campi
 * previsti dallo schema della tabella, più altri eventuali membri che si renderanno necessari;
 */
@Entity(tableName = "UserPreferences")
class UserPreferencesEntity(
    @PrimaryKey
    @ColumnInfo(name = "key")
    val key: String,
    @ColumnInfo(name = "value")
    val value: String
)