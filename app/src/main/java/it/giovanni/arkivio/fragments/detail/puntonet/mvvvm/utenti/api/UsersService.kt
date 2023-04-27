package it.giovanni.arkivio.fragments.detail.puntonet.mvvvm.utenti.api

import it.giovanni.arkivio.restclient.retrofit.User
import retrofit2.Call
import retrofit2.http.GET

/**
 * UsersService is the interface that defines the API endpoints. In this case, it should contain
 * a method that returns a Call object for retrieving utente data:
 */
interface UsersService {

    @GET("users")
    fun getUsers(): Call<List<User?>?>?
}