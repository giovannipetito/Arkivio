package it.giovanni.arkivio.fragments.detail.puntonet.mvvvm.users

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.ClientItemBinding
import it.giovanni.arkivio.databinding.SimpleRetrofitLayoutBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter
import it.giovanni.arkivio.restclient.retrofit.User
import it.giovanni.arkivio.utils.SharedPreferencesManager

/**
 * La classe View (activity o fragment) è dove definirai l'interfaccia utente (UI) che la tua app
 * mostrerà all'utente legando i vari elementi della UI (TextView, EditText, Button, ecc.) al ViewModel.
 *
 * Il ViewModel osserva un oggetto LiveData chiamato utente, che viene aggiornato quando viene
 * chiamato getUsersData(). La View quindi osserva l'oggetto utente e aggiorna la UI quando cambia.
 * Ciò consente di separare il codice della UI dalla business logic e semplifica il test e la
 * manutenzione del codice.
 */
class MvvmUsersFragment : DetailFragment() {

    private var layoutBinding: SimpleRetrofitLayoutBinding? = null
    private val binding get() = layoutBinding

    private lateinit var viewModel: MvvmUsersViewModel

    override fun getTitle(): Int {
        return R.string.mvvm_users_title
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

        val darkModePresenter = DarkModePresenter(this)
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isDarkMode = SharedPreferencesManager.loadDarkModeStateFromPreferences()

        viewModel = ViewModelProvider(requireActivity())[MvvmUsersViewModel::class.java]

        showProgressDialog()
        viewModel.getUsersData()

        viewModel.response.observe(viewLifecycleOwner) { response ->
            hideProgressDialog()
            Toast.makeText(context, response.message, Toast.LENGTH_LONG).show()
            showUsers(response.users)
        }
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