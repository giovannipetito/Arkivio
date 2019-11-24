package it.giovanni.kotlin.fragments

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import it.giovanni.kotlin.biometric.BiometricCallback
import kotlinx.android.synthetic.main.login_layout.*
import android.widget.Toast
import it.giovanni.kotlin.R
import it.giovanni.kotlin.biometric.BiometricManager
import it.giovanni.kotlin.customview.popup.CustomDialogPopup
import it.giovanni.kotlin.utils.PermissionManager

class LoginFragment : BaseFragment(SectionType.LOGIN), BiometricCallback, PermissionManager.PermissionListener {

    private var biometricManager: BiometricManager? = null
    private var customPopup: CustomDialogPopup? = null
    private var hasPermission: Boolean = false
    private var action: Action? = null

    internal enum class Action {
        NONE,
        REGISTER
    }

    override fun getTitle(): Int {
        return NO_TITLE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentActivity.window.statusBarColor = ContextCompat.getColor(context!!, R.color.colorPrimary)
        action = Action.NONE

        login_button.setOnClickListener {
            showProgressDialog()
            currentActivity.openMainFragment()
        }

        image_fingerprint.setOnClickListener {
            biometricManager = BiometricManager.BiometricBuilder(context!!)
                .setTitle(getString(R.string.biometric_title))
                .setSubtitle(getString(R.string.biometric_subtitle))
                .setDescription(getString(R.string.biometric_description))
                .setNegativeButtonText(getString(R.string.biometric_cancel_button))
                .build()

            // Start authentication
            biometricManager!!.authenticate(this)
        }
    }

    override fun onSdkVersionNotSupported() {
        Toast.makeText(context, getString(R.string.biometric_error_sdk), Toast.LENGTH_LONG).show()
    }

    override fun onBiometricAuthenticationNotSupported() {
        Toast.makeText(context, getString(R.string.biometric_error_hardware), Toast.LENGTH_LONG).show()
    }

    override fun onBiometricAuthenticationNotAvailable() {

        customPopup = CustomDialogPopup(
            currentActivity,
            R.style.PopupTheme
        )
        customPopup!!.setCancelable(false)
        customPopup!!.setTitle("")
        customPopup!!.setMessage(getString(R.string.biometric_error_fingerprint))

        customPopup!!.setButtons(
            resources.getString(R.string.popup_button_register), View.OnClickListener {
                action = Action.REGISTER
                askPermission()
                customPopup!!.dismiss()
            },
            resources.getString(R.string.popup_button_cancel), View.OnClickListener {
                customPopup!!.dismiss()
            }
        )
        customPopup!!.show()
    }

    private fun askPermission() {
        checkPermission()
        if (hasPermission)
            open()
    }

    private fun checkPermission() {
        hasPermission = PermissionManager.checkSelfPermission(context!!, Manifest.permission.USE_BIOMETRIC)
    }

    override fun onPermissionResult(permissions: Array<String>, grantResults: IntArray) {
        checkPermission()
        open()
    }

    private fun open() {

        val action = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            android.provider.Settings.ACTION_FINGERPRINT_ENROLL
        } else {
            android.provider.Settings.ACTION_SECURITY_SETTINGS
        }

        val intent = Intent(action)
        /*
        Oppure:
        val intent = Intent()
        intent.action = action
        */
        startActivity(intent)
        // startActivityForResult(intent, 0)
    }

    override fun onBiometricAuthenticationPermissionNotGranted() {
        Toast.makeText(context, getString(R.string.biometric_error_permission), Toast.LENGTH_LONG).show()
    }

    override fun onBiometricAuthenticationInternalError(error: String) {
        Toast.makeText(context, error, Toast.LENGTH_LONG).show()
    }

    override fun onAuthenticationFailed() {
        Toast.makeText(context, getString(R.string.biometric_failure), Toast.LENGTH_LONG).show()
    }

    override fun onAuthenticationCancelled() {
        Toast.makeText(context, getString(R.string.biometric_cancelled), Toast.LENGTH_LONG).show()
        biometricManager!!.cancelAuthentication()
    }

    override fun onAuthenticationSuccessful() {
        Toast.makeText(context, getString(R.string.biometric_success), Toast.LENGTH_LONG).show()

        showProgressDialog()
        currentActivity.openMainFragment()
    }

    override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence) {
        Toast.makeText(context, helpString, Toast.LENGTH_LONG).show()
    }

    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
        Toast.makeText(context, errString, Toast.LENGTH_LONG).show()
    }
}