package it.giovanni.arkivio.restclient.retrofit

import retrofit2.Call
import retrofit2.http.GET

/**
 * Questa interfaccia contiente i vari metodi per l'interrogazione del servizio.
 * Molto importante è l'annotazione nella quale indichiamo quale servizio prendere dall'url; indatti
 * la libreria richiede un url base (che in questo caso è "https://jsonplaceholder.typicode.com/") al
 * quale aggiungerà in automatico il servizio prendendolo dalla annotazione (in questo caso "users").
 */

interface RetrofitService {

    @GET("users")
    fun getUsers(): Call<List<User?>?>?
}