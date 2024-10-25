package it.giovanni.arkivio.puntonet.mvvvm.logininput

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.LoginResultLayoutBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter
import it.giovanni.arkivio.utils.SharedPreferencesManager

class LoginResultFragment : DetailFragment() {

    private var layoutBinding: LoginResultLayoutBinding? = null
    private val binding get() = layoutBinding

    private lateinit var viewModel: LoginInputViewModel

    override fun getTitle(): Int {
        return R.string.login_result_title
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
        layoutBinding = LoginResultLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this)
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isDarkMode = SharedPreferencesManager.loadDarkModeStateFromPreferences()

        viewModel = ViewModelProvider(requireActivity())[LoginInputViewModel::class.java]

        viewModel.message.observe(viewLifecycleOwner) { result ->
            binding?.labelLoginResult?.text = result
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}