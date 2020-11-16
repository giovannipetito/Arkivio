package it.giovanni.arkivio.fragments.detail.datemanager

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.children
import it.giovanni.arkivio.R
import it.giovanni.arkivio.customview.TextViewCustom
import it.giovanni.arkivio.customview.calendarview.getDaysOfWeek
import it.giovanni.arkivio.customview.calendarview.model.*
import it.giovanni.arkivio.customview.calendarview.ui.DayBinder
import it.giovanni.arkivio.customview.calendarview.ui.MonthHeaderFooterBinder
import it.giovanni.arkivio.customview.calendarview.ui.ViewContainer
import it.giovanni.arkivio.databinding.CalendarviewHorizontalHeaderBinding
import it.giovanni.arkivio.databinding.CalendarviewHorizontalItemBinding
import it.giovanni.arkivio.fragments.DetailFragment
import kotlinx.android.synthetic.main.calendarview_horizontal_layout.*
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.*

class CalendarViewHorizontalFragment : DetailFragment() {

    private var viewFragment: View? = null
    private var selectedDate: LocalDate? = null
    private val today = LocalDate.now()

    override fun getLayout(): Int {
        return R.layout.calendarview_horizontal_layout
    }

    override fun getTitle(): Int {
        return R.string.calendarview_horizontal_title
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

    override fun onCreateBindingView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?, ): View? {
        TODO("Not yet implemented")
    }

    @Suppress("DEPRECATION")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val daysOfWeek = getDaysOfWeek()

        calendarview_horizontal_legend?.children?.forEachIndexed { index, mView ->
            (mView as TextViewCustom).apply {
                // text = daysOfWeek[index].name.first().toString()
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

        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(2)
        val endMonth = currentMonth.plusMonths(2)

        calendarview_horizontal.setup(startMonth, endMonth, daysOfWeek.first())
        calendarview_horizontal.scrollToMonth(currentMonth)

        class DayViewContainer(view: View) : ViewContainer(view) {

            // Will be set when this container is bound. See the dayBinder.
            lateinit var day: CalendarDay
            val horizontalLabel = CalendarviewHorizontalItemBinding.bind(view).horizontalLabel

            init {
                horizontalLabel.setOnClickListener {
                    if (day.owner == DayOwner.THIS_MONTH) {
                        if (selectedDate != day.date) {
                            val oldDate = selectedDate
                            selectedDate = day.date
                            calendarview_horizontal.notifyDateChanged(day.date)
                            oldDate?.let { calendarview_horizontal.notifyDateChanged(oldDate) }
                        } else {
                            selectedDate = null
                            calendarview_horizontal.notifyDayChanged(day)
                        }
                    }
                }
            }
        }

        calendarview_horizontal.dayBinder = object : DayBinder<DayViewContainer> {

            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.day = day
                val horizontalLabel = container.horizontalLabel
                horizontalLabel.text = day.date.dayOfMonth.toString()

                if (day.owner == DayOwner.THIS_MONTH) {
                    when (day.date) {
                        selectedDate -> {
                            horizontalLabel.setTextColor(context?.resources?.getColor(R.color.verde_3)!!)
                            horizontalLabel.setBackgroundResource(R.drawable.calendarview_selected_item)

                            val text = DateTimeFormatter.ofPattern("d MMMM yyyy").format(selectedDate)
                            Toast.makeText(context, "" + text, Toast.LENGTH_SHORT).show()
                        }
                        today -> {
                            horizontalLabel.setTextColor(context?.resources?.getColor(R.color.rosso_1)!!)
                            horizontalLabel.background = null
                        }
                        else -> {
                            horizontalLabel.setTextColor(context?.resources?.getColor(R.color.black)!!)
                            horizontalLabel.background = null
                        }
                    }
                } else {
                    horizontalLabel.setTextColor(context?.resources?.getColor(R.color.grey_2)!!)
                    horizontalLabel.background = null
                    // horizontalLabel.visibility = View.INVISIBLE
                }
            }
        }

        class MonthViewContainer(view: View) : ViewContainer(view) {
            val textView = CalendarviewHorizontalHeaderBinding.bind(view).calendarviewHorizontalHeader
        }

        calendarview_horizontal.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)
            override fun bind(container: MonthViewContainer, month: CalendarMonth) {
                @SuppressLint("SetTextI18n")
                container.textView.text = "${month.yearMonth.month.name.toLowerCase(Locale.getDefault()).capitalize(Locale.getDefault())} ${month.year}"
            }
        }
    }
}