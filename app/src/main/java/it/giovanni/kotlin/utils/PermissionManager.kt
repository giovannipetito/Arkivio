package it.giovanni.kotlin.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import it.giovanni.kotlin.activities.PermissionActivity

class PermissionManager {

    companion object {

        private var listener: PermissionListener? = null
        private var permissions: Array<String>? = null
        private val MY_PERMISSION_REQUEST_CODE = 16

        fun checkSelfPermission(context: Context, permission: String): Boolean {
            return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }

        /**
         * Request permissions to the user. You must check if the permission has already been granted before using this method.
         * Unexpected results if you provide in input permissions that are already granted.
         *
         * @param context the activity context
         * @param permissionListener the listener for permissions callback
         * @param requestedPermissions array of String containing only not granted permissions
         * @param explanationRequired true if an explanation dialog must be shown before requesting permissions
         * @param explanationMsgResId array of int of stringResId describing the explanation for the corresponding permission
         * @param showNeverAskAgainExplanationDialog true if neverAskAgainExplanation dialog must be shown to the user. Set to false for requests not associated to explicit user actions
         * @param neverAskAgainMsgResId array of int of stringResId describing the explanation for the corresponding permission
         */

        fun requestPermission(
            context: Context,
            permissionListener: PermissionListener,
            requestedPermissions: Array<String>,
            explanationRequired: Boolean,
            explanationMsgResId: IntArray,
            showNeverAskAgainExplanationDialog: Boolean,
            neverAskAgainMsgResId: IntArray
        ) {
            listener = permissionListener
            permissions = requestedPermissions
            val intent = Intent(context, PermissionActivity::class.java)
            // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent)
        }

        fun requestPermission(
            context: Context,
            permissionListener: PermissionListener,
            requestedPermissions: Array<String>
        ) {
            listener = permissionListener
            permissions = requestedPermissions
            val intent = Intent(context, PermissionActivity::class.java)
            // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent)
        }

        fun onActivityCreated(callBackActivity: Activity) {
            ActivityCompat.requestPermissions(callBackActivity, permissions!!, MY_PERMISSION_REQUEST_CODE)
            return
        }

        fun onRequestPermissionsResult(
            callBackActivity: Activity,
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
        ) {
            if (listener != null)
                listener!!.onPermissionResult(permissions, grantResults)
            callBackActivity.finish()
        }
    }

    interface PermissionListener {
        fun onPermissionResult(permissions: Array<String>, grantResults: IntArray)
    }
}