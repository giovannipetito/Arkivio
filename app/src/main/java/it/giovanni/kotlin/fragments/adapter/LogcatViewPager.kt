package it.giovanni.kotlin.fragments.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import it.giovanni.kotlin.fragments.detail.FragmentA

@Suppress("DEPRECATION")
class LogcatViewPager(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    private var fragmentList: ArrayList<FragmentA> = ArrayList()
    private var fragmentTitleList: ArrayList<String> = ArrayList()

    override fun getPageTitle(position: Int): CharSequence {
        return fragmentTitleList[position]
    }

    fun addFragment(fromDate: String, toDate: String, title: String) {
        println(fromDate)

        val watd = FragmentA()
        val bundle = Bundle()
        bundle.putString(FragmentA.DATE_FROM, fromDate)
        bundle.putString(FragmentA.DATE_TO, toDate)
        bundle.putInt(FragmentA.POSITION, fragmentList.size)
        watd.arguments = bundle
        fragmentList.add(watd)
        fragmentTitleList.add(title)
    }

    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getCount(): Int {
        return fragmentList.size
    }
}