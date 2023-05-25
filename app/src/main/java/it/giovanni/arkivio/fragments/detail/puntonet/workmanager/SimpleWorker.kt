package it.giovanni.arkivio.fragments.detail.puntonet.workmanager

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * La classe SimpleWorker estende la classe Worker fornita da WorkManager, sarà in grado quindi di
 * eseguire un task in background. Nel nostro esempio, la classe SimpleWorker logga un messaggio.
 *
 * La classe SimpleWorker rappresenta il lavoro in background che verrà eseguito da WorkManager.
 * Nella funzione doWork viene definito il lavoro effettivo che deve essere effettuato.
 * Per gestire il risultato o lo stato del lavoro, è possibile utilizzare il meccanismo di callback
 * di WorkManager. Un modo per farlo è definire un tag univoco per la tua richiesta di lavoro e
 * utilizzare quel tag per osservare lo stato del lavoro utilizzando getWorkInfoByTagLiveData().
 */
class SimpleWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            Log.d("WorkManager", "Doing some work...")
            // Simulate work by sleeping for 3 seconds
            Thread.sleep(3000)
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}