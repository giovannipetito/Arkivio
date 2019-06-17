package it.giovanni.kotlin.fragments.detail

import it.giovanni.kotlin.R
import it.giovanni.kotlin.fragments.BaseFragment

class FragmentA : BaseFragment(SectionType.TAB_DETAIL) {

    override fun getTitle(): Int {
        return R.string.logcat_projects_title
    }
}