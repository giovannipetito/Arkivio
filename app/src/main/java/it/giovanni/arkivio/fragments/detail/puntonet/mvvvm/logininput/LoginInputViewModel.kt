package it.giovanni.arkivio.fragments.detail.puntonet.mvvvm.logininput

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginInputViewModel : ViewModel() {

    private val mUser: MutableLiveData<User> = MutableLiveData<User>()

    fun setUser(user: User) {
        mUser.value = user
    }

    // 1)
    /*
    fun getUser(): LiveData<User> {
        return mUser
    }
    */

    // 2)
    val user: LiveData<User>
        get() = mUser

    // Nota. 1) e 2) sono equivalenti.
}