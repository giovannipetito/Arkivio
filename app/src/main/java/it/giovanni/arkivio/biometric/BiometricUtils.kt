package it.giovanni.arkivio.biometric

import android.Manifest.permission.USE_BIOMETRIC
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.hardware.fingerprint.FingerprintManagerCompat

class BiometricUtils {

    companion object {

        fun isBiometricPromptEnabled(): Boolean {
            return true
        }

        fun isSdkVersionSupported(): Boolean {
            return true
        }

        fun isHardwareSupported(context: Context): Boolean {
            return FingerprintManagerCompat.from(context).isHardwareDetected
        }

        fun isFingerprintAvailable(context: Context): Boolean {
            return FingerprintManagerCompat.from(context).hasEnrolledFingerprints()
        }

        fun isPermissionGranted(context: Context): Boolean {
            return if (isBiometricPromptEnabled())
                ActivityCompat.checkSelfPermission(context, USE_BIOMETRIC) == PackageManager.PERMISSION_GRANTED
            else false
        }
    }
}