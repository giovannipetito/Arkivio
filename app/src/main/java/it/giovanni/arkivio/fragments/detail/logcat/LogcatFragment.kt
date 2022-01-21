package it.giovanni.arkivio.fragments.detail.logcat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.LogcatLayoutBinding
import it.giovanni.arkivio.fragments.adapter.LogcatViewPager
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter
import java.text.SimpleDateFormat
import java.util.*

class LogcatFragment : DetailFragment() {

    private var layoutBinding: LogcatLayoutBinding? = null
    private val binding get() = layoutBinding

    var adapter: LogcatViewPager? = null
    private val millisecondsInWeek = 604800000L

    override fun getLayout(): Int {
        return NO_LAYOUT
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

    override fun onCreateBindingView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        layoutBinding = LogcatLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this, requireContext())
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        detailLayoutBinding?.topBar?.elevation = 0f
        detailLayoutBinding?.arrowGoBack?.setOnClickListener {
            if (detailLayoutBinding?.searchInputText?.visibility == View.VISIBLE) {
                hideSoftKeyboard()
                detailLayoutBinding?.searchInputText?.setText("")
                detailLayoutBinding?.searchInputText?.visibility = View.GONE
                detailLayoutBinding?.detailTitle?.visibility = View.VISIBLE
            } else
                currentActivity.onBackPressed()
        }
        createTabs1()
        // createTabs2()
        // createTabs3()

        Toast.makeText(context, "Position " + 0, Toast.LENGTH_SHORT).show()

        binding?.logcatViewpager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
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

    private fun createTabs1() {

        binding?.logcatTabs?.removeAllTabs()
        adapter = LogcatViewPager(childFragmentManager)

        val currentTime = Calendar.getInstance()

        val nextTime = Calendar.getInstance()
        nextTime.add(Calendar.MONTH, 3)
        nextTime.set(Calendar.DAY_OF_MONTH, nextTime.getActualMaximum(Calendar.DAY_OF_MONTH))

        val friday = Calendar.getInstance()
        friday.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY)

        val sunday = Calendar.getInstance()
        sunday.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)

        adapter?.addFragment(
            SimpleDateFormat("yyyy-MM-dd HH:00:00", Locale.ITALY).format(currentTime.timeInMillis),
            SimpleDateFormat("yyyy-MM-dd", Locale.ITALY).format(friday.time),
            resources.getString(R.string.current_week))

        adapter?.addFragment(
            SimpleDateFormat("yyyy-MM-dd 23:59:59", Locale.ITALY).format(sunday.time),
            SimpleDateFormat("yyyy-MM-dd", Locale.ITALY).format(friday.timeInMillis + millisecondsInWeek),
            resources.getString(R.string.next_week))

        adapter?.addFragment(
            SimpleDateFormat("yyyy-MM-dd 23:59:59", Locale.ITALY).format(sunday.timeInMillis + millisecondsInWeek),
            SimpleDateFormat("yyyy-MM-dd", Locale.ITALY).format(nextTime.timeInMillis),
            resources.getString(R.string.future_events))

        binding?.logcatViewpager?.adapter = adapter
        binding?.logcatTabs?.setupWithViewPager(binding?.logcatViewpager)
        binding?.logcatTabs?.tabGravity = TabLayout.GRAVITY_CENTER
        binding?.logcatTabs?.tabMode = TabLayout.MODE_SCROLLABLE
        binding?.logcatViewpager?.offscreenPageLimit = 3
        binding?.logcatViewpager?.currentItem = 0
    }

    private fun createTabs2() {

        binding?.logcatTabs?.removeAllTabs()
        adapter = LogcatViewPager(childFragmentManager)

        val currentTime = Calendar.getInstance()
        val nextTime = Calendar.getInstance()

        adapter?.addFragment(
            SimpleDateFormat("yyyy-MM-dd HH:00:00", Locale.ITALY).format(currentTime.timeInMillis),
            SimpleDateFormat("yyyy-MM-dd", Locale.ITALY).format(nextTime.timeInMillis + millisecondsInWeek),
            resources.getString(R.string.current_week))

        adapter?.addFragment(
            SimpleDateFormat("yyyy-MM-dd 00:00:00", Locale.ITALY).format(currentTime.timeInMillis + millisecondsInWeek),
            SimpleDateFormat("yyyy-MM-dd", Locale.ITALY).format(nextTime.timeInMillis + millisecondsInWeek + millisecondsInWeek),
            resources.getString(R.string.next_week))

        val thirdWeek = nextTime.timeInMillis + millisecondsInWeek + millisecondsInWeek
        currentTime.add(Calendar.MONTH, 3)
        nextTime.add(Calendar.MONTH, 3)
        nextTime.set(Calendar.DAY_OF_MONTH, nextTime.getActualMaximum(Calendar.DAY_OF_MONTH))

        adapter?.addFragment(
            SimpleDateFormat("yyyy-MM-dd 00:00:00", Locale.ITALY).format(thirdWeek),
            SimpleDateFormat("yyyy-MM-dd", Locale.ITALY).format(nextTime.timeInMillis),
            resources.getString(R.string.future_events))

        binding?.logcatViewpager?.adapter = adapter
        binding?.logcatTabs?.setupWithViewPager(binding?.logcatViewpager)
        binding?.logcatTabs?.tabGravity = TabLayout.GRAVITY_CENTER
        binding?.logcatTabs?.tabMode = TabLayout.MODE_SCROLLABLE
        binding?.logcatViewpager?.offscreenPageLimit = 3
        binding?.logcatViewpager?.currentItem = 0
    }

    private fun createTabs3() {

        binding?.logcatTabs?.removeAllTabs()
        adapter = LogcatViewPager(childFragmentManager)

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ITALY)

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

        adapter?.addFragment(
            sdf.format(firstDayOfCurrentYear.time),
            sdf.format(currentDay.time),
            resources.getString(R.string.current_year))

        adapter?.addFragment(
            sdf.format(firstDayOf1YearAgo.time),
            sdf.format(lastDayOf1YearAgo.time),
            SimpleDateFormat("yyyy", Locale.ITALY).format(firstDayOf1YearAgo.time))

        adapter?.addFragment(
            sdf.format(firstDayOf2YearsAgo.time),
            sdf.format(lastDayOf2YearsAgo.time),
            SimpleDateFormat("yyyy", Locale.ITALY).format(firstDayOf2YearsAgo.time))

        adapter?.addFragment(
            sdf.format(firstDayOf3YearsAgo.time),
            sdf.format(lastDayOf3YearsAgo.time),
            SimpleDateFormat("yyyy", Locale.ITALY).format(firstDayOf3YearsAgo.time))

        binding?.logcatViewpager?.adapter = adapter
        binding?.logcatTabs?.setupWithViewPager(binding?.logcatViewpager)
        binding?.logcatTabs?.tabGravity = TabLayout.GRAVITY_CENTER
        binding?.logcatTabs?.tabMode = TabLayout.MODE_SCROLLABLE
        binding?.logcatViewpager?.offscreenPageLimit = 3
        binding?.logcatViewpager?.currentItem = 0
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}