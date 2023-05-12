package it.giovanni.arkivio.fragments.detail.puntonet.getpost

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

/**
 * GetPostViewModel estende la classe ViewModel dal package androidx.lifecycle. Ciò significa che avrà
 * un ciclo di vita legato a una activity o a un fragment e sarà in grado di conservare i dati ed
 * eseguire azioni che sopravvivono alle modifiche della configurazione (come le rotazioni dello schermo).
 *
 * Questo ViewModel è responsabile del recupero di una lista di users da una API e del loro mapping
 * (trasformazione) in una lista di elementi di dati dell'interfaccia utente (UI data Items).
 *
 * La classe ha un'istanza MutableLiveData di una lista di ListUsersDataItem. Questa lista è privata
 * ed è accessibile tramite un'istanza pubblica di LiveData denominata list. Ciò consente ad altri
 * componenti di osservare le modifiche alla lista.
 *
 * Il metodo fetchUsers è responsabile del recupero della lista di users dall'API. Avvia una coroutine
 * utilizzando la property viewModelScope dal package kotlinx.coroutines. Questo scope è legato al
 * ciclo di vita del ViewModel e annulla automaticamente tutte le coroutine quando il ViewModel non
 * è più in uso.
 *
 * All'interno della coroutine, chiama il metodo ApiServiceFactory.getListUsers(page) per recuperare i
 * dati dall'API. Questo metodo restituisce un oggetto Result, che può essere un'istanza Success o Error.
 *
 * Se il risultato è un'istanza Success, mappa l'elenco di users in un elenco di ListUsersDataItem
 * chiamando il metodo mapUsersDataItem. Questo metodo crea una nuova lista di data items mappando
 * ogni user a un'istanza di ListUsersDataItem. Questo nuovo elenco viene quindi impostato sulla
 * variabile _list utilizzando la property value.
 *
 * Se il risultato è un'istanza Error, non modifica la lista e può potenzialmente mostrare un messaggio
 * di errore (che è attualmente commentato).
 */

class GetPostViewModel : ViewModel() {

    private val _list: MutableLiveData<List<ListUsersDataItem>> = MutableLiveData<List<ListUsersDataItem>>()
    val list: LiveData<List<ListUsersDataItem>>
        get() = _list

    private val _listSer: MutableLiveData<List<Data>> = MutableLiveData<List<Data>>()
    val listSer: LiveData<List<Data>>
        get() = _listSer

    private val _responseMsg: MutableLiveData<String> = MutableLiveData<String>()
    val responseMsg: LiveData<String>
        get() = _responseMsg

    fun fetchUsers(page: Int) {
        viewModelScope.launch {
            when (val result: Result<ListUsersResponse> = ApiServiceFactory.getListUsers(page)) {
                is Result.Success<ListUsersResponse> -> {

                    _listSer.value = result.data.data

                    result.data.data?.let { mapUsersDataItem(it) }
                }
                is Result.Error -> {
                    // todo: show error message
                }
            }
        }
    }

    fun createUser(user: User) {
        viewModelScope.launch {
            when (val result = ApiServiceFactory.addUser(user)) {
                is Result.Success<UserResponse> -> {

                    val message = "name: " + result.data.name + "\n" +
                            "job: " + result.data.job + "\n" +
                            "id: " + result.data.id + "\n" +
                            "createdAt: " + result.data.createdAt

                    _responseMsg.value = message
                }
                is Result.Error -> {
                    // todo: show error message
                }
            }
        }
    }

    private fun mapUsersDataItem(data: List<Data>) {
        _list.value = data.map {
            ListUsersDataItem(
                it.id!!,
                it.email,
                it.firstName,
                it.lastName,
                it.avatar,
            )
        }
    }
}