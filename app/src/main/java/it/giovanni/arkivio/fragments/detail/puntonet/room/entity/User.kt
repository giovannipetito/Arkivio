package it.giovanni.arkivio.fragments.detail.puntonet.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity: una entity rappresenta una tabella nel database. È una classe Java o Kotlin annotata che
 * definisce la struttura e i campi della tabella. Ogni istanza della classe entity rappresenta una
 * riga nella tabella.
 * @PrimaryKey(autoGenerate = true) indica che la libreria Room genererà automaticamente i valori
 * numerici degli id di ogni riga (o colonna).
 */
@Entity(tableName = "users_table")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    var firstName: String,
    var lastName: String,
    var age: Int
)