package it.giovanni.kotlin.fragments.homepage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.giovanni.kotlin.fragments.HomeFragment
import it.giovanni.kotlin.fragments.MainFragment
import it.giovanni.kotlin.R

class AdministrativeAreaFragment : HomeFragment() {

    override fun getLayout(): Int {
        return R.layout.administrative_area_layout
    }

    override fun getTitle(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        var caller: MainFragment? = null
        fun newInstance(c: MainFragment): AdministrativeAreaFragment {
            caller = c
            return AdministrativeAreaFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onFragmentReady() {
        super.onFragmentReady()
    }
}