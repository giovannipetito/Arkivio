package it.giovanni.arkivio.fragments.detail.client

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.ClientItemBinding
import it.giovanni.arkivio.databinding.SimpleRetrofitLayoutBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter
import it.giovanni.arkivio.restclient.retrofit.IRetrofit
import it.giovanni.arkivio.restclient.retrofit.SimpleRetrofitClient
import it.giovanni.arkivio.restclient.retrofit.User
import it.giovanni.arkivio.utils.SharedPreferencesManager.Companion.loadDarkModeStateFromPreferences

class SimpleRetrofitFragment: DetailFragment(), IRetrofit {

    private var layoutBinding: SimpleRetrofitLayoutBinding? = null
    private val binding get() = layoutBinding

    override fun getTitle(): Int {
        return R.string.simple_retrofit_title
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
        layoutBinding = SimpleRetrofitLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this, requireContext())
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isDarkMode = loadDarkModeStateFromPreferences()

        SimpleRetrofitClient.getUsers(this)
        showProgressDialog()
    }

    override fun onRetrofitSuccess(users: List<User?>?, message: String?) {
        hideProgressDialog()
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        showUsers(users)
    }

    override fun onRetrofitFailure(message: String?) {
        hideProgressDialog()
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    private fun showUsers(list: List<User?>?) {
        if (list == null)
            return
        if (list.isEmpty())
            return
        for (user in list) {

            val itemBinding: ClientItemBinding = ClientItemBinding.inflate(layoutInflater, binding?.retrofitUsersContainer, false)
            val itemView: View = itemBinding.root

            val labelUsername: TextView = itemBinding.clientText1
            labelUsername.text = user?.username

            val labelEmail: TextView = itemBinding.clientText2
            labelEmail.text = user?.email

            if (isDarkMode) {
                labelUsername.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
                labelEmail.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
            }
            else {
                labelUsername.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark))
                labelEmail.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark))
            }

            binding?.retrofitUsersContainer?.addView(itemView)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}