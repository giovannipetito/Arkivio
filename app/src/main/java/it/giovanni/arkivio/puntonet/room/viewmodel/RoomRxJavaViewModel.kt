package it.giovanni.arkivio.puntonet.room.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import it.giovanni.arkivio.puntonet.room.database.CoreDatabase
import it.giovanni.arkivio.puntonet.room.entity.User
import it.giovanni.arkivio.puntonet.room.repository.RoomRxJavaRepository

class RoomRxJavaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RoomRxJavaRepository

    var disposable: Disposable? = null

    private val _users: MutableLiveData<List<User>> = MutableLiveData<List<User>>()
    val users: LiveData<List<User>>
        get() = _users

    init {
        val userDao = CoreDatabase.getDatabase(application).userRxJavaDao()
        repository = RoomRxJavaRepository(userDao)
    }

    fun getUsers() {
        disposable = repository.getUsers()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { users ->
                _users.value = users
            }
    }

    fun addUser(user: User) {
        repository.insertUser(user)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }

    fun updateUser(user: User) {
        repository.updateUser(user)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }

    fun deleteUser(user: User) {
        repository.deleteUser(user)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }

    fun deleteUsers() {
        repository.deleteUsers()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }
}