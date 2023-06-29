package it.giovanni.arkivio.fragments.detail.puntonet.workmanager.workers

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.workDataOf
import androidx.work.WorkerParameters
import it.giovanni.arkivio.fragments.detail.puntonet.workmanager.KEY_IMAGE_URI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * La classe SaveImageWorker estende la classe CoroutineWorker della libreria Android WorkManager,
 * ciò significa che esegue il proprio lavoro in un thread in background utilizzando le coroutine,
 * utilizzate per la programmazione asincrona. In questo esempio, lo scopo di SaveImageWorker è
 * quello di salvare l'immagine sfocata nel MediaStore del dispositivo, rendendola accessibile ad
 * altre applicazioni e servizi. Decodifica l'immagine dall'URI fornito, la salva nel MediaStore
 * con un titolo specifico e una data corrente e restituisce l'URL dell'immagine salvata come output.
 *
 * La classe SaveImageWorker accetta due parametri: context e params. context fa riferimento
 * all'applicationContext e params fornisce parametri aggiuntivi per il worker.
 */
class SaveImageWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    /**
     * La costante Title è utilizzata per il titolo dell'immagine salvata.
     */
    private val title = "Blurred Image"

    /**
     * dateFormatter un'istanza di SimpleDateFormat utilizzata per formattare la data e l'ora
     * correnti per la denominazione dell'immagine.
     */
    private val dateFormatter = SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss z", Locale.getDefault())

    /**
     * La funzione doWork contiene la logica per il salvataggio dell'immagine.
     */
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {

        // todo: send a notifification

        /**
         * La variabile resolver rappresenta il content resolver ottenuto dall'applicationContext.
         * Sarà utilizzato per interagire con il MediaStore.
         */
        val resolver = applicationContext.contentResolver
        try {
            /**
             * La variabile resourceUri è ottenuta dai dati di input del lavoratore. Contiene l'URI
             * dell'immagine sfocata ottenuta da un precedente worker della catena.
             */
            val resourceUri = inputData.getString(KEY_IMAGE_URI)

            /**
             * Decodifica e salvataggio dell'immagine: il worker tenta di decodificare l'immagine
             * aprendo un flusso di input utilizzando il resolver e resourceUri. Utilizza
             * BitmapFactory.decodeStream per decodificare l'immagine in un oggetto Bitmap.
             */
            val bitmap = BitmapFactory.decodeStream(resolver.openInputStream(Uri.parse(resourceUri)))

            /**
             * Salvataggio dell'immagine in MediaStore: se il processo di decodifica ha esito
             * positivo, il worker tenta di salvare l'immagine in MediaStore chiamando
             * MediaStore.Images.Media.insertImage. Passa come argomenti il content resolver,
             * la bitmap, il titolo e la data corrente formattata. Il metodo restituisce l'URL
             * dell'immagine salvata.
             */
            val imageUrl = MediaStore.Images.Media.insertImage(resolver, bitmap, title, dateFormatter.format(Date()))

            if (!imageUrl.isNullOrEmpty()) {
                /**
                 * Restituzione del risultato: se l'immagine viene salvata correttamente, il worker
                 * crea un oggetto dati di output utilizzando workDataOf con la chiave KEY_IMAGE_URI
                 * e l'URL come valore. Restituisce ApiResult.success(output) per indicare il
                 * completamento con successo del lavoro.
                 */
                val output = workDataOf(KEY_IMAGE_URI to imageUrl)
                return@withContext Result.success(output)
            } else {
                /**
                 * Gestione degli errori: se si verificano eccezioni durante il processo di
                 * decodifica o salvataggio dell'immagine, queste vengono rilevate, viene stampata
                 * lo stack trace e il worker restituisce ApiResult.failure() per indicare un errore
                 * nel lavoro.
                 */
                Log.e("[WORKER]", "Writing to MediaStore failed")
                return@withContext Result.failure()
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
            return@withContext Result.failure()
        }
    }
}