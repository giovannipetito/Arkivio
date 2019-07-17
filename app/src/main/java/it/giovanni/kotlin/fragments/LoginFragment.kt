package it.giovanni.kotlin.fragments

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import it.giovanni.kotlin.R
import kotlinx.android.synthetic.main.login.*

class LoginFragment : BaseFragment(SectionType.LOGIN) {

    override fun getTitle(): Int {
        return NO_TITLE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentActivity.window.statusBarColor = ContextCompat.getColor(context!!, R.color.colorPrimary)

        login_button.setOnClickListener {
            showProgressDialog()
            currentActivity.openMainFragment()
        }
    }
}