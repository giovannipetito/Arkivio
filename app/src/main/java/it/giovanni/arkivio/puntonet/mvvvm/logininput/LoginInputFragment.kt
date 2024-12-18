package it.giovanni.arkivio.puntonet.mvvvm.logininput

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.airbnb.paris.extensions.style
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.LoginInputLayoutBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter
import it.giovanni.arkivio.utils.Globals
import it.giovanni.arkivio.utils.SharedPreferencesManager

/**
 * Example of MVVM pattern without fetching data from an API, but from EditText user input stored in
 * a Model class.
 *
 * In this example, the user input from the EditText fields is stored in a User class. When the user
 * clicks the login button, the User object is created and passed to the LoginInputViewModel class
 * using setUser() method. The LoginInputViewModel then updates the user with the new User object.
 *
 * In the View, the LoginInputViewModel instance is obtained using ViewModelProvider and the
 * MutableLiveData<User> object is observed using getUser() method. Whenever there is a change in
 * the user, the onChanged() method of the Observer is called, which can be used to update the UI
 * or perform any other necessary actions.
 */
class LoginInputFragment : DetailFragment() {

    private var layoutBinding: LoginInputLayoutBinding? = null
    private val binding get() = layoutBinding

    private lateinit var viewModel: LoginInputViewModel

    override fun getTitle(): Int {
        return R.string.login_input_title
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
        layoutBinding = LoginInputLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this)
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewStyle()

        viewModel = ViewModelProvider(requireActivity())[LoginInputViewModel::class.java]

        binding?.buttonLogin?.setOnClickListener {
            val username: String = binding?.editUsername?.text.toString()
            val password: String = binding?.editPassword?.text.toString()

            val user = User(username, password)

            viewModel._user.value = user

            viewModel.showMessage1()

            viewModel.showMessage2()
        }

        viewModel.user.observe(viewLifecycleOwner) { result ->

            val message: String = if (result.password.isEmpty())
                "Inserisci la password!"
            else
                "Ciao " + result.username

            binding?.labelUser1?.text = message
        }

        viewModel.message.observe(viewLifecycleOwner) { message ->
            binding?.labelUser2?.text = message
        }

        viewModel.message.observe(viewLifecycleOwner) { message ->
            binding?.labelUser3?.text = message
        }

        binding?.buttonLoginResult?.setOnClickListener {
            currentActivity.openDetail(Globals.MVVM_LOGIN_RESULT, null)
        }
    }

    private fun setViewStyle() {
        isDarkMode = SharedPreferencesManager.loadDarkModeStateFromPreferences()
        if (isDarkMode) {
            binding?.buttonLogin?.style(R.style.ButtonNormalDarkMode)
            binding?.buttonLoginResult?.style(R.style.ButtonNormalDarkMode)
        } else {
            binding?.buttonLogin?.style(R.style.ButtonNormalLightMode)
            binding?.buttonLoginResult?.style(R.style.ButtonNormalLightMode)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}