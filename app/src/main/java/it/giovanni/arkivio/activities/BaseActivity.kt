package it.giovanni.arkivio.activities

import android.os.Bundle
import androidx.fragment.app.Fragment

abstract class BaseActivity : BaseView() {

    companion object {
        var TAG: String = BaseActivity::class.java.simpleName
    }

    open fun openDetail(detailType: String, extraParams: Bundle?) {}
    open fun openDetail(detailType: String, extraParams: Bundle?, caller: Fragment?, requestCode: Int?) {}
    open fun openDialogDetail(detailType: String, extraParams: Bundle?) {}
    open fun openDialogDetail(detailType: String, extraParams: Bundle?, caller: Fragment?, requestCode: Int?) {}
}