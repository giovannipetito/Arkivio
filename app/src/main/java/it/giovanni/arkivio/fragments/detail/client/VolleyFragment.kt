package it.giovanni.arkivio.fragments.detail.client

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.airbnb.paris.extensions.style
import it.giovanni.arkivio.App
import it.giovanni.arkivio.R
import it.giovanni.arkivio.customview.ButtonCustom
import it.giovanni.arkivio.databinding.ClientItemBinding
import it.giovanni.arkivio.databinding.VolleyLayoutBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter
import it.giovanni.arkivio.restclient.volley.IVolley
import it.giovanni.arkivio.restclient.volley.MyVolleyClient
import it.giovanni.arkivio.utils.SharedPreferencesManager.Companion.loadDarkModeStateFromPreferences
import kotlinx.android.synthetic.main.volley_layout.*

class VolleyFragment: DetailFragment(), IVolley {

    private var mBinding: VolleyLayoutBinding? = null
    private val binding get() = mBinding

    private var viewFragment: View? = null
    private var volleyUsersContainer: LinearLayout? = null

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // mBinding = DataBindingUtil.inflate(inflater, R.layout.volley_layout, container, false)
        mBinding = VolleyLayoutBinding.inflate(inflater, container, false)
        // mBinding = VolleyLayoutBinding.inflate(layoutInflater)
        val viewFragment = binding?.root

        val darkModePresenter = DarkModePresenter(this, requireContext())
        val model = DarkModeModel(requireContext())
        binding?.temp = model
        binding?.presenter = darkModePresenter

        return viewFragment
    }

    override fun onCreateBindingView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        // mBinding = DataBindingUtil.inflate(inflater, R.layout.volley_layout, container, false)
        // mBinding = VolleyLayoutBinding.inflate(inflater, container, false)
        // mBinding = VolleyLayoutBinding.inflate(layoutInflater)
        // viewFragment = binding?.root

        // val darkModePresenter = DarkModePresenter(this, requireContext())
        // val model = DarkModeModel(requireContext())
        // binding?.temp = model
        // binding?.presenter = darkModePresenter

        // buttonVolleySend = binding?.buttonVolleySend
        // buttonVolleySend = viewFragment?.findViewById(R.id.button_volley_send)

        return View(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // super.onViewCreated(view, savedInstanceState)

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

        // TODO ----------------------------------------//
        // TODO INTUIZIONE! Prova con: runOnUIThread ---//
        // TODO ----------------------------------------//

        // volleyUsersContainer = binding?.volleyUsersContainer
        // volleyUsersContainer = viewFragment?.findViewById(R.id.volley_users_container)

        val clientItemBinding: ClientItemBinding = ClientItemBinding.inflate(layoutInflater, volley_users_container, false)
        // val clientItemBinding: ClientItemBinding = ClientItemBinding.inflate(layoutInflater, volleyUsersContainer, false)

        val rowView: View = clientItemBinding.root

        val labelUsername: TextView = clientItemBinding.clientText1

        labelUsername.text = message

        isDarkMode = loadDarkModeStateFromPreferences()
        if (isDarkMode)
            labelUsername.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
        else
            labelUsername.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark))

        volley_users_container.addView(rowView)
        // volleyUsersContainer?.addView(rowView)
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
        Toast.makeText(App.context, message, Toast.LENGTH_LONG).show()
    }

    override fun onVolleyFailure(message: String?) {
        hideProgressDialog()
        Toast.makeText(App.context, message, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }
}