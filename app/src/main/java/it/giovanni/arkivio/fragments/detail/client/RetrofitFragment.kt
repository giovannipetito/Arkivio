package it.giovanni.arkivio.fragments.detail.client

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import it.giovanni.arkivio.R
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.restclient.retrofit.IRetrofit
import it.giovanni.arkivio.restclient.retrofit.MyRetrofitClient
import it.giovanni.arkivio.restclient.retrofit.User
import kotlinx.android.synthetic.main.retrofit_layout.*

/**
 * Retrofit è una libreria HTTP per Android che gestisce le chiamate REST.
 */

class RetrofitFragment: DetailFragment(), IRetrofit {

    private var viewFragment: View? = null

    override fun getLayout(): Int {
        return R.layout.retrofit_layout
    }

    override fun getTitle(): Int {
        return R.string.retrofit_title
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        viewFragment = super.onCreateView(inflater, container, savedInstanceState)
        return viewFragment
    }

    override fun onCreateBindingView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        TODO("Not yet implemented")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MyRetrofitClient.getUsers(this)
        showProgressDialog()
    }

    override fun onRetrofitSuccess(message: String?, list: List<User?>?) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        hideProgressDialog()
        showUsers(list)
    }

    override fun onRetrofitFailure(message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        hideProgressDialog()
    }

    private fun showUsers(list: List<User?>?) {
        if (list == null)
            return
        if (list.isEmpty())
            return
        for (user in list) {

            val rowView = LayoutInflater.from(context).inflate(
                R.layout.client_item,
                retrofit_users_container,
                false
            )

            val labelUsername: TextView = rowView.findViewById(R.id.client_text1)
            labelUsername.text = user?.username

            val labelEmail: TextView = rowView.findViewById(R.id.client_text2)
            labelEmail.text = user?.email

            if (isDarkMode) {
                labelUsername.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
                labelEmail.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
            }
            else {
                labelUsername.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark))
                labelEmail.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark))
            }

            retrofit_users_container.addView(rowView)
        }
    }
}