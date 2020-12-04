@file:Suppress("DEPRECATION")

package it.giovanni.arkivio.fragments.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import it.giovanni.arkivio.fragments.detail.logcat.ContentFragment

class LogcatViewPager(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    private var fragmentList: ArrayList<ContentFragment> = ArrayList()
    private var fragmentTitleList: ArrayList<String> = ArrayList()

    override fun getPageTitle(position: Int): CharSequence {
        return fragmentTitleList[position]
    }

    fun addFragment(fromDate: String, toDate: String, title: String) {
        println(fromDate)

        val fragment = ContentFragment()
        val bundle = Bundle()
        bundle.putString(ContentFragment.DATE_FROM, fromDate)
        bundle.putString(ContentFragment.DATE_TO, toDate)
        bundle.putInt(ContentFragment.POSITION, fragmentList.size)
        fragment.arguments = bundle
        fragmentList.add(fragment)
        fragmentTitleList.add(title)
    }

    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getCount(): Int {
        return fragmentList.size
    }
}