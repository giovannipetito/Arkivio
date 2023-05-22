package it.giovanni.arkivio.fragments.detail.puntonet.paging

import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow

/**
 * La classe RickMortyViewModel è un ViewModel che usa le classi Pager e PagingConfig della libreria
 * AndroidX Paging per fornire un flusso di oggetti PagingData ai suoi osservatori.
 *
 * La proprietà listData è un flusso di oggetti PagingData<RickMorty>, che possono essere osservati
 * da viste o altri componenti nell'app Android. La funzione Pager viene utilizzata per creare un
 * flusso PagingData basato su un PagingSource. L'oggetto PagingConfig specifica il comportamento
 * di paging, ad esempio le dimensioni della pagina e la distanza di prelettura.
 *
 * In questo caso, RickMortyPagingSource viene utilizzato come PagingSource. Questa classe estende
 * la classe PagingSource ed è responsabile del caricamento dei dati nel flusso PagingData.
 *
 * Il parametro apiService è commentato, il che suggerisce che deve essere utilizzato per fornire un
 * riferimento al servizio API utilizzato per recuperare i dati. Nella classe RickMortyPagingSource,
 * la funzione AppModule.getCharacters(currentPage) viene utilizzata per recuperare i dati. Tuttavia,
 * è possibile che questa funzione possa essere sottoposta a refactoring per utilizzare invece il
 * parametro apiService.
 */
class RickMortyViewModel /* constructor(private val apiService: ApiService) */ : ViewModel() {

    fun getDataFlow(): Flow<PagingData<RickMorty>> {

        val dataFlow: Flow<PagingData<RickMorty>> = Pager(PagingConfig(pageSize = 1)) {

            RickMortyPagingSource(/* apiService */)

        }.flow.cachedIn(viewModelScope)

        return dataFlow
    }
}