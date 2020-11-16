package it.giovanni.arkivio.fragments

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import it.giovanni.arkivio.R
import it.giovanni.arkivio.biometric.BiometricCallback
import it.giovanni.arkivio.biometric.BiometricManager
import it.giovanni.arkivio.customview.popup.CustomDialogPopup
import it.giovanni.arkivio.utils.PermissionManager
import it.giovanni.arkivio.utils.SharedPreferencesManager.Companion.loadRememberMeFromPreferences
import it.giovanni.arkivio.utils.SharedPreferencesManager.Companion.saveRememberMeToPreferences
import it.giovanni.arkivio.utils.Utils.Companion.isOnline
import kotlinx.android.synthetic.main.login_layout.*

class LoginFragment : BaseFragment(SectionType.LOGIN), BiometricCallback, PermissionManager.PermissionListener {

    private var biometricManager: BiometricManager? = null
    private var customPopup: CustomDialogPopup? = null
    private var hasPermission: Boolean = false
    private lateinit var loginButton: Button
    private var rememberMe: Boolean = false
    private var action: Action? = null

    internal enum class Action {
        NONE,
        REGISTER
    }

    override fun getTitle(): Int {
        return NO_TITLE
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        currentActivity.setStatusBarTransparent()
        loginButton = view?.findViewById(R.id.login_button) as Button

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        action = Action.NONE

        checkbox_remember_me.isChecked = loadRememberMeFromPreferences()

        username.getInputText().setOnKeyListener { _, _, _ ->
            loginButton.isEnabled = username.getText().trim().isNotEmpty() && password.getText().trim().isNotEmpty()
            false
        }

        username.getInputText().addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                loginButton.isEnabled = username.getText().trim().isNotEmpty() && password.getText().trim().isNotEmpty()
            }
        })

        password.getInputText().setOnKeyListener { _, _, _ ->
            loginButton.isEnabled = username.getText().trim().isNotEmpty() && password.getText().trim().isNotEmpty()
            false
        }

        password.getInputText().addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                loginButton.isEnabled = username.getText().trim().isNotEmpty() && password.getText().trim().isNotEmpty()
            }
        })

        loginButton.setOnClickListener {
            if (isOnline()) {
                showProgressDialog()
                currentActivity.openMainFragment()
            } else
                Toast.makeText(context,"Errore di connessione", Toast.LENGTH_LONG).show()
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

        checkbox_remember_me.setOnCheckedChangeListener { _, isChecked ->
            rememberMe = isChecked
            saveRememberMeToPreferences(rememberMe)
        }

        val apiVersion = Build.VERSION.SDK_INT
        api_version_text.text = getString(R.string.api_version, "" + apiVersion)
    }

    override fun onSdkVersionNotSupported() {
        Toast.makeText(context, getString(R.string.biometric_error_sdk), Toast.LENGTH_LONG).show()
    }

    override fun onBiometricAuthenticationNotSupported() {
        Toast.makeText(context, getString(R.string.biometric_error_hardware), Toast.LENGTH_LONG).show()
    }

    override fun onBiometricAuthenticationNotAvailable() {

        customPopup = CustomDialogPopup(currentActivity, R.style.PopupTheme)
        customPopup!!.setCancelable(false)
        customPopup!!.setTitle("")
        customPopup!!.setMessage(getString(R.string.biometric_error_fingerprint))

        customPopup!!.setButtons(
            resources.getString(R.string.button_register), {
                action = Action.REGISTER
                askPermission()
                customPopup!!.dismiss()
            },
            resources.getString(R.string.button_cancel), {
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

        val action = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
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