package it.giovanni.arkivio.fragments.detail.puntonet.getpost

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * ApiService è un'interfaccia che definisce un endpoint API HTTP per il recupero di una lista di
 * tutti i team NBA da un server remoto.
 *
 * L'interfaccia definisce un singolo metodo chiamato getListUsers che è annotato con @GET("data").
 * Questa annotazione specifica che il metodo HTTP utilizzato per questa chiamata API è GET e che
 * l'URL dell'endpoint è "data".
 *
 * Il metodo accetta una solo parametro page che è annotato con @Query("page"). Questa annotazione
 * specifica che il parametro verrà inviato come parametro di query nell'URL della request con il
 * nome "page".
 *
 * Il tipo restituito del metodo è ListUsersResponse. Il modificatore suspend prima della firma del
 * metodo indica che questo metodo è una coroutine e può essere utilizzato con le funzioni suspend.
 *
 * Quando viene chiamato questo metodo, Retrofit (libreria di networking per Android) effettuerà una
 * request HTTP GET all'URL specificato con il parametro di query fornito. Il server risponderà
 * quindi con un payload JSON che verrà deserializzato in un oggetto ListUsersResponse da Gson (una
 * libreria di serializzazione/deserializzazione JSON per Java). L'oggetto ListUsersResponse contiene
 * una lista di oggetti Data che rappresentano i team NBA.
 */
interface ApiService {

    @GET("/api/users")
    suspend fun getListUsers(
        @Query("page") page: Int,
    ): ListUsersResponse

    @POST("/api/users")
    suspend fun addUser(@Body user: User): UserResponse
}