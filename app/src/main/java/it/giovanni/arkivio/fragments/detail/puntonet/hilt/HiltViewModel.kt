package it.giovanni.arkivio.fragments.detail.puntonet.hilt

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.giovanni.arkivio.fragments.detail.puntonet.retrofitpaging.Data
import it.giovanni.arkivio.fragments.detail.puntonet.retrofitpaging.UsersResponse
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Questa classe ViewModel è responsabile del recupero e della gestione di una lista di utenti.
 * Utilizza Hilt per iniettare la dipendenza ApiService e utilizza le coroutine Kotlin per eseguire
 * la richiesta di rete asincrona e aggiornare i LiveData di conseguenza. Altri componenti, come
 * fragment o activity, possono osservare LiveData users per ricevere aggiornamenti ogni volta
 * che la lista users cambia.
 */

/**
 * L'annotazione @HiltViewModel indica che questa classe deve essere fornita e gestita da Hilt come
 * ViewModel.
 *
 * La dichiarazione della classe HiltViewModel prende un'istanza dell'interfaccia ApiService come
 * parametro del costruttore, che verrà iniettato da Hilt.
 */
@HiltViewModel
class HiltViewModel @Inject constructor(private val apiService: ApiService) : ViewModel() {

    // var disposable: Disposable? = null

    /**
     * _users è un'istanza MutableLiveData che contiene una lista di oggetti Data. Rappresenta
     * la versione mutabile di LiveData che può essere aggiornata dall'interno di ViewModel.
     */
    private val _users: MutableLiveData<List<Data>> = MutableLiveData<List<Data>>()

    /**
     * users è un'istanza LiveData derivata da _users. Fornisce accesso in sola lettura alla lista
     * di oggetti Data ai componenti di osservazione.
     */
    val users: LiveData<List<Data>>
        get() = _users

    /**
     * fetchUsers() è responsabile del recupero degli utenti. Utilizza le coroutine Kotlin e
     * viewModelScope per lanciare una nuova coroutine. All'interno della coroutine, chiama il
     * metodo getUsers() dall'apiService per recuperare la response degli utenti per la pagina
     * specificata. Quindi, aggiorna _users inviando il nuovo valore di response.data utilizzando il
     * metodo postValue(). Poiché questo aggiornamento viene eseguito all'interno di viewModelScope,
     * verrà automaticamente annullato quando il ViewModel viene cancellato.
     */
    fun fetchUsers(page: Int) {
        viewModelScope.launch {
            val response: UsersResponse = apiService.getUsers(page)
            _users.postValue(response.data)
        }
    }

    /*
    fun fetchUsers(page: Int) {

        val observable: Single<UsersResponse> = apiService.getUsers(page)

        disposable = observable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { response ->
                _users.postValue(response.data)
            }, { error ->
                Log.e("[RX]", "error: " + error.message)
            })
    }
    */
}