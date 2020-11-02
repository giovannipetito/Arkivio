package it.giovanni.arkivio.fragments.detail.datemanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.children
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import it.giovanni.arkivio.App
import it.giovanni.arkivio.R
import it.giovanni.arkivio.bean.SelectedDay
import it.giovanni.arkivio.bean.SelectedDaysResponse
import it.giovanni.arkivio.customview.TextViewCustom
import it.giovanni.arkivio.customview.calendarview.getDaysOfWeek
import it.giovanni.arkivio.customview.calendarview.generateBadges
import it.giovanni.arkivio.customview.calendarview.model.Day
import it.giovanni.arkivio.customview.calendarview.model.DayOwner
import it.giovanni.arkivio.customview.calendarview.model.DaysOfWeek
import it.giovanni.arkivio.customview.calendarview.model.Month
import it.giovanni.arkivio.customview.calendarview.ui.Badge
import it.giovanni.arkivio.customview.calendarview.ui.DayBinder
import it.giovanni.arkivio.customview.calendarview.ui.MonthHeaderFooterBinder
import it.giovanni.arkivio.customview.calendarview.ui.ViewContainer
import it.giovanni.arkivio.databinding.CalendarviewVerticalHeaderBinding
import it.giovanni.arkivio.databinding.CalendarviewVerticalItemBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.utils.SharedPreferencesManager.Companion.loadSelectedDateFromPreferences
import it.giovanni.arkivio.utils.SharedPreferencesManager.Companion.saveSelectedDateToPreferences
import it.giovanni.arkivio.utils.Utils
import kotlinx.android.synthetic.main.calendarview_vertical_layout.*
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class CalendarViewVerticalFragment : DetailFragment() {

    private var viewFragment: View? = null
    private var selectedDate: LocalDate? = null
    private val selectedDates = mutableSetOf<LocalDate>()
    private val today = LocalDate.now()
    private var badges: Map<LocalDate, List<Badge>>? = null
    private var list: ArrayList<SelectedDay>? = null

    override fun getLayout(): Int {
        return R.layout.calendarview_vertical_layout
    }

    override fun getTitle(): Int {
        return R.string.calendarview_vertical_title
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewFragment = super.onCreateView(inflater, container, savedInstanceState)
        return viewFragment
    }

    @Suppress("DEPRECATION")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val daysOfWeek = getDaysOfWeek()

        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(1)
        val endMonth = currentMonth.plusMonths(3)

        calendarview.setup(startMonth, endMonth, daysOfWeek.first())
        calendarview.scrollToMonth(currentMonth)

        val response = loadSelectedDateFromPreferences()
        list = response?.selectedDays

        if (list != null && list?.isNotEmpty()!!)
            badges = generateBadges(list!!).groupBy { it.time.toLocalDate() }

        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: Day // Will be set when this container is bound.
            val binding = CalendarviewVerticalItemBinding.bind(view)
            init {
                view.setOnClickListener {
                    if (day.owner == DayOwner.THIS_MONTH) {
                        if (selectedDates.contains(day.date)) {
                            selectedDates.remove(day.date)
                        } else {
                            selectedDates.add(day.date)
                        }
                        calendarview.notifyDayChanged(day)
                    }
                }
            }
        }

        if (list == null || list?.isEmpty()!!)
            list = ArrayList()

        calendarview.dayBinder = object : DayBinder<DayViewContainer> {

            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: Day) {
                container.day = day

                val verticalItem = container.binding.verticalItem
                val horizontalLabel = container.binding.horizontalLabel
                val badge = container.binding.badge

                horizontalLabel.text = day.date.dayOfMonth.toString()

                if (day.owner == DayOwner.THIS_MONTH) {

                    if (badges != null) {
                        val mBadges = badges!![day.date]
                        if (mBadges != null) {
                            badge.visibility = View.VISIBLE
                            badge.setColorFilter(App.context.resources.getColor(mBadges[0].color))
                        }
                    }

                    when {
                        selectedDates.contains(day.date) -> {

                            // val date1 = DateTimeFormatter.ofPattern("d MMMM yyyy").format(day.date) // 06 febbraio 1988
                            // val date2 = DateTimeFormatter.ofPattern("yyyy-MM-d").format(day.date) // 1988-02-06
                            // Toast.makeText(context, "" + date2, Toast.LENGTH_SHORT).show()

                            val year = DateTimeFormatter.ofPattern("yyyy").format(day.date)
                            val month = DateTimeFormatter.ofPattern("MM").format(day.date)
                            val dayOfMonth = DateTimeFormatter.ofPattern("d").format(day.date)

                            if (badge.isInvisible) {
                                verticalItem.setBackgroundResource(R.drawable.calendarview_selected_item)
                                horizontalLabel.setTextColor(context?.resources?.getColor(R.color.verde_3)!!)

                                list?.add(SelectedDay(year, month, dayOfMonth))
                            }

                            if (badge.isVisible) {
                                badge.visibility = View.INVISIBLE
                                horizontalLabel.setTextColor(context?.resources?.getColor(R.color.rosso_1)!!)
                                verticalItem.setBackgroundResource(R.drawable.calendarview_deselected_item)
                                // horizontalLabel.setTextColor(context?.resources?.getColor(R.color.white)!!)
                                // verticalItem.background = null

                                for (item in list!!) {
                                    if (item.year == day.date.year.toString() &&
                                        item.month == day.date.monthValue.toString() &&
                                        item.dayOfMonth == day.date.dayOfMonth.toString()) {
                                        list?.remove(item)
                                        break
                                    }
                                }
                            }
                        }
                        today == day.date -> {
                            verticalItem.setBackgroundResource(R.drawable.calendarview_today_item)
                            horizontalLabel.setTextColor(context?.resources?.getColor(R.color.azzurro_6)!!)
                        }
                        else -> {
                            horizontalLabel.setTextColor(context?.resources?.getColor(R.color.white)!!)
                            verticalItem.background = null

                            for (item in list!!) {
                                if (item.year == day.date.year.toString() &&
                                    item.month == day.date.monthValue.toString() &&
                                    item.dayOfMonth == day.date.dayOfMonth.toString()) {
                                    list?.remove(item)
                                    break
                                }
                            }
                        }
                    }

                    if (6 == day.date.dayOfWeek.value || 7 == day.date.dayOfWeek.value) {
                        horizontalLabel.setTextColor(context?.resources?.getColor(R.color.rosso_1)!!)
                        verticalItem.isClickable = false
                        verticalItem.isFocusable = false
                        if (today == day.date)
                            verticalItem.setBackgroundResource(R.drawable.calendarview_deselected_item)
                        else verticalItem.background = null
                    }
                } else {
                    horizontalLabel.setTextColor(context?.resources?.getColor(R.color.grey_4)!!)
                    if (6 == day.date.dayOfWeek.value || 7 == day.date.dayOfWeek.value)
                        horizontalLabel.setTextColor(context?.resources?.getColor(R.color.rosso_transparent)!!)
                    verticalItem.background = null
                }
            }
        }

        class MonthViewContainer(view: View) : ViewContainer(view) {
            val header = CalendarviewVerticalHeaderBinding.bind(view).calendarviewVerticalHeader
            val legend = CalendarviewVerticalHeaderBinding.bind(view).calendarviewVerticalLegend
        }

        calendarview.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {

            override fun create(view: View) = MonthViewContainer(view)

            override fun bind(container: MonthViewContainer, month: Month) {

                val currentDate = DateTimeFormatter.ofPattern("MMMM").format(month.yearMonth.month) + " | " + month.year
                val headerDate = currentDate.toUpperCase(Locale.getDefault())
                container.header.text = headerDate
                // container.header.text = "${month.yearMonth.month.name.toUpperCase(Locale.getDefault()).capitalize(Locale.getDefault())} ${"|"} ${month.year}"

                container.legend.children.forEachIndexed { index, mView ->
                    (mView as TextViewCustom).apply {
                        if (daysOfWeek[index].name == DaysOfWeek.MONDAY.name) {
                            setText(R.string.monday)
                        }
                        if (daysOfWeek[index].name == DaysOfWeek.TUESDAY.name) {
                            setText(R.string.tuesday)
                        }
                        if (daysOfWeek[index].name == DaysOfWeek.WEDNESDAY.name) {
                            setText(R.string.wednesday)
                        }
                        if (daysOfWeek[index].name == DaysOfWeek.THURSDAY.name) {
                            setText(R.string.thursday)
                        }
                        if (daysOfWeek[index].name == DaysOfWeek.FRIDAY.name) {
                            setText(R.string.friday)
                        }
                        if (daysOfWeek[index].name == DaysOfWeek.SATURDAY.name) {
                            setText(R.string.saturday)
                            setTextColor(context?.resources!!.getColor(R.color.rosso_1))
                        }
                        if (daysOfWeek[index].name == DaysOfWeek.SUNDAY.name) {
                            setText(R.string.sunday)
                            setTextColor(context?.resources!!.getColor(R.color.rosso_1))
                        }
                    }
                }
            }
        }

        calendarview.monthScrollListener = { _ ->
            selectedDate?.let {
                // Clear selection if we scroll to a new month.
                selectedDate = null
                calendarview.notifyDateChanged(it)
            }
        }

        save_state_button.setOnClickListener {
            if (list != null) {
                val selectedDaysResponse = SelectedDaysResponse()
                selectedDaysResponse.selectedDays = list
                saveSelectedDateToPreferences(selectedDaysResponse)

                val listString: ArrayList<String>?= ArrayList()
                for (item in list!!) {
                    listString?.add(item.dayOfMonth + " " + item.month + " " + item.year)
                }
                val items = Utils.turnArrayListToString(listString!!)
                Toast.makeText(context, "" + items, Toast.LENGTH_LONG).show()
            }
        }
    }
}