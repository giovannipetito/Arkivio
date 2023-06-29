package it.giovanni.arkivio.fragments.detail.client

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.airbnb.paris.extensions.style
import it.giovanni.arkivio.App
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.UserCardBinding
import it.giovanni.arkivio.databinding.VolleyLayoutBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter
import it.giovanni.arkivio.restclient.volley.IVolley
import it.giovanni.arkivio.restclient.volley.MyVolleyClient
import it.giovanni.arkivio.utils.SharedPreferencesManager.Companion.loadDarkModeStateFromPreferences

class VolleyFragment: DetailFragment(), IVolley {

    private var layoutBinding: VolleyLayoutBinding? = null
    private val binding get() = layoutBinding

    override fun getTitle(): Int {
        return R.string.volley_title
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
        layoutBinding = VolleyLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this)
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewStyle()

        MyVolleyClient.getPosts(this)
        showProgressDialog()

        binding?.buttonVolleySend?.setOnClickListener {

            val title: String = binding?.editVolleyTitle?.text.toString()
            val text: String = binding?.editVolleyText?.text.toString()

            MyVolleyClient.addPosts(title, text, this)
            showProgressDialog()
        }
    }

    override fun onVolleyGetSuccess(message: String?) {

        hideProgressDialog()

        val itemBinding: UserCardBinding = UserCardBinding.inflate(layoutInflater, binding?.volleyUsersContainer, false)
        val itemView: View = itemBinding.root

        val label: TextView = itemBinding.userFirstName
        label.text = message

        if (isDarkMode)
            label.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
        else
            label.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark))

        binding?.volleyUsersContainer?.addView(itemView)
    }

    private fun setViewStyle() {
        isDarkMode = loadDarkModeStateFromPreferences()
        if (isDarkMode)
            binding?.buttonVolleySend?.style(R.style.ButtonNormalDarkMode)
        else
            binding?.buttonVolleySend?.style(R.style.ButtonNormalLightMode)
    }

    override fun onVolleyPostSuccess(message: String?) {
        hideProgressDialog()
        Toast.makeText(App.context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onVolleyFailure(message: String?) {
        hideProgressDialog()
        Toast.makeText(App.context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}