package it.giovanni.arkivio.fragments.detail.client

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.AsyncHttpLayoutBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter
import it.giovanni.arkivio.restclient.asynchttp.IAsyncHttpClient
import it.giovanni.arkivio.restclient.asynchttp.MyAsyncHttpClient
import it.giovanni.arkivio.restclient.asynchttp.Response

class AsyncHttpFragment: DetailFragment(), IAsyncHttpClient {

    private var layoutBinding: AsyncHttpLayoutBinding? = null
    private val binding get() = layoutBinding

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

    override fun onActionSearch(searchString: String) {
    }

    override fun onCreateBindingView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        layoutBinding = AsyncHttpLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this)
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MyAsyncHttpClient.getIp(this)
        showProgressDialog()
    }

    override fun onAsyncHttpSuccess(message: String?, response: Response?) {
        val resulMessage = message + ", IP: " + response?.ip
        Toast.makeText(context, resulMessage, Toast.LENGTH_SHORT).show()
        binding?.labelIp?.text = resulMessage
        hideProgressDialog()
    }

    override fun onAsyncHttpFailure(message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        binding?.labelIp?.text = message
        hideProgressDialog()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}