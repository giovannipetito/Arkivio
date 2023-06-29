package it.giovanni.arkivio.fragments.detail.puntonet.mvvvm.users

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import it.giovanni.arkivio.fragments.detail.puntonet.retrofitgetpost.UsersResponse
import it.giovanni.arkivio.restclient.retrofit.IRetrofit
import it.giovanni.arkivio.restclient.retrofit.SimpleRetrofitClient

/**
 * Nella classe ViewModel è definita la logica che l'applicazione utilizzerà per interagire con il
 * Model. Ad esempio, puoi definire qui la funzione getUsers() che utilizza Retrofit per recuperare
 * la lista di utenti da un'API
 */
class MvvmUsersViewModel : ViewModel(), IRetrofit {

    private val _response: MutableLiveData<UsersResponse?> = MutableLiveData<UsersResponse?>()
    val response: LiveData<UsersResponse?> = _response

    private val _message: MutableLiveData<String> = MutableLiveData<String>()
    val message: LiveData<String> = _message

    fun getUsers() {
        SimpleRetrofitClient.getUsers(this)
    }

    override fun onRetrofitSuccess(usersResponse: UsersResponse?, message: String) {
        _response.postValue(usersResponse)
    }

    override fun onRetrofitFailure(message: String) {
        _message.postValue(message)
    }
}