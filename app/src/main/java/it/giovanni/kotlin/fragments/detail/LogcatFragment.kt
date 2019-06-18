package it.giovanni.kotlin.fragments.detail

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import it.giovanni.kotlin.R
import it.giovanni.kotlin.fragments.adapter.LogcatViewPager
import it.giovanni.kotlin.fragments.DetailFragment
import kotlinx.android.synthetic.main.detail_layout.*
import kotlinx.android.synthetic.main.logcat_layout.*
import java.text.SimpleDateFormat
import java.util.*

class LogcatFragment : DetailFragment() {

    var adapter: LogcatViewPager? = null
    private val MILLISECONDSINWEEK = 604800000

    override fun getLayout(): Int {
        return R.layout.logcat_layout
    }

    override fun getTitle(): Int {
        return R.string.logcat_projects_title
    }

    override fun getActionTitle(): Int {
        return NO_TITLE
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

        top_bar.elevation = 0f
        arrow_go_back.setOnClickListener {
            if (search_input_text.visibility == View.VISIBLE) {
                hideSoftKeyboard()
                search_input_text.setText("")
                search_input_text.visibility = View.GONE
                detail_title.visibility = View.VISIBLE
            } else
                currentActivity.onBackPressed()
        }
        createTabs()

        Toast.makeText(context, "Position " + 0, Toast.LENGTH_SHORT).show()

        working_area_viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> Toast.makeText(context, "Position $position", Toast.LENGTH_SHORT).show()
                    1 -> Toast.makeText(context, "Position $position", Toast.LENGTH_SHORT).show()
                    2 -> Toast.makeText(context, "Position $position", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    @SuppressLint("SimpleDateFormat")
    private fun createTabs() {

        working_area_tabs.removeAllTabs()
        adapter = LogcatViewPager(childFragmentManager)

        val currentTime = Calendar.getInstance()

        val nextTime = Calendar.getInstance()
        nextTime.add(Calendar.MONTH, 3)
        nextTime.set(Calendar.DAY_OF_MONTH, nextTime.getActualMaximum(Calendar.DAY_OF_MONTH))

        val friday = Calendar.getInstance()
        friday.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY)

        val sunday = Calendar.getInstance()
        sunday.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)

        adapter!!.addFragment(
            SimpleDateFormat("yyyy-MM-dd HH:00:00").format(currentTime.timeInMillis),
            SimpleDateFormat("yyyy-MM-dd").format(friday.time),
            resources.getString(R.string.current_week))

        adapter!!.addFragment(
            SimpleDateFormat("yyyy-MM-dd 23:59:59").format(sunday.time),
            SimpleDateFormat("yyyy-MM-dd").format(friday.timeInMillis + MILLISECONDSINWEEK),
            resources.getString(R.string.next_week))

        adapter!!.addFragment(
            SimpleDateFormat("yyyy-MM-dd 23:59:59").format(sunday.timeInMillis + MILLISECONDSINWEEK),
            SimpleDateFormat("yyyy-MM-dd").format(nextTime.timeInMillis),
            resources.getString(R.string.future_events))

        working_area_viewpager.adapter = adapter
        working_area_tabs.setupWithViewPager(working_area_viewpager)
        working_area_tabs.tabGravity = TabLayout.GRAVITY_CENTER
        working_area_tabs.tabMode = TabLayout.MODE_SCROLLABLE
        working_area_viewpager.offscreenPageLimit = 3
        working_area_viewpager.currentItem = 0
    }

    /*
    @SuppressLint("SimpleDateFormat")
    private fun createTabs() {

        working_area_tabs.removeAllTabs()
        adapter = LogcatViewPager(childFragmentManager)

        val currentTime = Calendar.getInstance()
        val nextTime = Calendar.getInstance()

        adapter!!.addFragment(
            SimpleDateFormat("yyyy-MM-dd HH:00:00").format(currentTime.timeInMillis),
            SimpleDateFormat("yyyy-MM-dd").format(nextTime.timeInMillis + MILLISECONDSINWEEK),
            resources.getString(R.string.current_week))

        adapter!!.addFragment(
            SimpleDateFormat("yyyy-MM-dd 00:00:00").format(currentTime.timeInMillis + MILLISECONDSINWEEK),
            SimpleDateFormat("yyyy-MM-dd").format(nextTime.timeInMillis + MILLISECONDSINWEEK + MILLISECONDSINWEEK),
            resources.getString(R.string.next_week))

        val thirdWeek = nextTime.timeInMillis + MILLISECONDSINWEEK + MILLISECONDSINWEEK
        currentTime.add(Calendar.MONTH, 3)
        nextTime.add(Calendar.MONTH, 3)
        nextTime.set(Calendar.DAY_OF_MONTH, nextTime.getActualMaximum(Calendar.DAY_OF_MONTH))

        adapter!!.addFragment(
            SimpleDateFormat("yyyy-MM-dd 00:00:00").format(thirdWeek),
            SimpleDateFormat("yyyy-MM-dd").format(nextTime.timeInMillis),
            resources.getString(R.string.future_events))

        working_area_viewpager.adapter = adapter
        working_area_tabs.setupWithViewPager(working_area_viewpager)
        working_area_tabs.tabGravity = TabLayout.GRAVITY_CENTER
        working_area_tabs.tabMode = TabLayout.MODE_SCROLLABLE
        working_area_viewpager.offscreenPageLimit = 3
        working_area_viewpager.currentItem = 0
    }
    */
}