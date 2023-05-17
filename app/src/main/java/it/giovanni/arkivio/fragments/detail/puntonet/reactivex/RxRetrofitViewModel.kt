package it.giovanni.arkivio.fragments.detail.puntonet.reactivex

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import it.giovanni.arkivio.fragments.detail.puntonet.retrofitpaging.Data
import it.giovanni.arkivio.fragments.detail.puntonet.retrofitpaging.Result
import it.giovanni.arkivio.fragments.detail.puntonet.retrofitpaging.UsersResponse

class RxRetrofitViewModel : ViewModel() {

    var disposable: Disposable? = null

    private val _users: MutableLiveData<List<Data>> = MutableLiveData<List<Data>>()
    val users: LiveData<List<Data>>
        get() = _users

    /**
     * Utilizzando RxJava2CallAdapterFactory in ApiServiceClient e il tipo restituito
     * Single<UsersResponse> per getUsers, è quindi possibile utilizzare gli operatori
     * e i metodi di RxJava per manipolare ed elaborare i dati emessi.
     *
     * In questo esempio, il metodo getUsersV1 restituisce un Single<UsersResponse>.
     */
    fun fetchUsersV1(page: Int) {

        val observable: Single<UsersResponse> = ApiServiceClient.getUsersV1(page)

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

    /**
     * In questo esempio, il metodo getUsersV2 restituisce un Result<Single<UsersResponse>>.
     */
    fun fetchUsersV2(page: Int) {
        when (val result: Result<Single<UsersResponse>> = ApiServiceClient.getUsersV2(page)) {
            is Result.Success<Single<UsersResponse>> -> {

                val observable: Single<UsersResponse> = result.data

                observable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(getUsersSingleObserver())
            }
            is Result.Error -> {
                // todo: show error message
            }
        }
    }

    private fun getUsersSingleObserver(): SingleObserver<UsersResponse> {
        return object :
            SingleObserver<UsersResponse> {
            override fun onSubscribe(d: Disposable) {
                disposable = d
            }

            override fun onError(e: Throwable) {
                Log.e("[RX]", "onError: " + e.message)
            }

            override fun onSuccess(response: UsersResponse) {
                _users.postValue(response.data)
            }
        }
    }
}