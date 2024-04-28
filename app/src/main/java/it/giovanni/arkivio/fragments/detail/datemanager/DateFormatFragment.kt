package it.giovanni.arkivio.fragments.detail.datemanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.DateFormatLayoutBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter
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
import java.text.SimpleDateFormat
import java.util.*

class DateFormatFragment : DetailFragment() {

    private var layoutBinding: DateFormatLayoutBinding? = null
    private val binding get() = layoutBinding

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

    override fun onActionSearch(searchString: String) {
    }

    override fun isRefreshEnabled(): Boolean {
        return true
    }

    override fun refresh() {
        startCurrentDate = DateManager(Date())
        binding?.getdate1?.text = DateManager(startCurrentDate?.getFormatDate()!!).getDate().toString()
        stopSwipeRefresh()
    }

    override fun onCreateBindingView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        layoutBinding = DateFormatLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this)
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        calendar = Calendar.getInstance()

        startCurrentDate = DateManager(Date())
        endCurrentDate = DateManager(Date().time + (60 * 60 * 1000))

        binding?.currentHours?.text = currentHours.toString()
        binding?.currentMinutes?.text = currentMinutes.toString()

        binding?.getdate1?.text = DateManager(startCurrentDate?.getFormatDate()!!).getDate().toString()
        binding?.getdate2?.text = DateManager(startCurrentDate?.getFormatDate()!!).getDate().time.toString()

        binding?.getdate3?.text = Date().time.toString() // In Java: new Date().getTime()
        binding?.getdate4?.text = System.currentTimeMillis().toString()

        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ITALY)
        binding?.getdate5?.text = sdf.format(Date()) // epoch
        binding?.getdate6?.text = sdf.format(Date().time) // epoch

        binding?.currentStartTime?.text = startCurrentDate?.getFormatTime()
        binding?.currentEndTime?.text = endCurrentDate?.getFormatTime()

        binding?.currentDate1?.text = startCurrentDate?.getFormatDate1()
        binding?.currentDate2?.text = startCurrentDate?.getFormatDate2()
        binding?.currentDate3?.text = startCurrentDate?.getFormatDate3()

        binding?.currentStartDate?.text = startCurrentDate?.getFormatDate4()
        binding?.currentEndDate?.text = endCurrentDate?.getFormatDate4()

        binding?.customFormatDate?.text = startCurrentDate?.getCustomFormatDate("|| dd || MM || yyyy ||")

        val dataInizio = "06/02/1988 06:00:00"
        val dataFine = "06/02/1988 12:30:00"
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ITALY)
        startDate = DateManager(dateFormat.parse(dataInizio)!!)
        endDate = DateManager(dateFormat.parse(dataFine)!!)

        binding?.responseStartTime?.text = startDate?.getFormatTime()
        binding?.responseEndTime?.text = endDate?.getFormatTime()

        binding?.responseStartDate?.text = startDate?.getFormatDate4()
        binding?.responseEndDate?.text = endDate?.getFormatDate4()

        binding?.currentRangeTime?.text = getRangeTime(startCurrentDate?.getFormatDate()!!, endCurrentDate?.getFormatDate()!!)
        binding?.responseRangeTime?.text = getRangeTime(startDate?.getFormatDate()!!, endDate?.getFormatDate()!!)

        binding?.currentRangeDate1?.text = getRangeDate1(startCurrentDate?.getFormatDate()!!, endCurrentDate?.getFormatDate()!!)
        binding?.responseRangeDate1?.text = getRangeDate1(startDate?.getFormatDate()!!, endDate?.getFormatDate()!!)

        binding?.currentRangeDate2?.text = getRangeDate2(startCurrentDate?.getFormatDate()!!, endCurrentDate?.getFormatDate()!!)

        binding?.simpleDate1?.text = getSimpleDate1(startCurrentDate?.getFormatDate()!!)
        binding?.simpleDate2?.text = getSimpleDate2(startCurrentDate?.getFormatDate()!!)
        binding?.simpleDate3?.text = getSimpleDate3("06 feb 1988") // dd MMM yyyy
        binding?.simpleDate4?.text = getSimpleDate4(startCurrentDate?.getFormatDate()!!)

        binding?.simpleTime?.text = getSimpleTime(startCurrentDate?.getFormatDate()!!)
        binding?.simpleName?.text = getSimpleName(startCurrentDate?.getFormatDate()!!)
        binding?.simpleDay?.text = getSimpleDay(startCurrentDate?.getFormatDate()!!)
        binding?.simpleMonth?.text = getSimpleMonth1(startCurrentDate?.getFormatDate()!!)
        binding?.simpleYear?.text = getSimpleYear(startCurrentDate?.getFormatDate()!!)

        binding?.upperSimpleName1?.text = getUpperSimpleName1("1988/02/06")
        binding?.upperSimpleName2?.text = getUpperSimpleName2("06/02/1988")
        binding?.upperSimpleDate1?.text = getUpperSimpleDate1("1988/02/06")
        binding?.upperSimpleDate2?.text = getUpperSimpleDate2("06/02/1988")

        binding?.customFormatTime?.text = getCustomFormatTime("06:30")

        binding?.timeRange1?.text = getTimeRange1("1988/02/06", "1988/02/06", 8F)
        binding?.timeRange2?.text = getTimeRange1("1988/02/06", "1988/02/07", 8F)
        binding?.timeRange3?.text = getTimeRange1("1988/02/06", "1988/02/10", 8F)

        binding?.timeRange4?.text = getTimeRange2("1988/02/06", "1988/02/06")
        binding?.timeRange5?.text = getTimeRange2("1988/02/06", "1988/02/07")

        binding?.timeRange6?.text = getTimeRange3("1988/02/06", "1988/02/06")
        binding?.timeRange7?.text = getTimeRange3("1988/02/06", "1988/02/07")

        binding?.timeRange8?.text = getTimeRange4("1988/02/06", "1988/02/06", 0.5F)
        binding?.timeRange9?.text = getTimeRange4("1988/02/06", "1988/02/06", 1F)
        binding?.timeRange10?.text = getTimeRange4("1988/02/06", "1988/02/06", 8F)
        binding?.timeRange11?.text = getTimeRange4("1988/02/06", "1988/02/10", 8F)

        binding?.granularityDate1?.text = startCurrentDate?.getGranularityDate(granularityHour)
        binding?.granularityDate2?.text = startCurrentDate?.getGranularityDate(granularityMinute)

        timeDate = DateManager(dateFormat.parse(dataInizio)!!)
        timeDate?.setTimeDate(currentHours, currentMinutes)
        binding?.timeDate?.text = getSimpleDate4(timeDate?.getFormatDate()!!)

        binding?.scrollContainer?.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            detailLayoutBinding?.swipeRefreshLayout?.isEnabled = scrollY == 0
        }

        detailLayoutBinding?.swipeRefreshLayout?.setOnRefreshListener {
            refresh()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}