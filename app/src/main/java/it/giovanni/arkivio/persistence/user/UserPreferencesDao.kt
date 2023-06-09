package it.giovanni.arkivio.persistence.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Flowable

/**
 * DAO (Data Access Object): un DAO viene utilizzato per incamerare il codice che agirà sui dati,
 * e conterrà i veri e propri comandi per le operazioni CRUD. Tipicamente esistono più DAO in una
 * applicazione, ma non è necessario che ve ne siano tanti quante le Entity. In genere, ogni DAO
 * raccoglie tutte le interazioni che riguardano un sottosistema del software: se stiamo modellando,
 * ad esempio, l’accesso ai dati di una biblioteca, potremo avere un DAO per gestire i dati utente,
 * uno per il catalogo libri, uno per i prestiti e così via.
 */

@Dao
interface UserPreferencesDao {

    /**
     * Get a user by id.
     * @return the user from the table with a specific id.
     */
    @Query("SELECT * from UserPreferences")
    fun getPreferences(): Flowable<List<UserPreferencesEntity>>

    /**
     * Insert a user in the database. If the user already exists, replace it.
     * @param preference the preference key value to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPreference(preference: UserPreferencesEntity)

    /**
     * Delete all preferences.
     */
    @Query("DELETE FROM UserPreferences")
    fun deleteAllPreferences()
}