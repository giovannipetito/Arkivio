package it.giovanni.arkivio.puntonet.workmanager.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import it.giovanni.arkivio.puntonet.workmanager.OUTPUT_BLUR_PATH
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * La classe CleanupWorker estende la classe CoroutineWorker della libreria Android WorkManager,
 * ciò significa che esegue il proprio lavoro in un thread in background utilizzando le coroutine,
 * utilizzate per la programmazione asincrona. In questo esempio, lo scopo di CleanupWorker è quello
 * di eseguire attività di pulizia eliminando i file immagine temporanei creati durante il processo
 * di sfocatura dell'immagine. In particolare, CleanupWorker esamina la directory di output alla ricerca
 * di file con estensione ".png" e li elimina. Questo aiuta a ripulire la memoria temporanea e mantenere
 * un ambiente pulito dopo il completamento del lavoro di sfocatura dell'immagine.
 *
 * La classe CleanupWorker accetta due parametri: context e params. context fa riferimento
 * all'applicationContext e params fornisce parametri aggiuntivi per il worker.
 */
class CleanupWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    /**
     * La funzione doWork contiene la logica di pulizia effettiva. È una funzione di sospensione
     * che consente di mettere in pausa e riprendere tale logica senza bloccare il thread.
     */
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            /**
             * La variabile outputDirectory rappresenta la directory in cui sono archiviati i file
             * immagine temporanei. Si ottiene creando un oggetto File utilizzando la directory dei
             * file dell'applicationContext e la costante OUTPUT_PATH.
             */
            val outputDirectory = File(applicationContext.filesDir, OUTPUT_BLUR_PATH)

            /**
             * Eliminazione file: il worker verifica se outputDirectory esiste e procede a iterare
             * sul suo contenuto. Per ogni entry nella directory, controlla se il nome del file non
             * è vuoto e termina con ".png". In tal caso, tenta di eliminare il file chiamando
             * entry.delete(). Il risultato dell'operazione di eliminazione viene registrato
             * utilizzando Log.i per indicare se il file è stato eliminato correttamente o meno.
             */
            if (outputDirectory.exists()) {
                val entries = outputDirectory.listFiles()
                if (entries != null) {
                    for (entry in entries) {
                        val name = entry.name
                        if (name.isNotEmpty() && name.endsWith(".png")) {
                            val deleted = entry.delete()
                            Log.i("[WORKER]", "Deleted $name - $deleted")
                        }
                    }
                }
            }
            /**
             * Se il processo di pulizia viene completato senza alcuna eccezione, il worker
             * restituisce ApiResult.success() per indicare il completamento corretto del lavoro.
             */
            return@withContext Result.success()
        } catch (exception: Exception) {
            /**
             * Gestione degli errori: se si verifica un'eccezione durante il processo di pulizia,
             * viene rilevata, viene stampata l'analisi dello stack e il worker restituisce
             * ApiResult.failure() per indicare un errore nel lavoro.
             */
            exception.printStackTrace()
            return@withContext Result.failure()
        }
    }
}