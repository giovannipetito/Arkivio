package it.giovanni.kotlin.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import it.giovanni.kotlin.fragments.MainFragment
import it.giovanni.kotlin.fragments.homepage.AdministrativeAreaFragment
import it.giovanni.kotlin.fragments.homepage.HomePageFragment
import it.giovanni.kotlin.fragments.homepage.WorkingAreaFragment

class HomeFragmentAdapter(fragmentManager: FragmentManager, private val homePageCounter: Int, private val caller: MainFragment) : FragmentPagerAdapter(fragmentManager) {

    private var homePageFragment: HomePageFragment? = null
    private var workingAreaFragment: WorkingAreaFragment? = null
    private var administrativeAreaFragment: AdministrativeAreaFragment? = null

    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> {
                if (homePageFragment == null) homePageFragment = HomePageFragment.newInstance(caller)
                return homePageFragment!!
            }
            1 -> {
                if (workingAreaFragment == null) workingAreaFragment = WorkingAreaFragment.newInstance(caller)
                return workingAreaFragment!!
            }
            2 -> {
                if (administrativeAreaFragment == null) administrativeAreaFragment = AdministrativeAreaFragment.newInstance(caller)
                return administrativeAreaFragment!!
            }
            else -> {
                if (homePageFragment == null) homePageFragment = HomePageFragment.newInstance(caller)
                return homePageFragment!!
            }
        }
    }

    override fun getCount(): Int {
        return homePageCounter
    }
}