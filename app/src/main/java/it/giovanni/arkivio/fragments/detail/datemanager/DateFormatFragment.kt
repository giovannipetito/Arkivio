package it.giovanni.arkivio.fragments.detail.datemanager

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.giovanni.arkivio.R
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.utils.DateManager
import it.giovanni.arkivio.utils.DateManager.Companion.getCustomFormatTime
import it.giovanni.arkivio.utils.DateManager.Companion.getRangeDate1
import it.giovanni.arkivio.utils.DateManager.Companion.getRangeDate2
import it.giovanni.arkivio.utils.DateManager.Companion.getRangeTime
import it.giovanni.arkivio.utils.DateManager.Companion.getSimpleDate1
import it.giovanni.arkivio.utils.DateManager.Companion.getSimpleDate2
import it.giovanni.arkivio.utils.DateManager.Companion.getSimpleDate3
import it.giovanni.arkivio.utils.DateManager.Companion.getSimpleDate4
import it.giovanni.arkivio.utils.DateManager.Companion.getSimpleDay
import it.giovanni.arkivio.utils.DateManager.Companion.getSimpleMonth1
import it.giovanni.arkivio.utils.DateManager.Companion.getSimpleName
import it.giovanni.arkivio.utils.DateManager.Companion.getSimpleTime
import it.giovanni.arkivio.utils.DateManager.Companion.getSimpleYear
import it.giovanni.arkivio.utils.DateManager.Companion.getTimeRange1
import it.giovanni.arkivio.utils.DateManager.Companion.getTimeRange2
import it.giovanni.arkivio.utils.DateManager.Companion.getTimeRange3
import it.giovanni.arkivio.utils.DateManager.Companion.getTimeRange4
import it.giovanni.arkivio.utils.DateManager.Companion.getUpperSimpleDate1
import it.giovanni.arkivio.utils.DateManager.Companion.getUpperSimpleDate2
import it.giovanni.arkivio.utils.DateManager.Companion.getUpperSimpleName1
import it.giovanni.arkivio.utils.DateManager.Companion.getUpperSimpleName2
import kotlinx.android.synthetic.main.date_format_layout.*
import kotlinx.android.synthetic.main.detail_layout.*
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class DateFormatFragment : DetailFragment() {

    private var viewFragment: View? = null

    private var calendar : Calendar? = null

    private val currentHours = Date().hours
    private val currentMinutes = Date().minutes

    private var startCurrentDate: DateManager? = null
    private var endCurrentDate: DateManager? = null
    private var startDate: DateManager? = null
    private var endDate: DateManager? = null
    private var timeDate: DateManager? = null

    private var granularityHour: Int = 1
    private var granularityMinute: Int = 2

    override fun getLayout(): Int {
        return R.layout.date_format_layout
    }

    override fun getTitle(): Int {
        return R.string.date_format_title
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

    override fun isRefreshEnabled(): Boolean {
        return true
    }

    override fun refresh() {
        startCurrentDate = DateManager(Date())
        getdate_1?.text = DateManager(startCurrentDate!!.getFormatDate()).getDate().toString()
        stopSwipeRefresh()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewFragment = super.onCreateView(inflater, container, savedInstanceState)
        return viewFragment
    }

    override fun onCreateBindingView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?, ): View? {
        TODO("Not yet implemented")
    }

    @SuppressLint("SimpleDateFormat")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        calendar = Calendar.getInstance()

        startCurrentDate = DateManager(Date())
        endCurrentDate = DateManager(Date().time + (60 * 60 * 1000))

        current_hours?.text = currentHours.toString()
        current_minutes?.text = currentMinutes.toString()

        getdate_1?.text = DateManager(startCurrentDate!!.getFormatDate()).getDate().toString()
        getdate_2?.text = DateManager(startCurrentDate!!.getFormatDate()).getDate().time.toString()

        getdate_3.text = Date().time.toString() // In Java: new Date().getTime()
        getdate_4.text = System.currentTimeMillis().toString()
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        getdate_5.text = sdf.format(Date()) // epoch
        getdate_6.text = sdf.format(Date().time) // epoch

        current_start_time?.text = startCurrentDate?.getFormatTime()
        current_end_time?.text = endCurrentDate?.getFormatTime()

        current_date_1?.text = startCurrentDate?.getFormatDate1()
        current_date_2?.text = startCurrentDate?.getFormatDate2()
        current_date_3?.text = startCurrentDate?.getFormatDate3()

        current_start_date?.text = startCurrentDate?.getFormatDate4()
        current_end_date?.text = endCurrentDate?.getFormatDate4()

        custom_format_date?.text = startCurrentDate?.getCustomFormatDate("|| dd || MM || yyyy ||")

        val dataInizio = "06/02/1988 06:00:00"
        val dataFine = "06/02/1988 12:30:00"
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        startDate = DateManager(dateFormat.parse(dataInizio)!!)
        endDate = DateManager(dateFormat.parse(dataFine)!!)

        response_start_time?.text = startDate?.getFormatTime()
        response_end_time?.text = endDate?.getFormatTime()

        response_start_date?.text = startDate?.getFormatDate4()
        response_end_date?.text = endDate?.getFormatDate4()

        current_range_time?.text = getRangeTime(startCurrentDate!!.getFormatDate(), endCurrentDate!!.getFormatDate())
        response_range_time?.text = getRangeTime(startDate!!.getFormatDate(), endDate!!.getFormatDate())

        current_range_date_1?.text = getRangeDate1(startCurrentDate!!.getFormatDate(), endCurrentDate!!.getFormatDate())
        response_range_date_1?.text = getRangeDate1(startDate!!.getFormatDate(), endDate!!.getFormatDate())

        current_range_date_2?.text = getRangeDate2(startCurrentDate!!.getFormatDate(), endCurrentDate!!.getFormatDate())

        simple_date_1?.text = getSimpleDate1(startCurrentDate!!.getFormatDate())
        simple_date_2?.text = getSimpleDate2(startCurrentDate!!.getFormatDate())
        simple_date_3?.text = getSimpleDate3("06 feb 1988") // dd MMM yyyy
        simple_date_4?.text = getSimpleDate4(startCurrentDate!!.getFormatDate())

        simple_time?.text = getSimpleTime(startCurrentDate!!.getFormatDate())
        simple_name?.text = getSimpleName(startCurrentDate!!.getFormatDate())
        simple_day?.text = getSimpleDay(startCurrentDate!!.getFormatDate())
        simple_month?.text = getSimpleMonth1(startCurrentDate!!.getFormatDate())
        simple_year?.text = getSimpleYear(startCurrentDate!!.getFormatDate())

        upper_simple_name_1?.text = getUpperSimpleName1("1988/02/06")
        upper_simple_name_2?.text = getUpperSimpleName2("06/02/1988")
        upper_simple_date_1?.text = getUpperSimpleDate1("1988/02/06")
        upper_simple_date_2.text = getUpperSimpleDate2("06/02/1988")

        custom_format_time?.text = getCustomFormatTime("06:30")

        time_range_1?.text = getTimeRange1("1988/02/06", "1988/02/06", 8F)
        time_range_2?.text = getTimeRange1("1988/02/06", "1988/02/07", 8F)
        time_range_3?.text = getTimeRange1("1988/02/06", "1988/02/10", 8F)

        time_range_4?.text = getTimeRange2("1988/02/06", "1988/02/06")
        time_range_5?.text = getTimeRange2("1988/02/06", "1988/02/07")

        time_range_6?.text = getTimeRange3("1988/02/06", "1988/02/06")
        time_range_7?.text = getTimeRange3("1988/02/06", "1988/02/07")

        time_range_8?.text = getTimeRange4("1988/02/06", "1988/02/06", 0.5F)
        time_range_9?.text = getTimeRange4("1988/02/06", "1988/02/06", 1F)
        time_range_10?.text = getTimeRange4("1988/02/06", "1988/02/06", 8F)
        time_range_11?.text = getTimeRange4("1988/02/06", "1988/02/10", 8F)

        granularity_date_1?.text = startCurrentDate?.getGranularityDate(granularityHour)
        granularity_date_2?.text = startCurrentDate?.getGranularityDate(granularityMinute)

        timeDate = DateManager(dateFormat.parse(dataInizio)!!)
        timeDate?.setTimeDate(currentHours, currentMinutes)
        time_date?.text = getSimpleDate4(timeDate!!.getFormatDate())

        scroll_container.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            swipeRefreshLayout!!.isEnabled = scrollY == 0
        }

        swipeRefreshLayout!!.setOnRefreshListener {
            refresh()
        }
    }
}