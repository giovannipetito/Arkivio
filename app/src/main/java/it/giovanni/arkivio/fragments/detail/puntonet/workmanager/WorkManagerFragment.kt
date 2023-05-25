package it.giovanni.arkivio.fragments.detail.puntonet.workmanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.WorkManagerLayoutBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Pianifica il lavoro: nel tuo fragment, pianifica il lavoro da eseguire utilizzando l'API di
 * WorkManager. Ad esempio, pianifichiamo l'esecuzione di SimpleWorker una volta dopo un delay.
 *
 * In questo esempio, SimpleWorker verrà pianificato per essere eseguito una volta con un ritardo
 * iniziale di 5 secondi. Una volta trascorso il ritardo, il metodo doWork() del SimpleWorker
 * verrà eseguito in background.
 */
class WorkManagerFragment : DetailFragment() {

    private var layoutBinding: WorkManagerLayoutBinding? = null
    private val binding get() = layoutBinding

    override fun getTitle(): Int {
        return R.string.work_manager_title
    }

    override fun getActionTitle(): Int {
        return NO_TITLE
    }

    override fun searchAction(): Boolean {
        return false
    }

    override fun backAction(): Boolean {
        return true
    }

    override fun closeAction(): Boolean {
        return false
    }

    override fun deleteAction(): Boolean {
        return false
    }

    override fun editAction(): Boolean {
        return false
    }

    override fun editIconClick() {
    }

    override fun onActionSearch(search_string: String) {
    }

    override fun onCreateBindingView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        layoutBinding = WorkManagerLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this)
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Create a WorkRequest
        val workRequest = OneTimeWorkRequest.Builder(SimpleWorker::class.java)
            .setInitialDelay(5, TimeUnit.SECONDS)
            .build()

        // Enqueue the WorkRequest
        WorkManager.getInstance(requireContext()).enqueue(workRequest)

        /**
         * Gestisci il risultato del lavoro: se è necessario gestire il risultato del lavoro, è
         * possibile aggiungere una callback di WorkManager utilizzando l'API di WorkManager. Questa
         * callback verrà richiamata al termine del lavoro e potrai gestire il risultato di conseguenza.
         */
        // Enqueue the WorkRequest and add a callback
        /*
        WorkManager.getInstance(requireContext())
            .enqueue(workRequest)
            .result.addListener({
                // Handle the work result
                val isSuccess = it.isSuccessful
                // ...
            }, Executors.newSingleThreadExecutor())
        */

        // Enqueue the work request and get a unique tag
        /*
        val workRequest = OneTimeWorkRequestBuilder<SimpleWorker>()
            .setInputData(workData)
            .build()

        val workTag = "my_unique_work_tag"
        WorkManager.getInstance(context).enqueueUniqueWork(workTag, ExistingWorkPolicy.REPLACE, workRequest)

        // Observe the work status
        val workManager = WorkManager.getInstance(context)
        val workInfoLiveData = workManager.getWorkInfoByTagLiveData(workTag)
        workInfoLiveData.observe(owner) { workInfo ->
            if (workInfo != null) {
                val state = workInfo.state
                // Handle the work state, e.g., WorkInfo.State.RUNNING, WorkInfo.State.SUCCEEDED, etc.
                if (state == WorkInfo.State.SUCCEEDED) {
                    // Work completed successfully
                    val result = workInfo.outputData.getString("result")
                    // Do something with the result
                }
            }
        }
        */
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}