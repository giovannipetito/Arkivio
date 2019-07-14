package it.giovanni.kotlin.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

abstract class BaseActivity : AppCompatActivity() {

    var TAG: String = "BaseActivity"
    open fun openDetail(detailType: String, extraParams: Bundle?) {}
    open fun openDetail(detailType: String, extraParams: Bundle?, caller: Fragment?, requestCode: Int?) {}
    open fun openDialogDetail(detailType: String, extraParams: Bundle?) {}
    open fun openDialogDetail(detailType: String, extraParams: Bundle?, caller: Fragment?, requestCode: Int?) {}

    abstract fun sendFCMToken(token: String?)
}