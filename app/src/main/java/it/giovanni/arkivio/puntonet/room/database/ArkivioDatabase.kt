package it.giovanni.arkivio.puntonet.room.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import it.giovanni.arkivio.puntonet.room.dao.UserCoroutinesDao
import it.giovanni.arkivio.puntonet.room.dao.UserRxJavaDao
import it.giovanni.arkivio.puntonet.room.dao.UsersWorkerDao
import it.giovanni.arkivio.puntonet.room.entity.User

/**
 * Database: la classe ArkivioDatabase è una classe astratta che estende RoomDatabase e rappresenta
 * il database Room nell'applicazione. Funge perciò da punto di ingresso principale per l'accesso
 * al database SQLite sottostante.
 *
 * L'annotazione @Database(entities = [User::class], version = 1) viene utilizzata per definire il
 * database Room. Specifica l'elenco di entità (tabelle) che verranno incluse nel database e il
 * numero di versione del database.
 *
 * Con i metodi astratti userCoroutinesDao e userRxJavaDao, fornisce l'accesso ai DAO per
 * l'esecuzione di operazioni di database usando coroutine e RxJava.
 *
 * Il companion object assicura che venga creata una sola istanza del database, seguendo il modello
 * singleton, e Room genera il codice di implementazione necessario per la creazione e la gestione
 * dell'istanza del database.
 *
 * INSTANCE è una variabile volatile che contiene l'istanza singleton di ArkivioDatabase. @Volatile
 * garantisce che il valore di INSTANCE sia sempre aggiornato e visibile a tutti i thread.
 *
 * getDatabase è un metodo statico che fornisce l'accesso all'istanza di ArkivioDatabase. Prende un
 * context come parametro e restituisce un'istanza di ArkivioDatabase. Segue il modello singleton,
 * il che significa che garantisce la creazione di una sola istanza del database. All'interno del
 * metodo, controlla innanzitutto se esiste un'istanza esistente del database (tempInstance).
 * Se c'è, restituisce quell'istanza. Se non esiste un'istanza esistente, entra in un blocco
 * synchronized per impedire a più thread di creare più istanze contemporaneamente. Dentro
 * il blocco synchronized, crea una nuova istanza di ArkivioDatabase utilizzando il metodo
 * Room.databaseBuilder, specificando il context, la classe ArkivioDatabase e il nome del
 * database. Una volta creata l'istanza, la assegna alla variabile INSTANCE e la restituisce.
 */
@Database(entities = [User::class, it.giovanni.arkivio.puntonet.retrofitgetpost.User::class], version = 1)
abstract class ArkivioDatabase : RoomDatabase() {

    abstract fun userCoroutinesDao(): UserCoroutinesDao

    abstract fun userRxJavaDao(): UserRxJavaDao

    abstract fun usersWorkerDao(): UsersWorkerDao

    companion object {

        @Volatile
        private var INSTANCE: ArkivioDatabase? = null

        fun getDatabase(context: Context): ArkivioDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ArkivioDatabase::class.java,
                    "arkivio_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}