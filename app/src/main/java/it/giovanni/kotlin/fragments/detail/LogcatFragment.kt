package it.giovanni.kotlin.fragments.detail

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.viewpager.widget.ViewPager
import it.giovanni.kotlin.R
import it.giovanni.kotlin.adapters.LogcatAdapter
import it.giovanni.kotlin.fragments.DetailFragment
import kotlinx.android.synthetic.main.logcat_layout.*

class LogcatFragment : DetailFragment() {

    override fun getLayout(): Int {
        return R.layout.logcat_layout
    }

    override fun getTitle(): Int {
        return R.string.logcat_title
    }

    override fun searchAction(): Boolean {
        return false
    }

    override fun backAction(): Boolean {
        return true
    }

    override fun closeAction(): Boolean {
        return false
    }

    override fun deleteAction(): Boolean {
        return false
    }

    override fun editAction(): Boolean {
        return false
    }

    override fun editIconClick() {
    }

    override fun onActionSearch(search_string: String) {
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewpager.adapter = LogcatAdapter(fragmentManager)
        tablayout.setupWithViewPager(viewpager)

        val toast = Toast.makeText(context, "onPageSelected: " + tablayout.selectedTabPosition, Toast.LENGTH_SHORT)
        toast.show()

        viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                Toast.makeText(
                    context, "onPageSelected: " + tablayout.selectedTabPosition, Toast.LENGTH_SHORT).show()
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }
}