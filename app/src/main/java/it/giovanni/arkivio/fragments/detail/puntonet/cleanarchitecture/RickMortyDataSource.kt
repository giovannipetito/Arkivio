package it.giovanni.arkivio.fragments.detail.puntonet.cleanarchitecture

interface RickMortyDataSource {

    suspend fun getAllCharactersV1(page: Int): RickMortyResponse

    suspend fun getAllCharactersV2(page: Int): Result<RickMortyResponse>

    suspend fun getAllCharactersV3(page: Int): List<RickMorty>
}