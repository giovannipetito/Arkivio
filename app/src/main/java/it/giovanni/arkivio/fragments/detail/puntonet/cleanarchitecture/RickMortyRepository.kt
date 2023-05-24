package it.giovanni.arkivio.fragments.detail.puntonet.cleanarchitecture

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RickMortyRepository @Inject constructor(private val apiService: ApiService) : RickMortyDataSource {

    override suspend fun getAllCharactersV1(page: Int): RickMortyResponse {
        val response: RickMortyResponse = apiService.getAllCharacters(page)
        return response
    }

    override suspend fun getAllCharactersV2(page: Int): Result<RickMortyResponse> {
        return try {
            val response: RickMortyResponse = apiService.getAllCharacters(page)
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(e.localizedMessage)
        }
    }

    override suspend fun getAllCharactersV3(page: Int): List<RickMorty> {
        val response: RickMortyResponse = apiService.getAllCharacters(page)
        val results: List<RickMorty> = response.results
        return results
    }
}