package it.giovanni.arkivio.biometric

import android.content.pm.PackageManager
import android.Manifest.permission.USE_FINGERPRINT
import android.Manifest.permission.USE_BIOMETRIC
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import android.os.Build
import android.content.Context
import androidx.core.app.ActivityCompat

class BiometricUtils {

    companion object {

        fun isBiometricPromptEnabled(): Boolean {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
        }

        fun isSdkVersionSupported(): Boolean {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
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
            else ActivityCompat.checkSelfPermission(context, USE_FINGERPRINT) == PackageManager.PERMISSION_GRANTED
        }
    }
}