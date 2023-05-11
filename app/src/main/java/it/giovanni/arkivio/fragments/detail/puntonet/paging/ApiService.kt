package it.giovanni.arkivio.fragments.detail.puntonet.paging

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("character")
    suspend fun getAllCharacters(
       // @Query("count") size:Int,
        @Query("page") page: Int

    ): Response<RickMortyResponse>
}