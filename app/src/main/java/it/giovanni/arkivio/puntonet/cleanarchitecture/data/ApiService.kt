package it.giovanni.arkivio.puntonet.cleanarchitecture.data

import io.reactivex.rxjava3.core.Single
import it.giovanni.arkivio.puntonet.cleanarchitecture.data.response.RickMortyResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("character")
    suspend fun getAllCharacters(
        @Query("page") page: Int

    ): RickMortyResponse

    @GET("character")
    fun getAllCharactersV4(
        @Query("page") page: Int

    ): Single<RickMortyResponse>
}