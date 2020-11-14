@file:Suppress("DEPRECATION")

package it.giovanni.arkivio.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import it.giovanni.arkivio.fragments.MainFragment
import it.giovanni.arkivio.fragments.homepage.LinkAreaFragment
import it.giovanni.arkivio.fragments.homepage.HomePageFragment
import it.giovanni.arkivio.fragments.homepage.WorkingAreaFragment

class HomeFragmentAdapter(fragmentManager: FragmentManager, private val homePageCounter: Int, private val caller: MainFragment) : FragmentPagerAdapter(fragmentManager) {

    private var homePageFragment: HomePageFragment? = null
    private var workingAreaFragment: WorkingAreaFragment? = null
    private var linkAreaFragment: LinkAreaFragment? = null

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
                if (linkAreaFragment == null) linkAreaFragment = LinkAreaFragment.newInstance(caller)
                return linkAreaFragment!!
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