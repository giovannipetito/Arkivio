package it.giovanni.arkivio.fragments.detail.puntonet.workmanager

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import it.giovanni.arkivio.fragments.detail.puntonet.workmanager.workers.SimpleWorker
import java.util.concurrent.TimeUnit

class SimpleWorkerViewModel(application: Application) : ViewModel() {

    private val workManager = WorkManager.getInstance(application)

    val workInfos: LiveData<List<WorkInfo>> = workManager.getWorkInfosByTagLiveData(TAG_SIMPLE_MESSAGE_OUTPUT)

    init {
        runWork()
    }

    private fun runWork() {
        /**
         * In questo esempio, SimpleWorker verrà pianificato per essere eseguito una volta
         * con un ritardo iniziale di 5 secondi. Una volta trascorso il ritardo, il metodo
         * doWork() del SimpleWorker verrà eseguito in background.
         */
        // Crea una WorkRequest
        val workRequest: OneTimeWorkRequest = OneTimeWorkRequestBuilder<SimpleWorker>()
            .setInitialDelay(5, TimeUnit.SECONDS)
            .addTag(TAG_SIMPLE_MESSAGE_OUTPUT)
            .build()

        // Enqueue (accoda) la WorkRequest
        workManager.enqueue(workRequest)
    }
}