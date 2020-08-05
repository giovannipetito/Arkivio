package it.giovanni.arkivio.biometric

import android.content.DialogInterface
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.annotation.TargetApi
import android.content.Context
import android.os.CancellationSignal
import androidx.annotation.NonNull

class BiometricManager private constructor(biometricBuilder: BiometricBuilder) : BiometricManagerV23() {

    private val mCancellationSignal = CancellationSignal()

    init {
        this.context = biometricBuilder.context
        this.title = biometricBuilder.title
        this.subtitle = biometricBuilder.subtitle
        this.description = biometricBuilder.description
        this.cancel = biometricBuilder.cancel
    }

    fun authenticate(@NonNull biometricCallback: BiometricCallback) {

        if (title == null) {
            biometricCallback.onBiometricAuthenticationInternalError("Biometric Dialog title cannot be null")
            return
        }

        if (subtitle == null) {
            biometricCallback.onBiometricAuthenticationInternalError("Biometric Dialog subtitle cannot be null")
            return
        }

        if (description == null) {
            biometricCallback.onBiometricAuthenticationInternalError("Biometric Dialog description cannot be null")
            return
        }

        if (cancel == null) {
            biometricCallback.onBiometricAuthenticationInternalError("Biometric Dialog negative button text cannot be null")
            return
        }

        if (!BiometricUtils.isSdkVersionSupported()) {
            biometricCallback.onSdkVersionNotSupported()
            return
        }

        if (!BiometricUtils.isPermissionGranted(context!!)) {
            biometricCallback.onBiometricAuthenticationPermissionNotGranted()
            return
        }

        if (!BiometricUtils.isHardwareSupported(context!!)) {
            biometricCallback.onBiometricAuthenticationNotSupported()
            return
        }

        if (!BiometricUtils.isFingerprintAvailable(context!!)) {
            biometricCallback.onBiometricAuthenticationNotAvailable()
            return
        }

        displayBiometricDialog(biometricCallback)
    }

    fun cancelAuthentication() {
        if (BiometricUtils.isBiometricPromptEnabled()) {
            if (!mCancellationSignal.isCanceled)
                mCancellationSignal.cancel()
        } else {
            if (!mCancellationSignalV23.isCanceled)
                mCancellationSignalV23.cancel()
        }
    }

    private fun displayBiometricDialog(biometricCallback: BiometricCallback) {
        if (BiometricUtils.isBiometricPromptEnabled())
            displayBiometricPrompt(biometricCallback)
        else
            displayBiometricPromptV23(biometricCallback)
    }

    @TargetApi(Build.VERSION_CODES.P)
    private fun displayBiometricPrompt(biometricCallback: BiometricCallback) {
        BiometricPrompt.Builder(context)
            .setTitle(title!!)
            .setSubtitle(subtitle!!)
            .setDescription(description!!)
            .setNegativeButton(cancel!!, context!!.mainExecutor,
                DialogInterface.OnClickListener { _, _ -> biometricCallback.onAuthenticationCancelled() })
            .build()
            .authenticate(
                mCancellationSignal,
                context!!.mainExecutor,
                BiometricCallbackV28(biometricCallback)
            )
    }

    class BiometricBuilder(val context: Context) {
        var title: String? = null
        var subtitle: String? = null
        var description: String? = null
        var cancel: String? = null

        fun setTitle(@NonNull title: String): BiometricBuilder {
            this.title = title
            return this
        }

        fun setSubtitle(@NonNull subtitle: String): BiometricBuilder {
            this.subtitle = subtitle
            return this
        }

        fun setDescription(@NonNull description: String): BiometricBuilder {
            this.description = description
            return this
        }

        fun setNegativeButtonText(@NonNull cancel: String): BiometricBuilder {
            this.cancel = cancel
            return this
        }

        fun build(): BiometricManager {
            return BiometricManager(this)
        }
    }
}