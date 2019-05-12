package it.giovanni.kotlin.fragments.homepage

import it.giovanni.kotlin.fragments.HomeFragment
import it.giovanni.kotlin.fragments.MainFragment
import it.giovanni.kotlin.R

class AdministrativeAreaFragment : HomeFragment() {

    override fun getLayout(): Int {
        return R.layout.administrative_area_layout
    }

    override fun getTitle(): Int {
        return NO_TITLE
    }

    companion object {
        private var caller: MainFragment? = null
        fun newInstance(c: MainFragment): AdministrativeAreaFragment {
            caller = c
            return AdministrativeAreaFragment()
        }
    }
}