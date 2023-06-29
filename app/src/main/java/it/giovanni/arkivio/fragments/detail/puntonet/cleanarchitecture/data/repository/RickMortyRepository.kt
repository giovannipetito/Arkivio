package it.giovanni.arkivio.fragments.detail.puntonet.cleanarchitecture.data.repository

import io.reactivex.Single
import it.giovanni.arkivio.fragments.detail.puntonet.cleanarchitecture.data.ApiService
import it.giovanni.arkivio.fragments.detail.puntonet.cleanarchitecture.data.ApiResult
import it.giovanni.arkivio.fragments.detail.puntonet.cleanarchitecture.data.datasource.remote.RickMortyDataSource
import it.giovanni.arkivio.fragments.detail.puntonet.cleanarchitecture.data.response.RickMortyResponse
import it.giovanni.arkivio.fragments.detail.puntonet.cleanarchitecture.data.model.RickMorty
import javax.inject.Inject
import javax.inject.Singleton

/**
 * - RickMortyRepository è una repository class che implementa l'interfaccia RickMortyDataSource
 *   che definisce i metodi per il recupero dei dati, in particolare i metodi getAllCharacters
 *   sono l'implementazione dei metodi corrispondenti di RickMortyDataSource.
 * - È annotata con @Singleton, a indicare che verrà creata e condivisa solo una singola istanza
 *   di questa classe nell'applicazione.
 * - Il costruttore della classe RickMortyRepository riceve un'istanza di ApiService tramite dependency
 *   injection (@Inject), utilizza poi l'apiService iniettato per recuperare e restituire la response.
 *
 * Questa repository class funge da ponte o intermediario tra il User Layer e i livelli superiori
 * dell'app come il ViewModel, fornisce cioè un livello di astrazione tra ViewModel e datasource.
 * Recupera i dati da un datasource remoto rappresentato dall'interfaccia ApiService e li fornisce
 * ad un consumatore del repository, come il ViewModel, in un formato semplice da usare.
 */
@Singleton
class RickMortyRepository @Inject constructor(private val apiService: ApiService) : RickMortyDataSource {

    override suspend fun getAllCharactersV1(page: Int): RickMortyResponse {
        val response: RickMortyResponse = apiService.getAllCharacters(page)
        return response
    }

    override suspend fun getAllCharactersV2(page: Int): ApiResult<RickMortyResponse> {
        return try {
            val response: RickMortyResponse = apiService.getAllCharacters(page)
            ApiResult.Success(response)
        } catch (e: Exception) {
            ApiResult.Error(e.localizedMessage)
        }
    }

    override suspend fun getAllCharactersV3(page: Int): List<RickMorty> {
        val response: RickMortyResponse = apiService.getAllCharacters(page)
        val results: List<RickMorty> = response.results
        return results
    }

    override fun getAllCharactersV4(page: Int): Single<RickMortyResponse> {
        val observable: Single<RickMortyResponse> = apiService.getAllCharactersV4(page)
        return observable
    }
}