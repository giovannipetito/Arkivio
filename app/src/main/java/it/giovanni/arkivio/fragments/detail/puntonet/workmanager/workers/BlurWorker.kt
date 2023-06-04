package it.giovanni.arkivio.fragments.detail.puntonet.workmanager.workers

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import it.giovanni.arkivio.fragments.detail.puntonet.workmanager.KEY_IMAGE_URI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * La classe BlurWorker estende la classe CoroutineWorker della libreria Android WorkManager, ciò
 * significa che esegue il proprio lavoro in un thread in background utilizzando le coroutine,
 * utilizzate per la programmazione asincrona. In questo esempio, BlurWorker rappresenta un worker
 * in background che esegue il processo di sfocatura dell'immagine, prende l'URI di un'immagine
 * come input, applica l'effetto di sfocatura e restituisce l'URI dell'immagine sfocata come output.
 *
 * La classe BlurWorker accetta due parametri: context e params. context fa riferimento
 * all'applicationContext e params fornisce parametri aggiuntivi per il worker.
 */
class BlurWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    /**
     * doWork è il metodo principale del worker che esegue il lavoro vero e proprio. È una funzione
     * di sospensione, cioè può essere messa in pausa e ripresa senza bloccare il thread.
     */
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {

        /**
         * La proprietà appContext contiene l'applicationContext, necessario per accedere alle
         * risorse e al content resolver.
         */
        val appContext = applicationContext

        /**
         * resourceUri: la proprietà inputData è utilizzata per recuperare i dati di input passati
         * al worker. In questo caso, recupera il valore associato alla chiave KEY_IMAGE_URI, che
         * rappresenta l'URI della risorsa immagine da sfumare.
         */
        val resourceUri = inputData.getString(KEY_IMAGE_URI)

        /**
         * La funzione sleep viene utilizzato per simulare un'operazione più lunga sospendendo
         * la coroutine per un periodo di tempo specificato. Aggiunge un ritardo fittizio alla
         * esecuzione del worker.
         */
        sleep()

        /**
         * Blurring process: il worker esegue il processo di sfocatura dell'immagine all'interno del
         * blocco try-catch. Decodifica l'immagine dall'URI fornito utilizzando il content resolver,
         * applica l'effetto di sfocatura utilizzando la funzione blurBitmap e scrive la bitmap
         * risultante in un file temporaneo utilizzando writeBitmapToFile.
         */
        try {
            if (TextUtils.isEmpty(resourceUri)) {
                Log.e("[WORKER]", "Invalid input uri")
                throw IllegalArgumentException("Invalid input uri")
            }

            val resolver = appContext.contentResolver

            val picture = BitmapFactory.decodeStream(resolver.openInputStream(Uri.parse(resourceUri)))

            val output = blurBitmap(picture, appContext)

            /**
             * outputUri rappresenta l'URI del file dell'immagine sfocata.
             */
            val outputUri = writeBitmapToFile(appContext, output)

            /**
             * outputData: la funzione workDataOf crea un oggetto Data per contenere i dati di output.
             * In questo caso associa la chiave KEY_IMAGE_URI all'URI dell'immagine sfocata.
             */
            val outputData = workDataOf(KEY_IMAGE_URI to outputUri.toString())

            /**
             * Result.success: se il processo di sfocatura viene completato correttamente, il worker
             * restituisce un Result.success con i dati di output contenenti l'URI dell'immagine sfocata.
             */
            return@withContext Result.success(outputData)
        } catch (throwable: Throwable) {
            /**
             * Gestione degli errori: se si verifica un errore durante il processo di sfocatura, il
             * worker rileva l'eccezione, registra un messaggio di errore, stampa l'analisi dello
             * stack e restituisce Result.failure per indicare l'errore del lavoro.
             */
            Log.e("[WORKER]", "Error applying blur")
            throwable.printStackTrace()
            return@withContext Result.failure()
        }
    }
}