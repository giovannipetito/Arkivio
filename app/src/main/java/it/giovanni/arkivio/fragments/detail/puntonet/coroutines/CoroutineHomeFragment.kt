package it.giovanni.arkivio.fragments.detail.puntonet.coroutines

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.CoroutineHomeLayoutBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter
import it.giovanni.arkivio.utils.Globals

class CoroutineHomeFragment : DetailFragment() {

    private var layoutBinding: CoroutineHomeLayoutBinding? = null
    private val binding get() = layoutBinding

    override fun getTitle(): Int {
        return R.string.coroutine_home_title
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
        layoutBinding = CoroutineHomeLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this)
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.labelCoroutineBasics?.setOnClickListener {
            currentActivity.openDetail(Globals.COROUTINE_BASICS, null)
        }
        binding?.labelCoroutineScopes?.setOnClickListener {
            currentActivity.openDetail(Globals.COROUTINE_SCOPES, null)
        }
        binding?.labelCoroutineJobsCancellation?.setOnClickListener {
            currentActivity.openDetail(Globals.COROUTINE_JOBS_CANCELLATION, null)
        }
        binding?.labelCoroutineRunBlocking?.setOnClickListener {
            currentActivity.openDetail(Globals.COROUTINE_RUN_BLOCKING, null)
        }
        binding?.labelCoroutineValues?.setOnClickListener {
            currentActivity.openDetail(Globals.COROUTINE_VALUES, null)
        }
        binding?.labelCoroutineChannels?.setOnClickListener {
            currentActivity.openDetail(Globals.COROUTINE_CHANNELS, null)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}