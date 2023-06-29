package it.giovanni.arkivio.fragments.detail.puntonet.mvvvm.users

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.UserCardBinding
import it.giovanni.arkivio.databinding.SimpleRetrofitLayoutBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.fragments.detail.puntonet.retrofitgetpost.User
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter
import it.giovanni.arkivio.utils.SharedPreferencesManager

/**
 * La classe View (activity o fragment) rappresenta l'interfaccia utente (UI) che l'applicazione
 * genererà legando i vari elementi della UI (TextView, EditText, Button, ecc.) al ViewModel.
 *
 * Il ViewModel osserva un oggetto LiveData chiamato response, che viene aggiornato quando viene
 * chiamato getUsers(). La View quindi osserva l'oggetto response e aggiorna la UI quando cambia.
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
        viewModel.getUsers()

        viewModel.response.observe(viewLifecycleOwner) { response ->
            hideProgressDialog()
            showUsers(response?.users)
        }
    }

    private fun showUsers(list: List<User>?) {

        binding?.retrofitUsersContainer?.removeAllViews()

        if (list.isNullOrEmpty())
            return

        for (user in list) {

            val itemBinding: UserCardBinding = UserCardBinding.inflate(layoutInflater, binding?.retrofitUsersContainer, false)
            val itemView: View = itemBinding.root

            val imageUrl: String = user.avatar

            Glide.with(requireActivity())
                .load(imageUrl)
                .placeholder(R.mipmap.logo_audioslave_blue)
                .error(R.mipmap.logo_audioslave_blue)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(54)))
                .into(itemBinding.userAvatar)

            val labelFirstName: TextView = itemBinding.userFirstName
            val labelLastName: TextView = itemBinding.userLastName

            labelFirstName.text = user.firstName
            labelLastName.text = user.lastName

            if (isDarkMode) {
                labelFirstName.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
                labelLastName.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
            } else {
                labelFirstName.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark))
                labelLastName.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark))
            }

            binding?.retrofitUsersContainer?.addView(itemView)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}