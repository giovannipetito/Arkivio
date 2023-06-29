package it.giovanni.arkivio.fragments.detail.puntonet.cleanarchitecture.data.datasource.remote

import io.reactivex.Single
import it.giovanni.arkivio.fragments.detail.puntonet.cleanarchitecture.data.ApiResult
import it.giovanni.arkivio.fragments.detail.puntonet.cleanarchitecture.data.response.RickMortyResponse
import it.giovanni.arkivio.fragments.detail.puntonet.cleanarchitecture.data.model.RickMorty

/**
 * - L'interfaccia RickMortyDataSource definisce il contratto per l'accesso al datasource relativo
 *   all'API remota.
 * - Dichiara tre funzioni di sospensione (suspend) getAllCharacters che prendono un parametro
 *   page e restituiscono una response.
 *
 * Questa interfaccia fornisce un livello di astrazione tra il User Layer e il Domain Layer
 * consentendo al Domain Layer di interagire con il datasource in modo standard senza la necessità
 * di conoscere i dettagli dell'implementazione. Utilizzando un'interfaccia infatti,
 * l'implementazione effettiva può essere facilmente sostituita o mockata in fase test.
 */
interface RickMortyDataSource {

    suspend fun getAllCharactersV1(page: Int): RickMortyResponse

    suspend fun getAllCharactersV2(page: Int): ApiResult<RickMortyResponse>

    suspend fun getAllCharactersV3(page: Int): List<RickMorty>

    fun getAllCharactersV4(page: Int): Single<RickMortyResponse>
}