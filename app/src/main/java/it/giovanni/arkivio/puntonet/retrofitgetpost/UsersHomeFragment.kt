package it.giovanni.arkivio.puntonet.retrofitgetpost

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.airbnb.paris.extensions.style
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.UsersHomeLayoutBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter
import it.giovanni.arkivio.utils.Globals
import it.giovanni.arkivio.utils.SharedPreferencesManager

class UsersHomeFragment : DetailFragment() {

    private var layoutBinding: UsersHomeLayoutBinding? = null
    private val binding get() = layoutBinding

    private lateinit var viewModel: UsersViewModel

    override fun getTitle(): Int {
        return R.string.users_home_title
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
        layoutBinding = UsersHomeLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this)
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isDarkMode = SharedPreferencesManager.loadDarkModeStateFromPreferences()

        viewModel = ViewModelProvider(requireActivity())[UsersViewModel::class.java]

        binding?.buttonGetUsers?.setOnClickListener {
            currentActivity.openDetail(Globals.USERS, null)
        }

        binding?.buttonAddUser?.setOnClickListener {
            val name: String = binding?.editName?.text.toString()
            val job: String = binding?.editJob?.text.toString()

            val utente = Utente(name, job)
            viewModel._utente.value = utente

            if (name.isNotEmpty() && job.isNotEmpty())
                currentActivity.openDetail(Globals.USERS_DETAIL, null)
        }

        if (isDarkMode) {
            binding?.buttonGetUsers?.style(R.style.ButtonNormalDarkMode)
            binding?.buttonAddUser?.style(R.style.ButtonNormalDarkMode)
        } else {
            binding?.buttonGetUsers?.style(R.style.ButtonNormalLightMode)
            binding?.buttonAddUser?.style(R.style.ButtonNormalLightMode)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}