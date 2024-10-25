package it.giovanni.arkivio.puntonet.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import it.giovanni.arkivio.puntonet.room.entity.User

/**
 * DAO (User Access Object): un DAO è un'interfaccia che definisce le operazioni del database
 * (ad esempio: insert, update, delete, query). Annotando i metodi nell'interfaccia DAO, si
 * specificano le query SQL o le operazioni da eseguire sul database. Room genera automaticamente
 * il codice di implementazione necessario per questi metodi.
 */
@Dao
interface UserCoroutinesDao {

    /**
     * Possiamo recuperare i dati usando la parola chiave ASC per ordinare i dati in modo
     * ascendente (dal minore al maggiore).
     */
    @Query("SELECT * FROM users_table ORDER BY id ASC")
    suspend fun getUsers(): List<User>

    @Query("SELECT * FROM users_table ORDER BY id ASC")
    fun getUsers1(): LiveData<List<User>>

    @Query("SELECT * FROM users_table ORDER BY id ASC")
    fun getUsers2(): List<User>

    /**
     * Inserisce un utente nel database. Se l'utente esiste già, lo ignora.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    @Query("DELETE FROM users_table")
    suspend fun deleteUsers()
}