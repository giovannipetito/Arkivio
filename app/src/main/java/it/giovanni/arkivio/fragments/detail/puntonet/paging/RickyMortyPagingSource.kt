package it.giovanni.arkivio.fragments.detail.puntonet.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState

/**
 * Questa è una classe che estende la classe astratta PagingSource, che è un componente chiave
 * della libreria Paging3 in Android.
 *
 * RickyMortyPagingSource recupera i dati da un'API in modo impaginato. Il metodo load viene
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
 * Qui viene implementato anche il metodo getRefreshKey, che in questo caso restituisce null,
 * indicando che non è necessario aggiornare i dati.
 */
class RickyMortyPagingSource : PagingSource<Int, RickMorty>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RickMorty> {

        return try {
            val currentPage = params.key ?: 1
            val response = AppModule.getCharacters(currentPage)

            val responseData = mutableListOf<RickMorty>()
            val data = response.body()?.results ?: emptyList()
            responseData.addAll(data)

            LoadResult.Page(
                data = responseData,
                prevKey = if (currentPage == 1) null else -1,
                nextKey = currentPage.plus(1)
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, RickMorty>): Int? {
        return null
    }
}