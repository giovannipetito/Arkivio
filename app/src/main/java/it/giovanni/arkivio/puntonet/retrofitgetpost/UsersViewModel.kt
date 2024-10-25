package it.giovanni.arkivio.puntonet.retrofitgetpost

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.giovanni.arkivio.puntonet.cleanarchitecture.data.ApiResult
import kotlinx.coroutines.launch

/**
 * UsersViewModel estende la classe ViewModel dal package androidx.lifecycle. Ciò significa che avrà
 * un ciclo di vita legato a un'activity o fragment e sarà in grado di conservare i dati ed eseguire
 * azioni che sopravvivono alle modifiche della configurazione (come le rotazioni dello schermo).
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
 * All'interno della coroutine, chiama il metodo ApiServiceClient.getListUsers per recuperare i dati
 * dall'API. Questo metodo restituisce un oggetto ApiResult, che può essere un'istanza Success o Error.
 *
 * Se il risultato è un'istanza Success, mappa l'elenco di users in un elenco di UserDataItem
 * chiamando il metodo mapUsersDataItem. Questo metodo crea una nuova lista di data items mappando
 * ogni user a un'istanza di UserDataItem. Questo nuovo elenco viene quindi impostato sulla
 * variabile _list utilizzando la property value.
 *
 * Se il risultato è un'istanza Error, non modifica la lista e può potenzialmente mostrare un
 * messaggio di errore (che è attualmente commentato).
 */
class UsersViewModel : ViewModel() {

    private val _usersDataItem: MutableLiveData<List<UserDataItem>> = MutableLiveData<List<UserDataItem>>()
    val usersDataItem: LiveData<List<UserDataItem>>
        get() = _usersDataItem

    /**
     * _users è un'istanza MutableLiveData che contiene una lista di oggetti User. Rappresenta
     * la versione mutabile di LiveData che può essere aggiornata all'interno di ViewModel.
     */
    private val _users: MutableLiveData<List<User>> = MutableLiveData<List<User>>()

    /**
     * users è un'istanza LiveData derivata da _users. Fornisce accesso in sola lettura alla lista
     * di oggetti User ai componenti di osservazione.
     */
    val users: LiveData<List<User>>
        get() = _users

    val _utente: MutableLiveData<Utente> = MutableLiveData<Utente>()
    val utente: LiveData<Utente>
        get() = _utente

    private val _message: MutableLiveData<String> = MutableLiveData<String>()
    val message: LiveData<String>
        get() = _message

    /**
     * Get Users (@GET)
     * fetchUsers() è responsabile del recupero degli utenti. Utilizza le coroutine Kotlin e
     * viewModelScope per lanciare una nuova coroutine. All'interno della coroutine, chiama il
     * metodo getUsers() dall'apiService per recuperare la response degli utenti per la pagina
     * specificata. Quindi, aggiorna _users inviando il nuovo valore di response.data utilizzando il
     * metodo postValue(). Poiché questo aggiornamento viene eseguito all'interno di viewModelScope,
     * verrà automaticamente annullato quando il ViewModel viene cancellato.
     */
    fun fetchUsers(page: Int) {
        viewModelScope.launch {
            when (val apiResult: ApiResult<UsersResponse> = ApiServiceClient.getUsers(page)) {
                is ApiResult.Success<UsersResponse> -> {

                    apiResult.data.users?.let {
                        mapUsersDataItem(it)
                    }

                    // Oppure, semplicemente:
                    _users.value = apiResult.data.users
                }
                is ApiResult.Error -> {
                    // todo: show error message
                }
            }
        }
    }

    /**
     * Add User (@POST)
     */
    fun addUser(utente: Utente) {
        viewModelScope.launch {
            when (val result = ApiServiceClient.addUser(utente)) {
                is ApiResult.Success<UtenteResponse> -> {

                    val info = "name: " + result.data.name + "\n" +
                            "job: " + result.data.job + "\n" +
                            "id: " + result.data.id + "\n" +
                            "createdAt: " + result.data.createdAt

                    _message.value = info
                }
                is ApiResult.Error -> {
                    // todo: show error message
                }
            }
        }
    }

    private fun mapUsersDataItem(users: List<User>) {
        _usersDataItem.value = users.map {
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