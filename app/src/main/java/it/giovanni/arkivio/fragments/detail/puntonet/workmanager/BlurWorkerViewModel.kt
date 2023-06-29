package it.giovanni.arkivio.fragments.detail.puntonet.workmanager

import android.app.Application
import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import it.giovanni.arkivio.R
import it.giovanni.arkivio.fragments.detail.puntonet.workmanager.workers.BlurWorker
import it.giovanni.arkivio.fragments.detail.puntonet.workmanager.workers.CleanupWorker
import it.giovanni.arkivio.fragments.detail.puntonet.workmanager.workers.SaveImageWorker

/**
 * Questo ViewModel esegue operazioni di sfocatura delle immagini utilizzando WorkManager.
 */
class BlurWorkerViewModel(application: Application) : ViewModel() {

    /**
     * workManager è un'istanza di WorkManager ottenuta utilizzando WorkManager.getInstance(application).
     * Viene utilizzato per interagire con WorkManager per la gestione e l'esecuzione del lavoro in background.
     */
    private val workManager = WorkManager.getInstance(application)

    /**
     * imageUri rappresenta l'URI dell'immagine di input da sfocare. Viene impostato utilizzando la
     * funzione getImageUri che costruisce un URI basato su una resource ID (R.drawable.ico_cupcake).
     */
    private var imageUri: Uri? = null

    /**
     * outputUri rappresenta l'URI dell'immagine di output dopo l'applicazione della sfocatura.
     */
    var outputUri: Uri? = null

    /**
     * workInfos è un oggetto LiveData che contiene una lista di oggetti WorkInfo. Si ottiene chiamando
     * workManager.getWorkInfosByTagLiveData(TAG_OUTPUT) e permette di osservare lo stato del lavoro.
     */
    val workInfos: LiveData<List<WorkInfo>> = workManager.getWorkInfosByTagLiveData(TAG_BLUR_OUTPUT)

    init {
        /**
         * Questa trasformazione garantisce che ogni volta che il work ID corrente cambia WorkInfo,
         * la UI ascolti le modifiche.
         */
        val resources = application.applicationContext.resources

        imageUri = Uri.Builder()
            .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .authority(resources.getResourcePackageName(R.drawable.ico_cupcake))
            .appendPath(resources.getResourceTypeName(R.drawable.ico_cupcake))
            .appendPath(resources.getResourceEntryName(R.drawable.ico_cupcake))
            .build()
    }

    fun setOutputUri(outputImageUri: String?) {
        outputUri = if (!outputImageUri.isNullOrEmpty()) Uri.parse(outputImageUri)
        else null
    }

    /**
     * applyBlur crea la WorkRequest per applicare la sfocatura e salva l'immagine risultante.
     * @param blurLevel La quantità di sfocatura dell'immagine.
     */
    fun applyBlur(blurLevel: Int) {
        // Add WorkRequest to Cleanup temporary images
        var continuation = workManager.beginUniqueWork(
            IMAGE_MANIPULATION_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            OneTimeWorkRequest.from(CleanupWorker::class.java)
        )

        // Questo ciclo for aggiunge le WorkRequests per sfocare l'immagine il numero di volte richiesto.
        for (i in 0 until blurLevel) {
            val blurBuilder: OneTimeWorkRequest.Builder = OneTimeWorkRequestBuilder<BlurWorker>()

            // Immette l'URI se questa è la prima operazione di sfocatura. Dopo la prima operazione
            // di sfocatura, l'input sarà l'output delle precedenti operazioni di sfocatura.
            if (i == 0) {
                blurBuilder.setInputData(createInputDataForUri())
            }

            continuation = continuation.then(blurBuilder.build())
        }

        // Create charging constraint
        val constraints = Constraints.Builder()
            .setRequiresCharging(true)
            .build()

        // Aggiunge una WorkRequest per salvare l'immagine nel filesystem.
        val save = OneTimeWorkRequestBuilder<SaveImageWorker>()
            .setConstraints(constraints)
            .addTag(TAG_BLUR_OUTPUT)
            .build()

        continuation = continuation.then(save)

        // È qui che in pratica inizia il lavoro.
        continuation.enqueue()
    }

    /**
     * La funzione createInputDataForUri crea un oggetto User che contiene l'input. Crea il bundle
     * di dati di input che include l'URI per operare su @return User che contengono l'URI della
     * immagine come stringa.
     */
    private fun createInputDataForUri(): Data {
        val builder = Data.Builder()
        imageUri?.let {
            builder.putString(KEY_IMAGE_URI, imageUri.toString())
        }
        return builder.build()
    }

    /**
     * La funzione cancelWork annulla il lavoro univoco in corso chiamando
     * workManager.cancelUniqueWork(IMAGE_MANIPULATION_WORK_NAME).
     */
    fun cancelWork() {
        workManager.cancelUniqueWork(IMAGE_MANIPULATION_WORK_NAME)
    }
}