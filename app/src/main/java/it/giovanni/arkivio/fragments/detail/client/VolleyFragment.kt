package it.giovanni.arkivio.fragments.detail.client

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import it.giovanni.arkivio.App
import it.giovanni.arkivio.R
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.restclient.volley.IVolley
import it.giovanni.arkivio.restclient.volley.MyVolleyClient
import kotlinx.android.synthetic.main.volley_layout.*

class VolleyFragment: DetailFragment(), IVolley {

    private var viewFragment: View? = null

    override fun getLayout(): Int {
        return R.layout.volley_layout
    }

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

        MyVolleyClient.getPosts(this)
        showProgressDialog()

        button_volley_send.setOnClickListener {
            val title: String = edit_volley_title.text.toString()
            val text: String = edit_volley_text.text.toString()

            MyVolleyClient.addPosts(title, text, this)
            showProgressDialog()
        }
    }

    override fun onVolleyGetSuccess(message: String?) {

        hideProgressDialog()

        val rowView = LayoutInflater.from(App.context).inflate(
            R.layout.client_item,
            volley_users_container,
            false
        )

        val labelUsername: TextView = rowView.findViewById(R.id.client_text1)
        labelUsername.text = message

        if (isDarkMode)
            labelUsername.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
        else
            labelUsername.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark))

        volley_users_container.addView(rowView)
    }

    override fun onVolleyPostSuccess(message: String?) {
        hideProgressDialog()
        Toast.makeText(App.context, message, Toast.LENGTH_LONG).show()
    }

    override fun onVolleyFailure(message: String?) {
        hideProgressDialog()
        Toast.makeText(App.context, message, Toast.LENGTH_LONG).show()
    }
}