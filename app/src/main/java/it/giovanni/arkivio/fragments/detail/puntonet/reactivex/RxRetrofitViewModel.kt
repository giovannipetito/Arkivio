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
import it.giovanni.arkivio.fragments.detail.puntonet.retrofitgetpost.User
import it.giovanni.arkivio.fragments.detail.puntonet.cleanarchitecture.data.ApiResult
import it.giovanni.arkivio.fragments.detail.puntonet.retrofitgetpost.UsersResponse

class RxRetrofitViewModel : ViewModel() {

    var disposable: Disposable? = null

    private val _users: MutableLiveData<List<User?>> = MutableLiveData<List<User?>>()
    val users: LiveData<List<User?>>
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
                _users.postValue(response.users)
            }, { error ->
                Log.e("[RX]", "error: " + error.message)
            })
    }

    /**
     * In questo esempio, il metodo getUsersV2 restituisce un ApiResult<Single<UsersResponse>>.
     */
    fun fetchUsersV2(page: Int) {
        when (val apiResult: ApiResult<Single<UsersResponse>> = ApiServiceClient.getUsersV2(page)) {
            is ApiResult.Success<Single<UsersResponse>> -> {

                val observable: Single<UsersResponse> = apiResult.data

                observable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(getUsersSingleObserver())
            }
            is ApiResult.Error -> {
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
                _users.postValue(response.users)
            }
        }
    }
}