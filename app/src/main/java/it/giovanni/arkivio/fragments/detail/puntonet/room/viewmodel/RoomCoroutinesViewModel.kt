package it.giovanni.arkivio.fragments.detail.puntonet.room.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import it.giovanni.arkivio.fragments.detail.puntonet.room.database.ArkivioDatabase
import it.giovanni.arkivio.fragments.detail.puntonet.room.entity.User
import it.giovanni.arkivio.fragments.detail.puntonet.room.repository.RoomCoroutinesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RoomCoroutinesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RoomCoroutinesRepository

    private var _users: MutableLiveData<List<User>> = MutableLiveData<List<User>>()
    val users: LiveData<List<User>>
        get() = _users

    init {
        val userDao = ArkivioDatabase.getDatabase(application).userCoroutinesDao()
        repository = RoomCoroutinesRepository(userDao)
    }

    fun getUsers() {
        viewModelScope.launch {
            _users.value = repository.getUsers()
        }
    }

    fun addUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertUser(user)
        }
    }

    fun updateUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateUser(user)
        }
    }

    fun deleteUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteUser(user)
        }
    }

    fun deleteUsers() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteUsers()
        }
    }
}