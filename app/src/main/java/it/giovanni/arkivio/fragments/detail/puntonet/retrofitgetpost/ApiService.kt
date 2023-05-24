package it.giovanni.arkivio.fragments.detail.puntonet.retrofitgetpost

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * ApiService è un'interfaccia che definisce un endpoint API HTTP per il recupero di una lista di
 * users da un server remoto.
 *
 * L'interfaccia definisce un metodo chiamato getUsers che è annotato con @GET("/api/users").
 * Questa annotazione specifica che il metodo HTTP utilizzato per questa chiamata API è GET e che
 * l'URL dell'endpoint è "/api/users".
 *
 * Il metodo accetta una solo parametro page che è annotato con @Query("page"). Questa annotazione
 * specifica che il parametro verrà inviato come parametro di query nell'URL della request con il
 * nome "page".
 *
 * Il tipo restituito del metodo è UsersResponse. Il modificatore suspend prima della firma del
 * metodo indica che questo metodo è una coroutine e può essere utilizzato con le funzioni suspend.
 *
 * Quando viene chiamato questo metodo, Retrofit (libreria di networking per Android) effettuerà una
 * request HTTP GET all'URL specificato con il parametro di query fornito. Il server risponderà
 * quindi con un payload JSON che verrà deserializzato in un oggetto UsersResponse da Gson (una
 * libreria di serializzazione/deserializzazione JSON per Java). L'oggetto UsersResponse contiene
 * una lista di oggetti Data che rappresentano gli utenti.
 *
 * L'interfaccia inoltre ha un metodo, addUser, annotato con @POST("/api/users") per specificare
 * l'URL dell'endpoint relativo per la richiesta POST. L'annotazione @Body viene utilizzata per
 * specificare il corpo della richiesta come oggetto User, che viene passato come parametro al
 * metodo. Il metodo restituisce un oggetto UserResponse, che è una response dal server dopo
 * l'aggiunta dell'user. La keyword suspend viene utilizzata per contrassegnare il metodo come
 * coroutine e consentirne la chiamata da una funzione di sospensione (suspend).
 */
interface ApiService {

    @GET("/api/users")
    suspend fun getUsers(
        @Query("page") page: Int,
    ): UsersResponse

    @POST("/api/users")
    suspend fun addUser(@Body user: User): UserResponse
}