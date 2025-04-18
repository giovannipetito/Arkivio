package it.giovanni.arkivio.fragments.detail.datemanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.children
import it.giovanni.arkivio.R
import it.giovanni.arkivio.customview.TextViewCustom
import it.giovanni.arkivio.customview.calendarview.getDaysOfWeek
import it.giovanni.arkivio.customview.calendarview.model.*
import it.giovanni.arkivio.customview.calendarview.ui.DayBinder
import it.giovanni.arkivio.customview.calendarview.ui.MonthHeaderFooterBinder
import it.giovanni.arkivio.customview.calendarview.ui.ViewContainer
import it.giovanni.arkivio.databinding.CalendarViewHorizontalHeaderBinding
import it.giovanni.arkivio.databinding.CalendarViewHorizontalItemBinding
import it.giovanni.arkivio.databinding.CalendarViewHorizontalLayoutBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter
import it.giovanni.arkivio.utils.SharedPreferencesManager
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class CalendarViewHorizontalFragment : DetailFragment() {

    private var layoutBinding: CalendarViewHorizontalLayoutBinding? = null
    private val binding get() = layoutBinding

    private var selectedDate: LocalDate? = null
    private val today = LocalDate.now()

    override fun getTitle(): Int {
        return R.string.calendar_view_horizontal_title
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

    override fun onCreateBindingView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        layoutBinding = CalendarViewHorizontalLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this)
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isDarkMode = SharedPreferencesManager.loadDarkModeStateFromPreferences()

        val daysOfWeek = getDaysOfWeek()

        binding?.calendarViewHorizontalLegend?.children?.forEachIndexed { index, mView ->
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
                    setTextColor(ContextCompat.getColor(context, R.color.rosso))
                }
                if (daysOfWeek[index].name == DaysOfWeek.SUNDAY.name) {
                    setText(R.string.sunday)
                    setTextColor(ContextCompat.getColor(context, R.color.rosso))
                }
            }
        }

        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(2)
        val endMonth = currentMonth.plusMonths(2)

        binding?.calendarViewHorizontal?.setup(startMonth, endMonth, daysOfWeek.first())
        binding?.calendarViewHorizontal?.scrollToMonth(currentMonth)

        class DayViewContainer(view: View) : ViewContainer(view) {

            // Will be set when this container is bound. See the dayBinder.
            lateinit var day: CalendarDay
            val horizontalLabel = CalendarViewHorizontalItemBinding.bind(view).horizontalLabel

            init {
                horizontalLabel.setOnClickListener {
                    if (day.owner == DayOwner.THIS_MONTH) {
                        if (selectedDate != day.date) {
                            val oldDate = selectedDate
                            selectedDate = day.date
                            binding?.calendarViewHorizontal?.notifyDateChanged(day.date)
                            oldDate?.let { binding?.calendarViewHorizontal?.notifyDateChanged(oldDate) }
                        } else {
                            selectedDate = null
                            binding?.calendarViewHorizontal?.notifyDayChanged(day)
                        }
                    }
                }
            }
        }

        binding?.calendarViewHorizontal?.dayBinder = object : DayBinder<DayViewContainer> {

            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.day = day
                val horizontalLabel = container.horizontalLabel
                horizontalLabel.text = day.date.dayOfMonth.toString()

                if (day.owner == DayOwner.THIS_MONTH) {
                    when (day.date) {
                        selectedDate -> {
                            horizontalLabel.setTextColor(ContextCompat.getColor(context!!, R.color.verde))
                            horizontalLabel.setBackgroundResource(R.drawable.calendarview_selected_item)

                            val text = DateTimeFormatter.ofPattern("d MMMM yyyy").format(selectedDate)
                            Toast.makeText(context, "" + text, Toast.LENGTH_SHORT).show()
                        }
                        today -> {
                            horizontalLabel.setTextColor(ContextCompat.getColor(context!!, R.color.rosso))
                            horizontalLabel.background = null
                        }
                        else -> {
                            if (isDarkMode)
                                horizontalLabel.setTextColor(ContextCompat.getColor(context!!, R.color.white))
                            else
                                horizontalLabel.setTextColor(ContextCompat.getColor(context!!, R.color.dark))

                            horizontalLabel.background = null
                        }
                    }
                } else {
                    horizontalLabel.setTextColor(ContextCompat.getColor(context!!, R.color.grey_2))
                    horizontalLabel.background = null
                }
            }
        }

        class MonthViewContainer(view: View) : ViewContainer(view) {
            val calendarViewHorizontalHeader = CalendarViewHorizontalHeaderBinding.bind(view).calendarViewHorizontalHeader
        }

        binding?.calendarViewHorizontal?.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)
            override fun bind(container: MonthViewContainer, month: CalendarMonth) {

                var monthName = month.yearMonth.month.name.lowercase()
                monthName = monthName.substring(0, 1).uppercase() + monthName.substring(1)
                val date = "$monthName ${month.year}"
                container.calendarViewHorizontalHeader.text = date

                if (isDarkMode)
                    container.calendarViewHorizontalHeader.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
                else
                    container.calendarViewHorizontalHeader.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}