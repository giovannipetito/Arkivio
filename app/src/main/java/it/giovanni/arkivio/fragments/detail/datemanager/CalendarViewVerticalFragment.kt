package it.giovanni.arkivio.fragments.detail.datemanager

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.airbnb.paris.extensions.style
import it.giovanni.arkivio.R
import it.giovanni.arkivio.bean.SelectedDay
import it.giovanni.arkivio.bean.SelectedDaysResponse
import it.giovanni.arkivio.customview.TextViewCustom
import it.giovanni.arkivio.customview.calendarview.getDaysOfWeek
import it.giovanni.arkivio.customview.calendarview.generateBadges
import it.giovanni.arkivio.customview.calendarview.model.CalendarDay
import it.giovanni.arkivio.customview.calendarview.model.DayOwner
import it.giovanni.arkivio.customview.calendarview.model.DaysOfWeek
import it.giovanni.arkivio.customview.calendarview.model.CalendarMonth
import it.giovanni.arkivio.customview.calendarview.ui.Badge
import it.giovanni.arkivio.customview.calendarview.ui.DayBinder
import it.giovanni.arkivio.customview.calendarview.ui.MonthHeaderFooterBinder
import it.giovanni.arkivio.customview.calendarview.ui.ViewContainer
import it.giovanni.arkivio.databinding.CalendarviewVerticalHeaderBinding
import it.giovanni.arkivio.databinding.CalendarviewVerticalItemBinding
import it.giovanni.arkivio.databinding.CalendarviewVerticalLayoutBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter
import it.giovanni.arkivio.utils.SharedPreferencesManager
import it.giovanni.arkivio.utils.SharedPreferencesManager.Companion.loadSelectedDaysFromPreferences
import it.giovanni.arkivio.utils.SharedPreferencesManager.Companion.saveSelectedDaysToPreferences
import it.giovanni.arkivio.utils.Utils
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import kotlin.collections.ArrayList

class CalendarViewVerticalFragment : DetailFragment() {

    companion object {
        private var TAG: String = CalendarViewVerticalFragment::class.java.simpleName
    }

    private var layoutBinding: CalendarviewVerticalLayoutBinding? = null
    private val binding get() = layoutBinding

    private var selectedDate: LocalDate? = null
    private val selectedDates = mutableSetOf<LocalDate>()
    private val today = LocalDate.now()
    private var badges: Map<LocalDate, List<Badge>>? = null
    private var items: ArrayList<SelectedDay>? = null
    private var selectedItems: ArrayList<SelectedDay>? = null
    private var deselectedItems: ArrayList<SelectedDay>? = null

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

    override fun onCreateBindingView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        layoutBinding = CalendarviewVerticalLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this, requireContext())
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewStyle()

        val daysOfWeek = getDaysOfWeek()

        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(1)
        val endMonth = currentMonth.plusMonths(3)

        binding?.calendarview?.setup(startMonth, endMonth, daysOfWeek.first())
        binding?.calendarview?.scrollToMonth(currentMonth)

        val response = loadSelectedDaysFromPreferences()
        items = response?.selectedDays

        if (items != null && items?.isNotEmpty()!!)
            badges = generateBadges(items!!).groupBy { it.time.toLocalDate() }

        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay // Will be set when this container is bound.
            val itemBinding = CalendarviewVerticalItemBinding.bind(view)
            init {
                view.setOnClickListener {
                    if (day.owner == DayOwner.THIS_MONTH) {
                        if (selectedDates.contains(day.date)) {
                            selectedDates.remove(day.date)
                        } else {
                            selectedDates.add(day.date)
                        }
                        binding?.calendarview?.notifyDayChanged(day)

                        binding?.calendarviewButton?.isEnabled = selectedDates.isNotEmpty()
                    }
                }
            }
        }

        if (items == null || items?.isEmpty()!!)
            items = ArrayList()

        selectedItems = ArrayList()
        deselectedItems = ArrayList()

        binding?.calendarview?.dayBinder = object : DayBinder<DayViewContainer> {

            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.day = day

                val verticalItem = container.itemBinding.verticalItem
                val verticalLabel = container.itemBinding.verticalLabel
                val badge = container.itemBinding.badge

                verticalLabel.text = day.date.dayOfMonth.toString()

                if (day.owner == DayOwner.THIS_MONTH) {

                    if (badges != null) {
                        val mBadges = badges!![day.date]
                        if (mBadges != null) {
                            badge.visibility = View.VISIBLE
                            badge.setColorFilter(ContextCompat.getColor(context!!, mBadges[0].color))
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
                                verticalLabel.setTextColor(ContextCompat.getColor(context!!, R.color.verde))

                                items?.add(SelectedDay(year, month, dayOfMonth))
                                selectedItems?.add(SelectedDay(year, month, dayOfMonth))
                            }

                            if (badge.isVisible) {
                                badge.visibility = View.INVISIBLE
                                verticalLabel.setTextColor(ContextCompat.getColor(context!!, R.color.rosso))
                                verticalItem.setBackgroundResource(R.drawable.calendarview_deselected_item)
                                // verticalLabel.setTextColor(ContextCompat.getColor(context!!, R.color.white))
                                // verticalItem.background = null

                                for (item in items!!) {
                                    if (item.year == day.date.year.toString() &&
                                        item.month == day.date.monthValue.toString() &&
                                        item.dayOfMonth == day.date.dayOfMonth.toString()) {
                                        items?.remove(item)
                                        deselectedItems?.add(item)
                                        break
                                    }
                                }
                            }
                        }
                        today == day.date -> {
                            verticalItem.setBackgroundResource(R.drawable.calendarview_today_item)
                            verticalLabel.setTextColor(ContextCompat.getColor(context!!, R.color.blu))
                        }
                        else -> {
                            if (isDarkMode)
                                verticalLabel.setTextColor(ContextCompat.getColor(context!!, R.color.white))
                            else
                                verticalLabel.setTextColor(ContextCompat.getColor(context!!, R.color.dark))

                            verticalItem.background = null
                            /*
                            for (item in items!!) {
                                if (item.year == day.date.year.toString() &&
                                    item.month == day.date.monthValue.toString() &&
                                    item.dayOfMonth == day.date.dayOfMonth.toString()) {
                                    items?.remove(item)
                                    break
                                }
                            }
                            */
                        }
                    }

                    if (6 == day.date.dayOfWeek.value || 7 == day.date.dayOfWeek.value) {
                        verticalLabel.setTextColor(ContextCompat.getColor(context!!, R.color.rosso))
                        verticalItem.isClickable = false
                        verticalItem.isFocusable = false
                        if (today == day.date)
                            verticalItem.setBackgroundResource(R.drawable.calendarview_deselected_item)
                        else verticalItem.background = null
                    }
                } else {
                    if (isDarkMode)
                        verticalLabel.setTextColor(ContextCompat.getColor(context!!, R.color.dark))
                    else
                        verticalLabel.setTextColor(ContextCompat.getColor(context!!, R.color.grey_2))

                    if (6 == day.date.dayOfWeek.value || 7 == day.date.dayOfWeek.value) {
                        if (isDarkMode)
                            verticalLabel.setTextColor(ContextCompat.getColor(context!!, R.color.red_transparent_1))
                        else
                            verticalLabel.setTextColor(ContextCompat.getColor(context!!, R.color.red_transparent_2))
                    }

                    verticalItem.background = null
                }
            }
        }

        class MonthViewContainerHeader(view: View) : ViewContainer(view) {
            val calendarviewVerticalHeader = CalendarviewVerticalHeaderBinding.bind(view).calendarviewVerticalHeader
            val calendarviewVerticalLegend = CalendarviewVerticalHeaderBinding.bind(view).calendarviewVerticalLegend
        }

        binding?.calendarview?.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainerHeader> {

            override fun create(view: View) = MonthViewContainerHeader(view)

            override fun bind(container: MonthViewContainerHeader, month: CalendarMonth) {

                val currentDate = DateTimeFormatter.ofPattern("MMMM").format(month.yearMonth.month) + " | " + month.year
                val headerDate = currentDate.uppercase()
                container.calendarviewVerticalHeader.text = headerDate
                // container.calendarviewVerticalHeader.text = "${month.yearMonth.month.name.uppercase().capitalize(Locale.getDefault())} ${"|"} ${month.year}"

                isDarkMode = SharedPreferencesManager.loadDarkModeStateFromPreferences()
                if (isDarkMode)
                    container.calendarviewVerticalHeader.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
                else
                    container.calendarviewVerticalHeader.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark))

                container.calendarviewVerticalLegend.children.forEachIndexed { index, mView ->
                    (mView as TextViewCustom).apply {
                        if (daysOfWeek[index].name == DaysOfWeek.MONDAY.name) {
                            setText(R.string.monday)
                            if (isDarkMode)
                                setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
                            else
                                setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
                        }
                        if (daysOfWeek[index].name == DaysOfWeek.TUESDAY.name) {
                            setText(R.string.tuesday)
                            if (isDarkMode)
                                setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
                            else
                                setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
                        }
                        if (daysOfWeek[index].name == DaysOfWeek.WEDNESDAY.name) {
                            setText(R.string.wednesday)
                            if (isDarkMode)
                                setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
                            else
                                setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
                        }
                        if (daysOfWeek[index].name == DaysOfWeek.THURSDAY.name) {
                            setText(R.string.thursday)
                            if (isDarkMode)
                                setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
                            else
                                setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
                        }
                        if (daysOfWeek[index].name == DaysOfWeek.FRIDAY.name) {
                            setText(R.string.friday)
                            if (isDarkMode)
                                setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
                            else
                                setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
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
            }
        }

        /*
        binding?.calendarview?.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainerLegend> {

            override fun create(view: View) = MonthViewContainerLegend(view)

            override fun bind(container: MonthViewContainerLegend, month: CalendarMonth) {

                container.calendarviewVerticalLegend.children.forEachIndexed { index, mView ->
                    (mView as TextViewCustom).apply {
                        if (daysOfWeek[index].name == DaysOfWeek.MONDAY.name) {
                            setText(R.string.monday)
                            if (isDarkMode)
                                setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
                            else
                                setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
                        }
                        if (daysOfWeek[index].name == DaysOfWeek.TUESDAY.name) {
                            setText(R.string.tuesday)
                            if (isDarkMode)
                                setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
                            else
                                setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
                        }
                        if (daysOfWeek[index].name == DaysOfWeek.WEDNESDAY.name) {
                            setText(R.string.wednesday)
                            if (isDarkMode)
                                setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
                            else
                                setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
                        }
                        if (daysOfWeek[index].name == DaysOfWeek.THURSDAY.name) {
                            setText(R.string.thursday)
                            if (isDarkMode)
                                setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
                            else
                                setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
                        }
                        if (daysOfWeek[index].name == DaysOfWeek.FRIDAY.name) {
                            setText(R.string.friday)
                            if (isDarkMode)
                                setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
                            else
                                setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
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
            }
        }
        */

        binding?.calendarview?.monthScrollListener = { _ ->
            selectedDate?.let {
                // Clear selection if we scroll to a new month.
                selectedDate = null
                binding?.calendarview?.notifyDateChanged(it)
            }
        }

        binding?.calendarviewButton?.setOnClickListener {
            if (items != null) {
                val selectedDaysResponse = SelectedDaysResponse()
                selectedDaysResponse.selectedDays = items
                saveSelectedDaysToPreferences(selectedDaysResponse)

                val arrayItems: ArrayList<String> = ArrayList()
                for (item in items!!) {
                    arrayItems.add(item.dayOfMonth + "/" + item.month + "/" + item.year)
                }
                val mItems = Utils.turnArrayListToString(arrayItems)

                val arraySelectedItems: ArrayList<String> = ArrayList()
                for (item in selectedItems!!) {
                    arraySelectedItems.add(item.dayOfMonth + "/" + item.month + "/" + item.year)
                }
                val mSelectedItems = Utils.turnArrayListToString(arraySelectedItems)

                val arrayDeselectedItems: ArrayList<String> = ArrayList()
                for (item in deselectedItems!!) {
                    arrayDeselectedItems.add(item.dayOfMonth + "/" + item.month + "/" + item.year)
                }
                val mDeselectedItems = Utils.turnArrayListToString(arrayDeselectedItems)

                Log.i(TAG, "mItems: $mItems\nmSelectedItems: $mSelectedItems\nmDeselectedItems: $mDeselectedItems")

                if (arrayItems.isNotEmpty() && arraySelectedItems.isEmpty() && arrayDeselectedItems.isEmpty())
                    Log.i(TAG, "Non mando alcuna segnalazione.")

                if (arrayItems.isNotEmpty() && arraySelectedItems.isNotEmpty() && arrayDeselectedItems.isEmpty())
                    Log.i(TAG, "Vengo questi giorni: mItems")

                if (arrayItems.isNotEmpty() && arraySelectedItems.isNotEmpty() && arrayDeselectedItems.isNotEmpty())
                    Log.i(TAG, "Vengo questi giorni: mItems e non vengo questi giorni: mDeselectedItems")

                if (arrayItems.isNotEmpty() && arraySelectedItems.isEmpty() && arrayDeselectedItems.isNotEmpty())
                    Log.i(TAG, "Vengo questi giorni: mItems e non vengo questi giorni: mDeselectedItems")

                if (arrayItems.isEmpty() && arraySelectedItems.isEmpty() && arrayDeselectedItems.isNotEmpty())
                    Log.i(TAG, "Non vengo questi giorni: mDeselectedItems")

                currentActivity.onBackPressed()
            }
        }
    }

    private fun setViewStyle() {
        isDarkMode = SharedPreferencesManager.loadDarkModeStateFromPreferences()
        if (isDarkMode)
            binding?.calendarviewButton?.style(R.style.ButtonNormalDarkMode)
        else
            binding?.calendarviewButton?.style(R.style.ButtonNormalLightMode)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}