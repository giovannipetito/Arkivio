package it.giovanni.arkivio.puntonet.cleanarchitecture.domain.usecase

import androidx.paging.PagingSource
import androidx.paging.PagingState
import it.giovanni.arkivio.puntonet.cleanarchitecture.data.model.RickMorty
import it.giovanni.arkivio.puntonet.cleanarchitecture.data.datasource.remote.RickMortyDataSource
import it.giovanni.arkivio.puntonet.cleanarchitecture.data.response.RickMortyResponse
import retrofit2.HttpException
import java.io.IOException

/**
 * Questa è una classe che estende la classe astratta PagingSource, che è un componente chiave
 * della libreria Paging3 in Android.
 *
 * RickMortyPagingSource recupera i dati da un'API in modo impaginato. Il metodo load viene
 * chiamato ogni volta che RecyclerView necessita di più dati da visualizzare e accetta un
 * oggetto LoadParams come argomento, che contiene informazioni sullo stato di caricamento
 * corrente (come il numero di pagina).
 *
 * All'interno del metodo load, il numero della pagina corrente è ottenuto dalla variabile
 * params.key. Il metodo AppModule.getCharacters(currentPage) viene quindi chiamato per
 * recuperare i dati per quella pagina.
 *
 * I risultati della chiamata API vengono quindi convertiti in un elenco di oggetti RickMorty
 * e archiviati nell'elenco responseData. Questo elenco viene quindi utilizzato per costruire
 * un oggetto LoadResult.Page, che viene restituito al chiamante.
 *
 * L'oggetto LoadResult.Page contiene i dati da visualizzare in RecyclerView, nonché le proprietà
 * prevKey e nextKey, che indicano rispettivamente le chiavi delle pagine di dati precedente e
 * successiva. Se non esiste una pagina precedente o successiva, queste proprietà vengono impostate
 * su null.
 *
 * Qui viene implementato anche il metodo getRefreshKey, che se restituisce null indica che non è
 * necessario aggiornare i dati.
 */
class RickMortyPagingSource constructor(private val rickMortyDataSource: RickMortyDataSource) : PagingSource<Int, RickMorty>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RickMorty> {

        return try {
            val currentPage = params.key ?: 1
            val response: RickMortyResponse = rickMortyDataSource.getAllCharactersV1(currentPage)

            val mutableListOfRickMorty = mutableListOf<RickMorty>()
            mutableListOfRickMorty.addAll(response.results)

            LoadResult.Page(
                data = mutableListOfRickMorty,
                prevKey = if (currentPage == 1) null else currentPage.minus(1),
                nextKey = if (response.results.isEmpty()) null else currentPage.plus(1)
            )
        } catch (exception: IOException) {
            exception.printStackTrace()
            val error = IOException("Please Check Internet Connection")
            LoadResult.Error(error)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, RickMorty>): Int? {
        return state.anchorPosition
    }
}