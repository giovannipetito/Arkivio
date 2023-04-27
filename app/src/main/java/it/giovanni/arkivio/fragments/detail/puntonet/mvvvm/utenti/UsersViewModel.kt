package it.giovanni.arkivio.fragments.detail.puntonet.mvvvm.utenti

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import it.giovanni.arkivio.fragments.detail.puntonet.mvvvm.utenti.api.IUsers
import it.giovanni.arkivio.fragments.detail.puntonet.mvvvm.utenti.api.UsersClient
import it.giovanni.arkivio.restclient.retrofit.User

/**
 * La classe ViewModel è la classe dove definirai la logica che la tua app utilizzerà per interagire
 * con il tuo Model. Ad esempio, se desideri recuperare la lista di utenti da un'API, puoi definire
 * nel tuo ViewModel la funzione getUsersData() che utilizza Retrofit.
 *
 * Prossima implementazione:
 * Il metodo getUsers() è una funzione che effettua la request e restituisce un oggetto Result<Utente>.
 * La classe Result è una classe personalizzata che può contenere un risultato di successo
 * (Result.Success) o un risultato di errore (Result.Error).
 * Nell'implementazione, per prima cosa effettuiamo la request utilizzando il metodo getUsersData()
 * e controlliamo se la response ha avuto successo. In caso affermativo, restituiamo un oggetto
 * Result.Success con i dati degli utenti. Se invece, durante la request viene rilevata un'eccezione,
 * restituiamo un oggetto Result.Error con l'eccezione rilevata (con il messaggio di eccezione).
 */
class UsersViewModel : ViewModel(), IUsers {

    private val _utente: MutableLiveData<Utente> = MutableLiveData<Utente>()
    val utente: LiveData<Utente> = _utente

    fun getUsersData() {
        UsersClient.getUsers(this)
    }

    override fun onSuccess(message: String?, users: List<User?>?) {
        _utente.postValue(Utente(message, users))
    }

    override fun onFailure(message: String?) {
        _utente.postValue(Utente(message, null))
    }
}