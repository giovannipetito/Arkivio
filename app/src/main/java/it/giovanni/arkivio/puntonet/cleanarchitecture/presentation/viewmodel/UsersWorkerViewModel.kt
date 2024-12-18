package it.giovanni.arkivio.puntonet.cleanarchitecture.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import it.giovanni.arkivio.puntonet.cleanarchitecture.KEY_USERS
import it.giovanni.arkivio.puntonet.cleanarchitecture.TAG_USERS_OUTPUT
import it.giovanni.arkivio.puntonet.cleanarchitecture.domain.worker.UsersWorker
import it.giovanni.arkivio.puntonet.retrofitgetpost.User
import it.giovanni.arkivio.puntonet.room.database.CoreDatabase
import it.giovanni.arkivio.puntonet.room.repository.UsersWorkerRepository
import kotlinx.coroutines.launch

class UsersWorkerViewModel(application: Application) : ViewModel() {

    lateinit var workInfos: LiveData<List<WorkInfo>>

    private val repository: UsersWorkerRepository

    private var _users: MutableLiveData<List<User>> = MutableLiveData<List<User>>()
    val users: LiveData<List<User>>
        get() = _users

    init {
        val usersDao = CoreDatabase.getDatabase(application).usersWorkerDao()
        repository = UsersWorkerRepository(usersDao)
    }

    /**
     * Get data with WorkManager
     */
    fun getUsersFromWorkManager(application: Application, page: Int) {

        val workManager = WorkManager.getInstance(application)
        workInfos = workManager.getWorkInfosByTagLiveData(TAG_USERS_OUTPUT)

        val workRequestBuilder: OneTimeWorkRequest.Builder = OneTimeWorkRequestBuilder<UsersWorker>()
            .addTag(TAG_USERS_OUTPUT)
            .setInputData(createInputData(page))

        workManager.enqueue(workRequestBuilder.build())
    }

    private fun createInputData(page: Int): Data {
        val builder = Data.Builder()
        page.let {
            builder.putInt(KEY_USERS, page)
        }
        return builder.build()
    }

    fun getUsersFromDatabase() {
        viewModelScope.launch {
            _users.value = repository.getUsers()
        }
    }
}