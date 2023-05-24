package it.giovanni.arkivio.fragments.detail.puntonet.cleanarchitecture

import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * PAGING LIBRARY
 * La classe RickMortyViewModel è un ViewModel che usa le classi Pager e PagingConfig della
 * libreria AndroidX Paging per fornire un flusso di oggetti PagingData ai suoi osservatori.
 *
 * Il metodo getDataFlow restituisce un flusso di oggetti PagingData<RickMorty> che possono essere
 * osservati da view o altri componenti nell'app Android. La funzione Pager viene utilizzata per
 * creare un flusso PagingData basato su un PagingSource. L'oggetto PagingConfig specifica il
 * comportamento di paging, ad esempio le dimensioni della pagina e la distanza di prelettura.
 *
 * In questo caso, RickMortyPagingSource viene utilizzato come PagingSource. Questa classe estende
 * la classe PagingSource ed è responsabile del caricamento dei dati nel flusso PagingData.
 *
 * DEPENDENCY INJECTION - HILT
 * RickMortyViewModel usa il framework Hilt per iniettare la dipendenza ApiService. L'annotazione
 * @HiltViewModel indica che questa classe deve essere fornita e gestita da Hilt come ViewModel. La
 * dichiarazione della classe RickMortyViewModel prende un'istanza dell'interfaccia ApiService come
 * parametro del costruttore, che verrà iniettato da Hilt.
 */
@HiltViewModel
class RickMortyViewModel @Inject constructor(private val apiService: ApiService) : ViewModel() {

    fun getDataFlow(): Flow<PagingData<RickMorty>> {

        val dataFlow: Flow<PagingData<RickMorty>> = Pager(PagingConfig(pageSize = 1)) {

            RickMortyPagingSource(apiService)

        }.flow.cachedIn(viewModelScope)

        return dataFlow
    }
}