package it.giovanni.arkivio.puntonet.mvvvm.logininput

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginInputViewModel : ViewModel() {

    val _user: MutableLiveData<User> = MutableLiveData<User>()
    val user: LiveData<User> = _user

    private val _message: MutableLiveData<String> = MutableLiveData<String>()
    val message: LiveData<String>
        get() = _message

    // setValue()
    fun showMessage1() {
        _message.value = if (_user.value?.password?.isEmpty()!!)
            "Inserisci la password!"
        else
            "Ciao " + _user.value?.username
    }

    // postValue()
    fun showMessage2() {
        if (_user.value?.password?.isEmpty()!!)
            _message.postValue("Inserisci la password!")
        else
            _message.postValue("Ciao " + _user.value?.username)
    }
}