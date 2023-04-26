package it.giovanni.arkivio.fragments.detail.puntonet.mvvvm.logininput

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginInputViewModel : ViewModel() {

    val user: MutableLiveData<User> = MutableLiveData<User>()
    val message: MutableLiveData<String> = MutableLiveData<String>()

    fun showMessage() {
        message.value = if (user.value?.password?.isEmpty()!!)
            "Inserisci la password!"
        else
            "Ciao " + user.value?.username
    }
}