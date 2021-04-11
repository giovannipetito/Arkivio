package it.giovanni.arkivio.fragments.detail.client

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import it.giovanni.arkivio.R
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.restclient.asynchttp.IAsyncHttpClient
import it.giovanni.arkivio.restclient.asynchttp.MyAsyncHttpClient
import it.giovanni.arkivio.restclient.asynchttp.Response

class AsyncHttpFragment: DetailFragment(), IAsyncHttpClient {

    private var viewFragment: View? = null

    override fun getLayout(): Int {
        return R.layout.async_http_layout
    }

    override fun getTitle(): Int {
        return R.string.async_http_title
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

        MyAsyncHttpClient.getIp(this)
        showProgressDialog()
    }

    override fun onAsyncHttpSuccess(message: String?, response: Response?) {
        Toast.makeText(context, message + ", IP: " + response?.ip, Toast.LENGTH_LONG).show()
        hideProgressDialog()
    }

    override fun onAsyncHttpFailure(message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        hideProgressDialog()
    }
}