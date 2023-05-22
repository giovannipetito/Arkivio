package it.giovanni.arkivio.fragments.detail.puntonet.dagger

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import it.giovanni.arkivio.fragments.detail.puntonet.retrofitpaging.Data
import it.giovanni.arkivio.fragments.detail.puntonet.retrofitpaging.UsersResponse
import javax.inject.Inject

@HiltViewModel
class DIViewModel @Inject constructor(private val apiService: ApiService) : ViewModel() {

    var disposable: Disposable? = null

    private val _users: MutableLiveData<List<Data>> = MutableLiveData<List<Data>>()
    val users: LiveData<List<Data>>
        get() = _users

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
}