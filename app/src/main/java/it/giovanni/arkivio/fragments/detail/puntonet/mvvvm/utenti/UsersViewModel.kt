package it.giovanni.arkivio.fragments.detail.puntonet.mvvvm.utenti

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import it.giovanni.arkivio.restclient.retrofit.IRetrofit
import it.giovanni.arkivio.restclient.retrofit.SimpleRetrofitClient
import it.giovanni.arkivio.restclient.retrofit.User
import it.giovanni.arkivio.restclient.retrofit.UsersResponse

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
class UsersViewModel : ViewModel(), IRetrofit {

    private val _response: MutableLiveData<UsersResponse> = MutableLiveData<UsersResponse>()
    val response: LiveData<UsersResponse> = _response

    fun getUsersData() {
        SimpleRetrofitClient.getUsers(this)
    }

    override fun onRetrofitSuccess(users: List<User?>?, message: String?) {
        _response.postValue(UsersResponse(users, message))
    }

    override fun onRetrofitFailure(message: String?) {
        _response.postValue(UsersResponse(null, message))
    }
}