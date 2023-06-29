package it.giovanni.arkivio.fragments.detail.puntonet.cleanarchitecture.domain.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import it.giovanni.arkivio.fragments.detail.puntonet.cleanarchitecture.KEY_USERS
import it.giovanni.arkivio.fragments.detail.puntonet.cleanarchitecture.data.ApiResult
import it.giovanni.arkivio.fragments.detail.puntonet.retrofitgetpost.ApiServiceClient
import it.giovanni.arkivio.fragments.detail.puntonet.retrofitgetpost.UsersResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UsersWorker constructor(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {

        val page = inputData.getInt(KEY_USERS, 1)

        try {
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

            val outputData: Data = workDataOf(KEY_USERS to usersResponse.toString())

            return@withContext Result.success(outputData)
        } catch (throwable: Throwable) {
            Log.e("[WORKER]", "UsersWorker Error!")
            throwable.printStackTrace()
            return@withContext Result.failure()
        }
    }
}