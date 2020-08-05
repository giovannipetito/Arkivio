package it.giovanni.arkivio.fragments.detail.logcat

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import it.giovanni.arkivio.R
import it.giovanni.arkivio.fragments.adapter.LogcatViewPager
import it.giovanni.arkivio.fragments.DetailFragment
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
        createTabs1()
        // createTabs2()
        // createTabs3()

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
    private fun createTabs1() {

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

    @SuppressLint("SimpleDateFormat")
    private fun createTabs2() {

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

    @SuppressLint("SimpleDateFormat")
    private fun createTabs3() {

        working_area_tabs.removeAllTabs()
        adapter = LogcatViewPager(childFragmentManager)

        val sdf = SimpleDateFormat("dd/MM/yyyy")

        val firstDayOfCurrentYear = Calendar.getInstance()
        firstDayOfCurrentYear.set(Calendar.DATE, 1)
        firstDayOfCurrentYear.set(Calendar.MONTH, 0)
        Log.i("YEARTAG", "df.format(firstDayOfCurrentYear.time): " + sdf.format(firstDayOfCurrentYear.time))

        val currentDay = Calendar.getInstance()
        Log.i("YEARTAG", "df.format(currentDay.time): " + sdf.format(currentDay.time))

        val firstDayOf1YearAgo = Calendar.getInstance()
        firstDayOf1YearAgo.add(Calendar.YEAR, -1)
        firstDayOf1YearAgo.set(Calendar.DATE, 1)
        firstDayOf1YearAgo.set(Calendar.MONTH, 0)
        Log.i("YEARTAG", "df.format(firstDayOf1YearAgo.time): " + sdf.format(firstDayOf1YearAgo.time))

        val lastDayOf1YearAgo = Calendar.getInstance()
        lastDayOf1YearAgo.add(Calendar.YEAR, -1)
        lastDayOf1YearAgo.set(Calendar.DATE, 31)
        lastDayOf1YearAgo.set(Calendar.MONTH, 11)
        Log.i("YEARTAG", "df.format(lastDayOf1YearAgo.time): " + sdf.format(lastDayOf1YearAgo.time))

        val firstDayOf2YearsAgo = Calendar.getInstance()
        firstDayOf2YearsAgo.add(Calendar.YEAR, -2)
        firstDayOf2YearsAgo.set(Calendar.DATE, 1)
        firstDayOf2YearsAgo.set(Calendar.MONTH, 0)
        Log.i("YEARTAG", "df.format(firstDayOf2YearsAgo.time): " + sdf.format(firstDayOf2YearsAgo.time))

        val lastDayOf2YearsAgo = Calendar.getInstance()
        lastDayOf2YearsAgo.add(Calendar.YEAR, -2)
        lastDayOf2YearsAgo.set(Calendar.DATE, 31)
        lastDayOf2YearsAgo.set(Calendar.MONTH, 11)
        Log.i("YEARTAG", "df.format(lastDayOf2YearsAgo.time): " + sdf.format(lastDayOf2YearsAgo.time))

        val firstDayOf3YearsAgo = Calendar.getInstance()
        firstDayOf3YearsAgo.add(Calendar.YEAR, -3)
        firstDayOf3YearsAgo.set(Calendar.DATE, 1)
        firstDayOf3YearsAgo.set(Calendar.MONTH, 0)
        Log.i("YEARTAG", "df.format(firstDayOf3YearsAgo.time): " + sdf.format(firstDayOf3YearsAgo.time))

        val lastDayOf3YearsAgo = Calendar.getInstance()
        lastDayOf3YearsAgo.add(Calendar.YEAR, -3)
        lastDayOf3YearsAgo.set(Calendar.DATE, 31)
        lastDayOf3YearsAgo.set(Calendar.MONTH, 11)
        Log.i("YEARTAG", "df.format(lastDayOf3YearsAgo.time): " + sdf.format(lastDayOf3YearsAgo.time))

        adapter!!.addFragment(
            sdf.format(firstDayOfCurrentYear.time),
            sdf.format(currentDay.time),
            resources.getString(R.string.current_year))

        adapter!!.addFragment(
            sdf.format(firstDayOf1YearAgo.time),
            sdf.format(lastDayOf1YearAgo.time),
            SimpleDateFormat("yyyy").format(firstDayOf1YearAgo.time))

        adapter!!.addFragment(
            sdf.format(firstDayOf2YearsAgo.time),
            sdf.format(lastDayOf2YearsAgo.time),
            SimpleDateFormat("yyyy").format(firstDayOf2YearsAgo.time))

        adapter!!.addFragment(
            sdf.format(firstDayOf3YearsAgo.time),
            sdf.format(lastDayOf3YearsAgo.time),
            SimpleDateFormat("yyyy").format(firstDayOf3YearsAgo.time))

        working_area_viewpager.adapter = adapter
        working_area_tabs.setupWithViewPager(working_area_viewpager)
        working_area_tabs.tabGravity = TabLayout.GRAVITY_CENTER
        working_area_tabs.tabMode = TabLayout.MODE_SCROLLABLE
        working_area_viewpager.offscreenPageLimit = 3
        working_area_viewpager.currentItem = 0
    }
}