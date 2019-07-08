package it.giovanni.kotlin.activities

import android.app.Activity
import android.os.Bundle
import android.view.WindowManager
import it.giovanni.kotlin.utils.PermissionManager

class PermissionActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PermissionManager.onActivityCreated(this)
        window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        PermissionManager.onRequestPermissionsResult(this, requestCode, permissions, grantResults)
    }
}