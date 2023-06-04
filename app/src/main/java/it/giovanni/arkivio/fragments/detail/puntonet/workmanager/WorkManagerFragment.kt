package it.giovanni.arkivio.fragments.detail.puntonet.workmanager

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.airbnb.paris.extensions.style
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.WorkManagerLayoutBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.fragments.detail.puntonet.workmanager.workers.SimpleWorker
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter
import it.giovanni.arkivio.utils.SharedPreferencesManager
import java.util.concurrent.TimeUnit

class WorkManagerFragment : DetailFragment() {

    private var layoutBinding: WorkManagerLayoutBinding? = null
    private val binding get() = layoutBinding

    private val viewModel: BlurViewModel by viewModels {
        BlurViewModelProviderFactory(currentActivity.application)
    }

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

        setViewStyle()

        /**
         * ------------------ SIMPLE EXAMPLE ------------------
         * In questo esempio, SimpleWorker verrà pianificato per essere eseguito una volta con un
         * ritardo iniziale di 5 secondi. Una volta trascorso il ritardo, il metodo doWork() del
         * SimpleWorker verrà eseguito in background.
         */
        // Create a WorkRequest
        val workRequest = OneTimeWorkRequest.Builder(SimpleWorker::class.java)
            .setInitialDelay(5, TimeUnit.SECONDS)
            .build()

        // Enqueue the WorkRequest
        WorkManager.getInstance(requireContext()).enqueue(workRequest)
        /**
         * ---------------- END SIMPLE EXAMPLE ----------------
         */

        binding?.buttonRunWork?.setOnClickListener {
            viewModel.applyBlur(3)
        }

        // Setup view output image file button
        binding?.buttonSeeFile?.setOnClickListener {
            viewModel.outputUri?.let { currentUri ->
                val actionView = Intent(Intent.ACTION_VIEW, currentUri)
                actionView.resolveActivity(currentActivity.packageManager)?.run {
                    startActivity(actionView)
                }
            }
        }

        // Hookup the Cancel button
        binding?.buttonCancel?.setOnClickListener {
            viewModel.cancelWork()
        }

        viewModel.workInfos.observe(viewLifecycleOwner, workInfosObserver())
    }

    private fun workInfosObserver(): Observer<List<WorkInfo>> {
        return Observer { listOfWorkInfo ->

            // Note that these next few lines grab a single WorkInfo if it exists
            // This code could be in a Transformation in the ViewModel; they are included here
            // so that the entire process of displaying a WorkInfo is in one location.

            // If there are no matching work info, do nothing
            if (listOfWorkInfo.isEmpty()) {
                return@Observer
            }

            // We only care about the one output status.
            // Every continuation has only one worker tagged TAG_OUTPUT
            val workInfo = listOfWorkInfo[0]

            if (workInfo.state.isFinished) {
                showWorkFinished()

                // Normally this processing, which is not directly related to drawing views on
                // screen would be in the ViewModel. For simplicity we are keeping it here.
                val outputImageUri = workInfo.outputData.getString(KEY_IMAGE_URI)

                // If there is an output file show "See File" button
                if (!outputImageUri.isNullOrEmpty()) {
                    viewModel.setOutputUri(outputImageUri)
                    binding?.buttonSeeFile?.isEnabled = true
                }
            } else {
                showWorkInProgress()
            }
        }
    }

    /**
     * Shows and hides views for when the Activity is processing an image
     */
    private fun showWorkInProgress() {
        binding?.progressBar?.visibility = View.VISIBLE
        binding?.buttonCancel?.isEnabled = true
        binding?.buttonRunWork?.isEnabled = false
        binding?.buttonSeeFile?.isEnabled = false
    }

    /**
     * Shows and hides views for when the Activity is done processing an image
     */
    private fun showWorkFinished() {
        binding?.progressBar?.visibility = View.INVISIBLE
        binding?.buttonCancel?.isEnabled = false
        binding?.buttonRunWork?.isEnabled = true
    }

    private fun setViewStyle() {
        isDarkMode = SharedPreferencesManager.loadDarkModeStateFromPreferences()

        if (isDarkMode) {
            binding?.buttonRunWork?.style(R.style.ButtonNormalDarkMode)
            binding?.buttonCancel?.style(R.style.ButtonNormalDarkMode)
            binding?.buttonSeeFile?.style(R.style.ButtonNormalDarkMode)
        } else {
            binding?.buttonRunWork?.style(R.style.ButtonNormalLightMode)
            binding?.buttonCancel?.style(R.style.ButtonNormalLightMode)
            binding?.buttonSeeFile?.style(R.style.ButtonNormalLightMode)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}