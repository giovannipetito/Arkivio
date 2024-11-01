package it.giovanni.arkivio.puntonet.cleanarchitecture.domain.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import it.giovanni.arkivio.puntonet.cleanarchitecture.KEY_USERS
import it.giovanni.arkivio.puntonet.cleanarchitecture.data.ApiResult
import it.giovanni.arkivio.puntonet.retrofitgetpost.ApiServiceClient
import it.giovanni.arkivio.puntonet.retrofitgetpost.User
import it.giovanni.arkivio.puntonet.retrofitgetpost.UsersResponse
import it.giovanni.arkivio.puntonet.room.database.CoreDatabase
import it.giovanni.arkivio.puntonet.room.repository.UsersWorkerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UsersWorker constructor(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    private val application = context.applicationContext

    private val repository: UsersWorkerRepository

    init {
        val usersDao = CoreDatabase.getDatabase(application).usersWorkerDao()
        repository = UsersWorkerRepository(usersDao)
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {

        val page = inputData.getInt(KEY_USERS, 1)

        try {
            // Simulate work by sleeping for 20 seconds
            Thread.sleep(20000)

            val usersResponse: UsersResponse? =
                when (val apiResult: ApiResult<UsersResponse> = ApiServiceClient.getUsers(page)) {
                    is ApiResult.Success<UsersResponse> -> {
                        apiResult.data
                    }
                    is ApiResult.Error -> {
                        // todo: show error message
                        null
                    }
                }

            val users: List<User> = usersResponse?.users!!
            repository.insertUsers(users)

            val outputData: Data = workDataOf(KEY_USERS to "users retrieved successfully!")

            return@withContext Result.success(outputData)
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
            return@withContext Result.failure()
        }
    }
}