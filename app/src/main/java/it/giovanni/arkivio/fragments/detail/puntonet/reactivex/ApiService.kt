package it.giovanni.arkivio.fragments.detail.puntonet.reactivex

import io.reactivex.Single
import it.giovanni.arkivio.fragments.detail.puntonet.retrofitgetpost.UsersResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * The interface ApiService represents an API service interface with a method to retrieve users
 * from a specific endpoint, taking the page number as a parameter and returning a Single that
 * emits a UsersResponse object.
 *
 * The Single class from the RxJava library represents a stream of data that will emit either a
 * single response item or an error.
 * @GET and @Query are annotations from the Retrofit library that define the HTTP request methods
 * and parameters.
 * The UsersResponse is a custom class representing the response data structure for retrieving users.
 *
 * The ApiService interface declares the getUsers method. This method is annotated with
 * @GET("/api/users"), which specifies the HTTP GET request with the endpoint "/api/users".
 * It takes a single parameter page of type Int, annotated with @Query("page"), which allows
 * passing the page number as a query parameter.
 * The return type of the method is Single<UsersResponse>, indicating that it will emit a
 * UsersResponse object wrapped in a Single.
 *
 * Quando aggiungi RxJava2CallAdapterFactory alla tua istanza di Retrofit utilizzando il metodo
 * addCallAdapterFactory() in ApiServiceClient, Retrofit eseguirà automaticamente il wrapping
 * delle risposte alla chiamata API nel tipo RxJava2 appropriato.
 *
 * Se ad esempio non utilizzassi RxJava2CallAdapterFactory, il tipo restituito del metodo getUsers
 * sarebbe Call<UsersResponse>, che è il tipo predefinito di Retrofit. Tuttavia, quando aggiungi
 * RxJava2CallAdapterFactory, Retrofit modificherà il tipo restituito in Single<UsersResponse>,
 * che è un tipo reattivo fornito da RxJava.
 *
 * Il tipo Single rappresenta un singolo valore o un errore. In questo caso, rappresenta un singolo
 * oggetto UsersResponse con all'interno una lista di utenti o un errore se la richiesta API fallisce.
 */
interface ApiService {

    @GET("/api/users")
    fun getUsers(
        @Query("page") page: Int,
    ): Single<UsersResponse>
}