package it.giovanni.arkivio.puntonet.workmanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.work.WorkInfo
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.SimpleWorkerLayoutBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter

class SimpleWorkerFragment : DetailFragment() {

    private var layoutBinding: SimpleWorkerLayoutBinding? = null
    private val binding get() = layoutBinding

    private val viewModel: SimpleWorkerViewModel by viewModels {
        WorkerViewModelProviderFactory(currentActivity.application)
    }

    override fun getTitle(): Int {
        return R.string.simple_worker_title
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

    override fun onActionSearch(searchString: String) {
    }

    override fun onCreateBindingView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        layoutBinding = SimpleWorkerLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this)
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.workInfos.observe(viewLifecycleOwner, workInfosObserver())
    }

    private fun workInfosObserver(): Observer<List<WorkInfo>> {
        return Observer { listOfWorkInfo ->

            if (listOfWorkInfo.isEmpty()) {
                return@Observer
            }

            val workInfo = listOfWorkInfo[0]

            if (workInfo.state.isFinished) {

                val outputMessage = workInfo.outputData.getString(KEY_SIMPLE_MESSAGE)

                if (!outputMessage.isNullOrEmpty()) {
                    binding?.labelSimpleWorker?.text = outputMessage
                }
            } else {
                val runningMessage = "Single work is running!"
                binding?.labelSimpleWorker?.text = runningMessage
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}