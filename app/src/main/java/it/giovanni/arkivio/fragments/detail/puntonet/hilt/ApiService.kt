package it.giovanni.arkivio.fragments.detail.puntonet.hilt

import io.reactivex.Single
import it.giovanni.arkivio.fragments.detail.puntonet.retrofitpaging.UsersResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("/api/users")
    suspend fun getUsers(
        @Query("page") page: Int,
    ): UsersResponse

    /*
    @GET("/api/users")
    fun getUsers(
        @Query("page") page: Int,
    ): Single<UsersResponse>
    */
}