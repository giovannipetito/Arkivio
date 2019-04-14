package it.giovanni.kotlin.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

abstract class BaseActivity : AppCompatActivity() {

    open fun openDetail(detailType: String, extraParams: Bundle?) {}
    open fun openDetail(detailType: String, extraParams: Bundle?, caller: Fragment?, requestCode: Int?) {}
}