package it.giovanni.kotlin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.login.*

class LoginFragment : BaseFragment(SectionType.LOGIN) {

    override fun getTitle(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        return view
    }

    override fun onFragmentReady() {
        super.onFragmentReady()

        login_button.setOnClickListener {
            showProgressDialog()
            currentActivity.openHomeFragment()
        }
    }
}