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
import android.widget.Toast
import it.giovanni.arkivio.R
import it.giovanni.arkivio.biometric.BiometricCallback
import it.giovanni.arkivio.biometric.BiometricManager
import it.giovanni.arkivio.customview.popup.CustomDialogPopup
import it.giovanni.arkivio.databinding.LoginLayoutBinding
import it.giovanni.arkivio.utils.PermissionManager
import it.giovanni.arkivio.utils.SharedPreferencesManager.Companion.loadRememberMeFromPreferences
import it.giovanni.arkivio.utils.SharedPreferencesManager.Companion.saveRememberMeToPreferences
import it.giovanni.arkivio.utils.Utils.Companion.clearCache
import it.giovanni.arkivio.utils.Utils.Companion.isOnline

class LoginFragment : BaseFragment(SectionType.LOGIN), BiometricCallback, PermissionManager.PermissionListener {

    private var layoutBinding: LoginLayoutBinding? = null
    private val binding get() = layoutBinding

    private var biometricManager: BiometricManager? = null
    private var customPopup: CustomDialogPopup? = null
    private var hasPermission: Boolean = false
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
        layoutBinding = LoginLayoutBinding.inflate(inflater, container, false)

        currentActivity.setStatusBarTransparent()

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        action = Action.NONE

        binding?.checkboxRememberMe?.isChecked = loadRememberMeFromPreferences()

        binding?.username?.getInputText()?.setOnKeyListener { _, _, _ ->
            binding?.loginButton?.isEnabled = binding?.username?.getText()?.trim()?.isNotEmpty()!! && binding?.password?.getText()?.trim()?.isNotEmpty()!!
            false
        }

        binding?.username?.getInputText()?.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                binding?.loginButton?.isEnabled = binding?.username?.getText()?.trim()?.isNotEmpty()!! && binding?.password?.getText()?.trim()?.isNotEmpty()!!
            }
        })

        binding?.password?.getInputText()?.setOnKeyListener { _, _, _ ->
            binding?.loginButton?.isEnabled = binding?.username?.getText()?.trim()?.isNotEmpty()!! && binding?.password?.getText()?.trim()?.isNotEmpty()!!
            false
        }

        binding?.password?.getInputText()?.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                binding?.loginButton?.isEnabled = binding?.username?.getText()?.trim()?.isNotEmpty()!! && binding?.password?.getText()?.trim()?.isNotEmpty()!!
            }
        })

        binding?.loginButton?.setOnClickListener {
            if (isOnline()) {
                showProgressDialog()
                currentActivity.openMainFragment()
            } else
                Toast.makeText(context,"Errore di connessione", Toast.LENGTH_LONG).show()
        }

        binding?.imageFingerprint?.setOnClickListener {
            biometricManager = BiometricManager.BiometricBuilder(requireContext())
                .setTitle(getString(R.string.biometric_title))
                .setSubtitle(getString(R.string.biometric_subtitle))
                .setDescription(getString(R.string.biometric_description))
                .setNegativeButtonText(getString(R.string.biometric_cancel_button))
                .build()

            // Start authentication
            biometricManager?.authenticate(this)
        }

        binding?.checkboxRememberMe?.setOnCheckedChangeListener { _, isChecked ->
            rememberMe = isChecked
            saveRememberMeToPreferences(rememberMe)
        }

        binding?.clearCache?.setOnClickListener {
            clearCache(context)
        }

        val apiVersion = Build.VERSION.SDK_INT
        binding?.apiVersionText?.text = getString(R.string.api_version, "" + apiVersion)
    }

    override fun onSdkVersionNotSupported() {
        Toast.makeText(context, getString(R.string.biometric_error_sdk), Toast.LENGTH_LONG).show()
    }

    override fun onBiometricAuthenticationNotSupported() {
        Toast.makeText(context, getString(R.string.biometric_error_hardware), Toast.LENGTH_LONG).show()
    }

    override fun onBiometricAuthenticationNotAvailable() {

        customPopup = CustomDialogPopup(currentActivity, R.style.PopupTheme)
        customPopup?.setCancelable(false)
        customPopup?.setTitle("")
        customPopup?.setMessage(getString(R.string.biometric_error_fingerprint))

        customPopup?.setButtons(
            resources.getString(R.string.button_register), {
                action = Action.REGISTER
                askPermission()
                customPopup?.dismiss()
            },
            resources.getString(R.string.button_cancel), {
                customPopup?.dismiss()
            }
        )
        customPopup?.show()
    }

    private fun askPermission() {
        checkPermission()
        if (hasPermission)
            open()
    }

    private fun checkPermission() {
        hasPermission = PermissionManager.checkSelfPermission(requireContext(), Manifest.permission.USE_BIOMETRIC)
    }

    override fun onPermissionResult(permissions: Array<String>, grantResults: IntArray) {
        checkPermission()
        open()
    }

    private fun open() {

        val action = android.provider.Settings.ACTION_BIOMETRIC_ENROLL

        val intent = Intent(action)
        /*
        Oppure:
        val intent = Intent()
        intent.action = action
        */
        startActivity(intent)
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
        biometricManager?.cancelAuthentication()
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

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}