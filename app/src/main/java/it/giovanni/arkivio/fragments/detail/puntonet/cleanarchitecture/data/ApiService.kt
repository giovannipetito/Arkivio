package it.giovanni.arkivio.fragments.detail.puntonet.cleanarchitecture.data

import it.giovanni.arkivio.fragments.detail.puntonet.cleanarchitecture.data.response.RickMortyResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("character")
    suspend fun getAllCharacters(
        @Query("page") page: Int

    ): RickMortyResponse
}