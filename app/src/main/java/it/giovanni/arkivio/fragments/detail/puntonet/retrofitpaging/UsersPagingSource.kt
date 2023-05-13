package it.giovanni.arkivio.fragments.detail.puntonet.retrofitpaging

import androidx.paging.PagingSource
import androidx.paging.PagingState

/**
 * Questa è una classe che estende la classe astratta PagingSource, che è un componente chiave
 * della libreria Paging3 in Android.
 *
 * UsersPagingSource recupera i dati da un'API in modo impaginato. Il metodo load viene
 * chiamato ogni volta che RecyclerView necessita di più dati da visualizzare e accetta un
 * oggetto LoadParams come argomento, che contiene informazioni sullo stato di caricamento
 * corrente (come il numero di pagina).
 *
 * All'interno del metodo load, il numero della pagina corrente è ottenuto dalla variabile
 * params.key. Il metodo AppModule.getCharacters(currentPage) viene quindi chiamato per
 * recuperare i dati per quella pagina.
 *
 * I risultati della chiamata API vengono quindi convertiti in un elenco di oggetti Data
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
class UsersPagingSource : PagingSource<Int, Data>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Data> {

        return try {

            val data: List<Data>
            val currentPage = params.key ?: 1

            data = when (val result: Result<UsersResponse> = ApiServiceFactory.getUsers(currentPage)) {
                is Result.Success<UsersResponse> -> {
                    result.data.data ?: emptyList()
                }

                is Result.Error -> {
                    // todo: show error message
                    result.message
                    result.statusCode

                    emptyList()
                }
            }

            val mutableListOfData = mutableListOf<Data>()
            mutableListOfData.addAll(data)

            LoadResult.Page(
                data = mutableListOfData,
                prevKey = if (currentPage == 1) null else -1,
                nextKey = currentPage.plus(1)
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Data>): Int? {
        return null
    }
}