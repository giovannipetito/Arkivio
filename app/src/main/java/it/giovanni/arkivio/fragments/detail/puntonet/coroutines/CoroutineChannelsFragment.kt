package it.giovanni.arkivio.fragments.detail.puntonet.coroutines

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.CoroutineChannelsLayoutBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter
import it.giovanni.arkivio.utils.SharedPreferencesManager

/**
 * The channels are used for asynchronous communication between the coroutines allowing to pass a
 * stream of values from one coroutine to another and the elements inside the channel are processed
 * in the same order as they arrive in.
 *
 */
class CoroutineChannelsFragment : DetailFragment() {

    private var layoutBinding: CoroutineChannelsLayoutBinding? = null
    private val binding get() = layoutBinding

    private lateinit var viewModel: CoroutineChannelsViewModel

    override fun getTitle(): Int {
        return R.string.coroutine_channels_title
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
        layoutBinding = CoroutineChannelsLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this, requireContext())
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isDarkMode = SharedPreferencesManager.loadDarkModeStateFromPreferences()

        viewModel = ViewModelProvider(requireActivity())[CoroutineChannelsViewModel::class.java]
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}