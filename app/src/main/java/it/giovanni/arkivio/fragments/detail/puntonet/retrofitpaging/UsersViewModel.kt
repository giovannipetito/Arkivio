package it.giovanni.arkivio.fragments.detail.puntonet.retrofitpaging

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * UsersViewModel estende la classe ViewModel dal package androidx.lifecycle. Ciò significa che avrà
 * un ciclo di vita legato a una activity o a un fragment e sarà in grado di conservare i dati ed
 * eseguire azioni che sopravvivono alle modifiche della configurazione (come le rotazioni dello schermo).
 *
 * Questo ViewModel è responsabile del recupero di una lista di users da una API e del loro mapping
 * (trasformazione) in una lista di elementi di dati dell'interfaccia utente (UI data Items).
 *
 * La classe ha un'istanza MutableLiveData di una lista di UserDataItem. Questa lista è privata
 * ed è accessibile tramite un'istanza pubblica di LiveData denominata list. Ciò consente ad altri
 * componenti di osservare le modifiche alla lista.
 *
 * Il metodo fetchUsers è responsabile del recupero della lista di users dall'API. Avvia una coroutine
 * utilizzando la property viewModelScope dal package kotlinx.coroutines. Questo scope è legato al
 * ciclo di vita del ViewModel e annulla automaticamente tutte le coroutine quando il ViewModel non
 * è più in uso.
 *
 * All'interno della coroutine, chiama il metodo ApiServiceClient.getListUsers(page) per recuperare i
 * dati dall'API. Questo metodo restituisce un oggetto Result, che può essere un'istanza Success o Error.
 *
 * Se il risultato è un'istanza Success, mappa l'elenco di users in un elenco di UserDataItem
 * chiamando il metodo mapUsersDataItem. Questo metodo crea una nuova lista di data items mappando
 * ogni user a un'istanza di UserDataItem. Questo nuovo elenco viene quindi impostato sulla
 * variabile _list utilizzando la property value.
 *
 * Se il risultato è un'istanza Error, non modifica la lista e può potenzialmente mostrare un messaggio
 * di errore (che è attualmente commentato).
 *
 * * * * * * * * * * * * * * * * * * * * * PAGING LIBRARY * * * * * * * * * * * * * * * * * * * * *
 *
 * UsersViewModel inoltre usa le classi Pager e PagingConfig della libreria AndroidX Paging per
 * fornire un flusso di oggetti PagingData ai suoi osservatori.
 *
 * La proprietà listData è un flusso di oggetti PagingData<Data>, che possono essere osservati da
 * view o altri componenti nell'app Android. La funzione Pager viene usata per creare un flusso
 * PagingData basato su un PagingSource. L'oggetto PagingConfig specifica il comportamento di
 * paging, ad esempio le dimensioni della pagina e la distanza di prelettura.
 *
 * In questo caso, UsersPagingSource viene utilizzato come PagingSource. Questa classe estende
 * la classe PagingSource ed è responsabile del caricamento dei dati nel flusso PagingData.
 */
class UsersViewModel : ViewModel() {

    private val _usersDataItem: MutableLiveData<List<UserDataItem>> = MutableLiveData<List<UserDataItem>>()
    val usersDataItem: LiveData<List<UserDataItem>>
        get() = _usersDataItem

    private val _users: MutableLiveData<List<Data>> = MutableLiveData<List<Data>>()
    val users: LiveData<List<Data>>
        get() = _users

    val _user: MutableLiveData<User> = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    private val _message: MutableLiveData<String> = MutableLiveData<String>()
    val message: LiveData<String>
        get() = _message

    /**
     * Get Users (@GET)
     */
    fun fetchUsers(page: Int) {
        viewModelScope.launch {
            when (val result: Result<UsersResponse> = ApiServiceClient.getUsers(page)) {
                is Result.Success<UsersResponse> -> {

                    result.data.data?.let {
                        mapUsersDataItem(it)
                    }

                    // Oppure, semplicemente:
                    _users.value = result.data.data
                }
                is Result.Error -> {
                    // todo: show error message
                }
            }
        }
    }

    /**
     * Get Users (@GET) & Paging
     */
    val listData: Flow<PagingData<Data>> = Pager(PagingConfig(pageSize = 1)) {
        UsersPagingSource(/* apiService */)

    }.flow.cachedIn(viewModelScope)

    /**
     * Add User (@POST)
     */
    fun addUser(user: User) {
        viewModelScope.launch {
            when (val result = ApiServiceClient.addUser(user)) {
                is Result.Success<UserResponse> -> {

                    val info = "name: " + result.data.name + "\n" +
                            "job: " + result.data.job + "\n" +
                            "id: " + result.data.id + "\n" +
                            "createdAt: " + result.data.createdAt

                    _message.value = info
                }
                is Result.Error -> {
                    // todo: show error message
                }
            }
        }
    }

    private fun mapUsersDataItem(data: List<Data>) {
        _usersDataItem.value = data.map {
            UserDataItem(
                it.id,
                it.email,
                it.firstName,
                it.lastName,
                it.avatar,
            )
        }
    }
}