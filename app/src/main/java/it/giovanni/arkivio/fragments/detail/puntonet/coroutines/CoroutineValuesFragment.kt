package it.giovanni.arkivio.fragments.detail.puntonet.coroutines

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.CoroutineValuesLayoutBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter
import it.giovanni.arkivio.utils.SharedPreferencesManager

class CoroutineValuesFragment : DetailFragment() {

    private var layoutBinding: CoroutineValuesLayoutBinding? = null
    private val binding get() = layoutBinding

    private lateinit var viewModel: CoroutineValuesViewModel

    override fun getTitle(): Int {
        return R.string.coroutine_values_title
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
        layoutBinding = CoroutineValuesLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this)
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isDarkMode = SharedPreferencesManager.loadDarkModeStateFromPreferences()

        viewModel = ViewModelProvider(requireActivity())[CoroutineValuesViewModel::class.java]

        viewModel.result1.observe(viewLifecycleOwner) { result ->
            val apiResult1 = "ApiResult 1: $result"
            binding?.labelResult1?.text = apiResult1
        }

        viewModel.result2.observe(viewLifecycleOwner) { result ->
            val apiResult2 = "ApiResult 2: $result"
            binding?.labelResult2?.text = apiResult2
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}