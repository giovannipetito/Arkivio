package it.giovanni.kotlin.fragments.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import it.giovanni.kotlin.fragments.detail.FragmentA

class LogcatAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm!!) {

    var fragmentA : FragmentA = FragmentA()
    var fragmentB : FragmentA = FragmentA()
    var fragmentC : FragmentA = FragmentA()

    // private val fragments = arrayOf("Array", "Articolo", "Ascensore", "Dipendente e progetto", "Dipendente, manager e azienda")
    private val fragments = arrayOf("Fragment A", "Fragment B", "Fragment C")

    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> return fragmentA
            1 -> return fragmentB
            2 -> return fragmentC
        }
        return fragmentA
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return fragments[position]
    }
}