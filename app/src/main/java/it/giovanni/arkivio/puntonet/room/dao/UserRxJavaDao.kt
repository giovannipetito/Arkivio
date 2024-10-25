package it.giovanni.arkivio.puntonet.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import it.giovanni.arkivio.puntonet.room.entity.User

/**
 * DAO (User Access Object): un DAO è un'interfaccia che definisce le operazioni del database
 * (ad esempio: insert, update, delete, query). Annotando i metodi nell'interfaccia DAO, si
 * specificano le query SQL o le operazioni da eseguire sul database. Room genera automaticamente
 * il codice di implementazione necessario per questi metodi.
 *
 * Flowable e Completable sono tipi RxJava utilizzati per gestire operazioni di database asincrone:
 *
 * 1. Flowable è un tipo di flusso reattivo fornito da RxJava che rappresenta un flusso di dati
 * che può essere emesso nel tempo. Viene utilizzato quando si vuole osservare continuamente le
 * modifiche in un set di dati. Nel contesto di Room, un Flowable può essere utilizzato per
 * osservare il risultato di una query di database che restituisce un elenco di elementi.
 *
 * Il metodo getUsers() restituisce un Flowable<List<User>>. Ciò significa che quando ti iscrivi a
 * questo Flowable, riceverai un flusso di elenchi di utenti man mano che i dati vengono aggiornati.
 * Ciò consente di osservare i cambiamenti nei dati dell'utente e di ricevere automaticamente gli
 * aggiornamenti ogni volta che i dati sottostanti nel database cambiano.
 *
 * 2. Completable è un tipo di flusso reattivo fornito da RxJava che rappresenta un task o una
 * operazione che viene completata correttamente o incontra un errore, senza emettere alcun dato.
 * Viene utilizzato quando si desidera eseguire un'operazione senza aspettarsi un risultato.
 *
 * I metodi insertUser(), updateUser() e deleteUser() restituiscono tutti Completable. Questi metodi
 * vengono utilizzati rispettivamente per inserire, aggiornare ed eliminare un utente nel database.
 * Quando ti iscrivi a queste istanze Completable, stai indicando che desideri eseguire l'operazione
 * di database corrispondente e non sei interessato al risultato o ai dati emessi.
 */
@Dao
interface UserRxJavaDao {

    /**
     * Possiamo recuperare i dati usando la parola chiave DESC per ordinare i dati in modo
     * discendente (dal maggiore al minore).
     */
    @Query("SELECT * FROM users_table ORDER BY id DESC")
    fun getUsers(): Flowable<List<User>>

    /**
     * Inserisce un utente nel database. Se l'utente esiste già, lo sostituisce.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: User): Completable

    @Update
    fun updateUser(user: User): Completable

    @Delete
    fun deleteUser(user: User): Completable

    @Query("DELETE FROM users_table")
    fun deleteUsers(): Completable
}