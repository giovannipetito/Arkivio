package it.giovanni.arkivio.fragments.detail.puntonet.retrofit2

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * ApiService è un'interfaccia che definisce un endpoint API HTTP per il recupero di una lista di
 * tutti i team NBA da un server remoto.
 *
 * L'interfaccia definisce un singolo metodo chiamato getAllTeams che è annotato con @GET("teams").
 * Questa annotazione specifica che il metodo HTTP utilizzato per questa chiamata API è GET e che
 * l'URL dell'endpoint è "teams".
 *
 * Il metodo accetta una solo parametro page che è annotato con @Query("page"). Questa annotazione
 * specifica che il parametro verrà inviato come parametro di query nell'URL della request con il
 * nome "page".
 *
 * Il tipo restituito del metodo è AllTeamsResponse. Il modificatore suspend prima della firma del
 * metodo indica che questo metodo è una coroutine e può essere utilizzato con le funzioni suspend.
 *
 * Quando viene chiamato questo metodo, Retrofit (libreria di networking per Android) effettuerà una
 * request HTTP GET all'URL specificato con il parametro di query fornito. Il server risponderà
 * quindi con un payload JSON che verrà deserializzato in un oggetto AllTeamsResponse da Gson (una
 * libreria di serializzazione/deserializzazione JSON per Java). L'oggetto AllTeamsResponse contiene
 * una lista di oggetti Team che rappresentano i team NBA.
 */
interface ApiService {

    @GET("teams")
    suspend fun getAllTeams(
        @Query("page") page: Int,
    ): AllTeamsResponse
}