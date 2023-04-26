package it.giovanni.arkivio.fragments.detail.puntonet.mvvvm.logininput

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.LoginInputLayoutBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter

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

    override fun onActionSearch(search_string: String) {
    }

    override fun onCreateBindingView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        layoutBinding = LoginInputLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this, requireContext())
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(LoginInputViewModel::class.java)

        binding?.buttonLogin?.setOnClickListener {
            val username: String = binding?.editUsername?.text.toString()
            val password: String = binding?.editPassword?.text.toString()

            val user = User(username, password)
            viewModel.setUser(user)
        }

        viewModel.user.observe(viewLifecycleOwner) { result ->

            val message: String = if (result.password.isEmpty())
                "Inserisci la password!"
            else
                "Ciao " + result.username

            binding?.labelUser?.text = message
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}