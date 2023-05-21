package it.giovanni.arkivio.fragments.detail.notification

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.paris.extensions.style
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.NotificationHomeLayoutBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter
import it.giovanni.arkivio.utils.Globals
import it.giovanni.arkivio.utils.PermissionManager
import it.giovanni.arkivio.utils.SharedPreferencesManager

class NotificationHomeFragment: DetailFragment(), PermissionManager.PermissionListener {

    private var layoutBinding: NotificationHomeLayoutBinding? = null
    private val binding get() = layoutBinding

    override fun getTitle(): Int {
        return R.string.notification_title
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
        layoutBinding = NotificationHomeLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this)
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewStyle()
        askPermissions()

        binding?.buttonHomeNotification?.setOnClickListener {
            currentActivity.openDetail(Globals.NOTIFICATION, null)
        }
    }

    private fun askPermissions() {
        if (checkPermissions()) {
            binding?.buttonHomeNotification?.isEnabled = true
            return
        }
        binding?.buttonHomeNotification?.isEnabled = false
        PermissionManager.requestPermission(requireContext(), this, arrayOf(Manifest.permission.BLUETOOTH_CONNECT))
    }

    private fun checkPermissions(): Boolean {
        return PermissionManager.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_CONNECT)
    }

    override fun onPermissionResult(permissions: Array<String>, grantResults: IntArray) {
        binding?.buttonHomeNotification?.isEnabled = checkPermissions()
    }

    private fun setViewStyle() {
        isDarkMode = SharedPreferencesManager.loadDarkModeStateFromPreferences()
        if (isDarkMode)
            binding?.buttonHomeNotification?.style(R.style.ButtonNormalDarkMode)
        else
            binding?.buttonHomeNotification?.style(R.style.ButtonNormalLightMode)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}