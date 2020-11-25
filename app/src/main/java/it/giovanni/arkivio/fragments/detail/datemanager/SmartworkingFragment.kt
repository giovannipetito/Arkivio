package it.giovanni.arkivio.fragments.detail.datemanager

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.airbnb.paris.extensions.style
import it.giovanni.arkivio.App
import it.giovanni.arkivio.R
import it.giovanni.arkivio.bean.SelectedDay
import it.giovanni.arkivio.bean.SelectedDaysResponse
import it.giovanni.arkivio.customview.TextViewCustom
import it.giovanni.arkivio.customview.calendarview.generateBadges
import it.giovanni.arkivio.customview.calendarview.getDaysOfWeek
import it.giovanni.arkivio.customview.calendarview.model.CalendarDay
import it.giovanni.arkivio.customview.calendarview.model.CalendarMonth
import it.giovanni.arkivio.customview.calendarview.model.DayOwner
import it.giovanni.arkivio.customview.calendarview.model.DaysOfWeek
import it.giovanni.arkivio.customview.calendarview.ui.Badge
import it.giovanni.arkivio.customview.calendarview.ui.DayBinder
import it.giovanni.arkivio.customview.calendarview.ui.MonthHeaderFooterBinder
import it.giovanni.arkivio.customview.calendarview.ui.ViewContainer
import it.giovanni.arkivio.customview.popup.CustomDialogPopup
import it.giovanni.arkivio.databinding.CalendarviewHeaderBinding
import it.giovanni.arkivio.databinding.CalendarviewItemBinding
import it.giovanni.arkivio.databinding.SmartworkingLayoutBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter
import it.giovanni.arkivio.utils.DateManager
import it.giovanni.arkivio.utils.DateManager.Companion.getSimpleMonth2
import it.giovanni.arkivio.utils.DateManager.Companion.getUpperSimpleDate3
import it.giovanni.arkivio.utils.SharedPreferencesManager.Companion.loadDarkModeStateFromPreferences
import it.giovanni.arkivio.utils.SharedPreferencesManager.Companion.loadSelectedDaysFromPreferences
import it.giovanni.arkivio.utils.SharedPreferencesManager.Companion.saveSelectedDaysToPreferences
import it.giovanni.arkivio.utils.UserFactory
import it.giovanni.arkivio.utils.Utils.Companion.turnArrayListToString
import kotlinx.android.synthetic.main.smartworking_layout.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.YearMonth
import java.util.*
import kotlin.collections.ArrayList

class SmartworkingFragment: DetailFragment() {

    private var mTag: String = SmartworkingFragment::class.java.simpleName
    private var viewFragment: View? = null
    private var currentDate: DateManager? = null
    private var selectedDate: LocalDate? = null
    private val selectedDates = mutableSetOf<LocalDate>()
    private var badges: Map<LocalDate, List<Badge>>? = null
    private var items: ArrayList<SelectedDay>? = null
    private var oldItems: ArrayList<SelectedDay>? = null
    private var selectedItems: ArrayList<SelectedDay>? = null
    private var deselectedItems: ArrayList<SelectedDay>? = null
    private var reallyGoOut = false
    private var onSuccess: Boolean = false
    private var editingMode: Boolean = false

    override fun getLayout(): Int {
        return NO_LAYOUT
    }

    override fun getTitle(): Int {
        return R.string.smartworking_title
    }

    override fun getActionTitle(): Int {
        return NO_TITLE
    }

    override fun searchAction(): Boolean {
        return false
    }

    override fun backAction(): Boolean {
        return false
    }

    override fun closeAction(): Boolean {
        return true
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

    override fun beforeClosing(): Boolean {
        if (!onSuccess) {
            if (!reallyGoOut) {
                val customPopup = CustomDialogPopup(currentActivity, R.style.PopupTheme)
                customPopup.setCancelable(false)
                customPopup.setTitle("", "")

                customPopup.setMessage(resources.getString(R.string.smartworking_message_abort))
                customPopup.setButtons(resources.getString(R.string.button_confirm), {
                    reallyGoOut = true
                    customPopup.dismiss()
                    currentActivity.onBackPressed()
                },
                    resources.getString(R.string.button_cancel), {
                        customPopup.dismiss()
                    }
                )
                customPopup.show()
                return false
            }
            return true
        }
        return true
    }

    override fun onCreateBindingView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: SmartworkingLayoutBinding? = DataBindingUtil.inflate(
            inflater,
            R.layout.smartworking_layout,
            container,
            false
        )
        viewFragment = binding?.root

        val darkModePresenter = DarkModePresenter(this, context!!)
        val model = DarkModeModel(context!!)
        binding?.temp = model
        binding?.presenter = darkModePresenter

        return viewFragment
    }

    @Suppress("DEPRECATION")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        UserFactory.getInstance().givenName = "Giovanni"
        UserFactory.getInstance().surname = "Petito"
        UserFactory.getInstance().lineManagerDisplayName = "Simone Ludovico"

        currentDate = DateManager(Date())
        val currentDay = currentDate?.getCustomFormatDate("yyyy-MM-dd")

        setViewStyle()

        val daysOfWeek = getDaysOfWeek()

        val currentMonth = YearMonth.of(
            currentDate?.getCustomFormatDate("yyyy")?.toInt()!!, currentDate?.getCustomFormatDate(
                "MM"
            )?.toInt()!!
        )
        val startMonth = currentMonth.minusMonths(1)
        val endMonth = currentMonth.plusMonths(3)

        calendarview.setup(startMonth, endMonth, daysOfWeek.first())
        calendarview.scrollToMonth(currentMonth)

        val response = loadSelectedDaysFromPreferences()
        items = response?.selectedDays
        oldItems = cloneItems(items)

        if (items != null && items?.isNotEmpty()!!)
            badges = generateBadges(items!!).groupBy { it.time.toLocalDate() }

        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay // Will be set when this container is bound.
            val binding = CalendarviewItemBinding.bind(view)

            init {
                view.setOnClickListener {
                    if (day.owner == DayOwner.THIS_MONTH) {

                        val year = day.date.year.toString()
                        val month = day.date.monthValue.toString()
                        val dayOfMonth = day.date.dayOfMonth.toString()

                        var isBadged = false
                        for (item in oldItems!!) {
                            if (item.year == year && item.month == month && item.dayOfMonth == dayOfMonth) {
                                isBadged = true
                                break
                            }
                        }

                        if (isBadged) {
                            if (!selectedDates.contains(day.date)) {
                                selectedDates.add(day.date)
                                for (item in items!!) {
                                    if (item.year == year && item.month == month && item.dayOfMonth == dayOfMonth) {
                                        items?.remove(item)
                                        deselectedItems?.add(item)
                                        break
                                    }
                                }
                            } else {
                                selectedDates.remove(day.date)
                                items?.add(SelectedDay(year, month, dayOfMonth))
                                for (item in deselectedItems!!) {
                                    if (item.year == year && item.month == month && item.dayOfMonth == dayOfMonth) {
                                        deselectedItems?.remove(item)
                                        break
                                    }
                                }
                            }
                            calendarview.notifyDayChanged(day)
                        } else {
                            if (selectedDates.contains(day.date)) {
                                selectedDates.remove(day.date)
                                for (item in items!!) {
                                    if (item.year == year && item.month == month && item.dayOfMonth == dayOfMonth) {
                                        items?.remove(item)
                                        break
                                    }
                                }
                                for (item in selectedItems!!) {
                                    if (item.year == year && item.month == month && item.dayOfMonth == dayOfMonth) {
                                        selectedItems?.remove(item)
                                        break
                                    }
                                }
                            } else {
                                selectedDates.add(day.date)
                                var isNotPresent = true
                                for (item in items!!) {
                                    if (item.year == year && item.month == month && item.dayOfMonth == dayOfMonth) {
                                        isNotPresent = false
                                        break
                                    }
                                }
                                if (isNotPresent) {
                                    items?.add(SelectedDay(year, month, dayOfMonth))
                                    selectedItems?.add(SelectedDay(year, month, dayOfMonth))
                                }
                            }
                            calendarview.notifyDayChanged(day)
                        }
                    }
                    checkItemsStatus()
                }
            }
        }

        if (items == null || items?.isEmpty()!!) {
            items = ArrayList()
            oldItems = ArrayList()
        }

        selectedItems = ArrayList()
        deselectedItems = ArrayList()

        calendarview.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) {

                container.day = day

                val itemLayout = container.binding.itemLayout
                val itemText = container.binding.itemText
                val badge = container.binding.badge

                itemText.text = day.date.dayOfMonth.toString()

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

                            val year = day.date.year.toString()
                            val month = day.date.monthValue.toString()
                            val dayOfMonth = day.date.dayOfMonth.toString()

                            if (badge.isInvisible) {
                                itemLayout.setBackgroundResource(R.drawable.calendarview_selected_item)
                                itemText.setTextColor(context?.resources?.getColor(R.color.colorPrimary)!!)
                            }

                            if (badge.isVisible) {

                                if (!editingMode) {
                                    val customPopup =
                                        CustomDialogPopup(currentActivity, R.style.PopupTheme)
                                    customPopup.setCancelable(false)
                                    customPopup.setTitle("", "")
                                    customPopup.setMessage(resources.getString(R.string.smartworking_message_modify))
                                    customPopup.setButtons(
                                        resources.getString(R.string.button_confirm),
                                        {
                                            editingMode = true
                                            customPopup.dismiss()

                                            badge.visibility = View.INVISIBLE
                                            if (currentDay.toString() == day.date.toString()) {
                                                itemLayout.setBackgroundResource(R.drawable.calendarview_today_item)
                                                itemText.setTextColor(context?.resources?.getColor(R.color.azzurro_6)!!)
                                            } else {
                                                itemLayout.background = null
                                                if (isDarkMode)
                                                    itemText.setTextColor(context?.resources?.getColor(
                                                        R.color.white)!!)
                                                else
                                                    itemText.setTextColor(context?.resources?.getColor(
                                                        R.color.dark)!!)
                                            }
                                            checkItemsStatus()
                                        },
                                        resources.getString(R.string.button_cancel),
                                        {
                                            editingMode = false
                                            customPopup.dismiss()
                                            selectedDates.remove(day.date)
                                            items?.add(SelectedDay(year, month, dayOfMonth))
                                            for (item in deselectedItems!!) {
                                                if (item.year == year && item.month == month && item.dayOfMonth == dayOfMonth) {
                                                    deselectedItems?.remove(item)
                                                    break
                                                }
                                            }
                                            checkItemsStatus()
                                        }
                                    )
                                    customPopup.show()
                                } else {
                                    badge.visibility = View.INVISIBLE
                                    if (currentDay.toString() == day.date.toString()) {
                                        itemLayout.setBackgroundResource(R.drawable.calendarview_today_item)
                                        itemText.setTextColor(context?.resources?.getColor(R.color.azzurro_6)!!)
                                    } else {
                                        itemLayout.background = null
                                        if (isDarkMode)
                                            itemText.setTextColor(context?.resources?.getColor(R.color.white)!!)
                                        else
                                            itemText.setTextColor(context?.resources?.getColor(R.color.dark)!!)
                                    }
                                    checkItemsStatus()
                                }
                            }
                        }
                        currentDay.toString() == day.date.toString() -> {
                            itemLayout.setBackgroundResource(R.drawable.calendarview_today_item)
                            itemText.setTextColor(context?.resources?.getColor(R.color.azzurro_6)!!)
                        }
                        else -> {
                            itemLayout.background = null
                            // Se i giorni sono precedenti al giorno corrente.
                            if (currentDay.toString() > day.date.toString()) {
                                itemLayout.isClickable = false
                                itemLayout.isFocusable = false
                                if (isDarkMode)
                                    itemText.setTextColor(context?.resources?.getColor(R.color.arancio)!!)
                                else
                                    itemText.setTextColor(context?.resources?.getColor(R.color.arancio)!!)
                            } else {
                                if (badge.isVisible)
                                    itemText.setTextColor(context?.resources?.getColor(R.color.colorPrimary)!!)
                                else {
                                    if (isDarkMode)
                                        itemText.setTextColor(context?.resources?.getColor(R.color.white)!!)
                                    else
                                        itemText.setTextColor(context?.resources?.getColor(R.color.dark)!!)
                                }
                            }
                        }
                    }

                    if (6 == day.date.dayOfWeek.value || 7 == day.date.dayOfWeek.value) {
                        itemText.setTextColor(context?.resources?.getColor(R.color.rosso_1)!!)
                        itemLayout.isClickable = false
                        itemLayout.isFocusable = false
                        if (currentDay.toString() == day.date.toString())
                            itemLayout.setBackgroundResource(R.drawable.calendarview_deselected_item)
                        else itemLayout.background = null
                    }
                } else {
                    if (isDarkMode)
                        itemText.setTextColor(context?.resources?.getColor(R.color.dark)!!)
                    else
                        itemText.setTextColor(context?.resources?.getColor(R.color.grey_3)!!)

                    if (6 == day.date.dayOfWeek.value || 7 == day.date.dayOfWeek.value) {
                        if (isDarkMode)
                            itemText.setTextColor(context?.resources?.getColor(R.color.red_transparent_1)!!)
                        else
                            itemText.setTextColor(context?.resources?.getColor(R.color.red_transparent_2)!!)
                    }
                    itemLayout.background = null
                }
            }
        }

        class MonthViewContainer(view: View) : ViewContainer(view) {
            val monthHeader = CalendarviewHeaderBinding.bind(view).calendarviewMonthHeader
            val yearHeader = CalendarviewHeaderBinding.bind(view).calendarviewYearHeader
            val separator = CalendarviewHeaderBinding.bind(view).calendarviewSeparator
            val legend = CalendarviewHeaderBinding.bind(view).calendarviewLegend
            val pipe = CalendarviewHeaderBinding.bind(view).calendarviewPipe
        }

        calendarview.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)
            override fun bind(container: MonthViewContainer, month: CalendarMonth) {

                if (isDarkMode) {
                    container.monthHeader.setTextColor(context?.resources?.getColor(R.color.white)!!)
                    container.yearHeader.setTextColor(context?.resources?.getColor(R.color.white)!!)
                    container.pipe.setTextColor(context?.resources?.getColor(R.color.white)!!)
                    container.separator.setBackgroundColor(context?.resources?.getColor(R.color.dark)!!)
                } else {
                    container.monthHeader.setTextColor(context?.resources?.getColor(R.color.dark)!!)
                    container.yearHeader.setTextColor(context?.resources?.getColor(R.color.dark)!!)
                    container.pipe.setTextColor(context?.resources?.getColor(R.color.dark)!!)
                    container.separator.setBackgroundColor(context?.resources?.getColor(R.color.grey_3)!!)
                }

                container.monthHeader.text = getUpperSimpleDate3(month.yearMonth.monthValue.toString())
                container.yearHeader.text = month.year.toString()

                container.legend.children.forEachIndexed { index, mView ->
                    (mView as TextViewCustom).apply {
                        if (daysOfWeek[index].name == DaysOfWeek.MONDAY.name) {
                            setText(R.string.monday)
                            if (isDarkMode) setTextColor(context?.resources?.getColor(R.color.white)!!)
                            else setTextColor(context?.resources?.getColor(R.color.dark)!!)
                        }
                        if (daysOfWeek[index].name == DaysOfWeek.TUESDAY.name) {
                            setText(R.string.tuesday)
                            if (isDarkMode) setTextColor(context?.resources?.getColor(R.color.white)!!)
                            else setTextColor(context?.resources?.getColor(R.color.dark)!!)
                        }
                        if (daysOfWeek[index].name == DaysOfWeek.WEDNESDAY.name) {
                            setText(R.string.wednesday)
                            if (isDarkMode) setTextColor(context?.resources?.getColor(R.color.white)!!)
                            else setTextColor(context?.resources?.getColor(R.color.dark)!!)
                        }
                        if (daysOfWeek[index].name == DaysOfWeek.THURSDAY.name) {
                            setText(R.string.thursday)
                            if (isDarkMode) setTextColor(context?.resources?.getColor(R.color.white)!!)
                            else setTextColor(context?.resources?.getColor(R.color.dark)!!)
                        }
                        if (daysOfWeek[index].name == DaysOfWeek.FRIDAY.name) {
                            setText(R.string.friday)
                            if (isDarkMode) setTextColor(context?.resources?.getColor(R.color.white)!!)
                            else setTextColor(context?.resources?.getColor(R.color.dark)!!)
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

        smartworking_button.setOnClickListener {
            if (items != null) {

                val sortedDates: ArrayList<Date> = sortItems(items)
                var sortedItems: ArrayList<String>? = turnDatesToStrings(sortedDates)
                sortedItems = showLastDayOfMonth(sortedItems)
                // val contiguousItems = groupContiguousItems(sortedItems)
                // val mSortedItems = turnArrayListToString(sortedItems!!)

                val sortedSelectedDates: ArrayList<Date> = sortItems(selectedItems)
                var sortedSelectedItems: ArrayList<String>? = turnDatesToStrings(sortedSelectedDates)
                sortedSelectedItems = showLastDayOfMonth(sortedSelectedItems)
                val contiguousSelectedItems = groupContiguousItems(sortedSelectedItems)
                // val mSortedSelectedItems = turnArrayListToString(sortedSelectedItems!!)

                val sortedDeselectedDates: ArrayList<Date> = sortItems(deselectedItems)
                var sortedDeselectedItems: ArrayList<String>? = turnDatesToStrings(sortedDeselectedDates)
                sortedDeselectedItems = showLastDayOfMonth(sortedDeselectedItems)
                val contiguousDeselectedItems = groupContiguousItems(sortedDeselectedItems)
                // val mSortedDeselectedItems = turnArrayListToString(sortedDeselectedItems!!)

                // Log.i(TAG, "contiguousItems: $contiguousItems\ncontiguousSelectedItems: $contiguousSelectedItems\ncontiguousDeselectedItems: $contiguousDeselectedItems")
                // Log.i(TAG, "mSortedItems: $mSortedItems\nmSortedSelectedItems: $mSortedSelectedItems\nmSortedDeselectedItems: $mSortedDeselectedItems")

                if (sortedItems.isNotEmpty() && sortedSelectedItems.isEmpty() && sortedDeselectedItems.isEmpty()) {
                    Log.i(mTag, "Non mando alcuna segnalazione.")
                }

                if (sortedItems.isNotEmpty() && sortedSelectedItems.isNotEmpty() && sortedDeselectedItems.isEmpty()) {
                    UserFactory.getInstance().smartworkingSubjectMail = resources.getString(
                        R.string.smartworking_subject_mail,
                        UserFactory.getInstance().surname
                    )
                    UserFactory.getInstance().smartworkingContentMail = resources.getString(
                        R.string.smartworking_content_mail,
                        UserFactory.getInstance().lineManagerDisplayName,
                        contiguousSelectedItems,
                        UserFactory.getInstance().givenName
                    )
                    Log.i(mTag, UserFactory.getInstance().smartworkingSubjectMail + "\n" + UserFactory.getInstance().smartworkingContentMail)
                }

                if (sortedItems.isNotEmpty() && sortedSelectedItems.isEmpty() && sortedDeselectedItems.isNotEmpty()) {
                    UserFactory.getInstance().smartworkingSubjectMail = resources.getString(
                        R.string.smartworking_revision_subject_mail,
                        UserFactory.getInstance().surname
                    )
                    UserFactory.getInstance().smartworkingContentMail = resources.getString(
                        R.string.smartworking_revision_1_content_mail,
                        contiguousDeselectedItems,
                        UserFactory.getInstance().givenName
                    )
                    Log.i(mTag,
                        UserFactory.getInstance().smartworkingSubjectMail + "\n" + UserFactory.getInstance().smartworkingContentMail)
                }

                if (sortedItems.isEmpty() && sortedSelectedItems.isEmpty() && sortedDeselectedItems.isNotEmpty()) {
                    UserFactory.getInstance().smartworkingSubjectMail = resources.getString(
                        R.string.smartworking_revision_subject_mail,
                        UserFactory.getInstance().surname
                    )
                    UserFactory.getInstance().smartworkingContentMail = resources.getString(
                        R.string.smartworking_revision_1_content_mail,
                        contiguousDeselectedItems,
                        UserFactory.getInstance().givenName
                    )
                    Log.i(mTag, UserFactory.getInstance().smartworkingSubjectMail + "\n" + UserFactory.getInstance().smartworkingContentMail)
                }

                if (sortedItems.isNotEmpty() && sortedSelectedItems.isNotEmpty() && sortedDeselectedItems.isNotEmpty()) {
                    UserFactory.getInstance().smartworkingSubjectMail = resources.getString(
                        R.string.smartworking_revision_subject_mail,
                        UserFactory.getInstance().surname
                    )
                    UserFactory.getInstance().smartworkingContentMail = resources.getString(
                        R.string.smartworking_revision_2_content_mail,
                        contiguousDeselectedItems,
                        UserFactory.getInstance().lineManagerDisplayName,
                        contiguousSelectedItems,
                        UserFactory.getInstance().givenName
                    )
                    Log.i(mTag, UserFactory.getInstance().smartworkingSubjectMail + "\n" + UserFactory.getInstance().smartworkingContentMail)
                }

                sendSmartworkingCommunication()
            }
        }
    }

    private fun setViewStyle() {
        isDarkMode = loadDarkModeStateFromPreferences()
        if (isDarkMode)
            smartworking_button.style(R.style.DarkModeButton)
        else
            smartworking_button.style(R.style.CustomLoginButton)
    }

    private fun cloneItems(list: ArrayList<SelectedDay>?): ArrayList<SelectedDay>? {
        return if (list != null) {
            val clonedList = ArrayList<SelectedDay>()
            for (i in list.indices) {
                clonedList.add(list[i].cloneList())
            }
            clonedList
        } else null
    }

    private fun checkItemsStatus() {
        if (items?.size != oldItems?.size) {
            smartworking_button_container.animate().translationY(4F).alpha(1.0f).duration = 500
        }
        else {
            if (selectedItems?.isEmpty()!! && deselectedItems?.isEmpty()!!) {
                smartworking_button_container.animate().translationY(smartworking_button_container.height.toFloat()).alpha(0.0f).duration = 500
            }
            else {
                for (i in 0 until items?.size!!) {
                    if (items!![i].year == oldItems!![i].year && items!![i].month == oldItems!![i].month && items!![i].dayOfMonth == oldItems!![i].dayOfMonth) {
                        smartworking_button_container.animate().translationY(smartworking_button_container.height.toFloat()).alpha(0.0f).duration = 500
                    } else {
                        smartworking_button_container.animate().translationY(4F).alpha(1.0f).duration = 500
                        break
                    }
                }
            }
        }
    }

    private fun sortItems(items: ArrayList<SelectedDay>?): ArrayList<Date> {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val sortedDates = ArrayList<Date>()
        for (item in items!!) {
            sortedDates.add(sdf.parse(item.dayOfMonth + "/" + item.month + "/" + item.year)!!)
        }
        try {
            sortedDates.sort()
        } catch (ex: ParseException) {
            ex.printStackTrace()
        }
        return sortedDates
    }

    private fun turnDatesToStrings(sortedDates: ArrayList<Date>?): ArrayList<String> {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val sortedItems: ArrayList<String> = ArrayList()
        for (date in sortedDates!!) {
            sortedItems.add(sdf.format(date))
        }
        return sortedItems
    }

    private fun showLastDayOfMonth(sortedItems: ArrayList<String>?): ArrayList<String> {
        if (sortedItems?.isNotEmpty()!!) {
            for (i in 0 until sortedItems.size - 1) {
                if (sortedItems[i] > sortedItems[i+1]) {
                    sortedItems[i] = sortedItems[i] + "LastDayOfMonth"
                }
            }
            sortedItems[sortedItems.size - 1] = sortedItems[sortedItems.size - 1] + "LastDayOfMonth"
        }
        return sortedItems
    }

    private fun groupContiguousItems(sortedItems: ArrayList<String>?): String {

        val sdf = SimpleDateFormat("MM", Locale.getDefault())
        val contiguousItems: ArrayList<String> = ArrayList()
        var firstDay: String
        var lastDay: String
        var lastDate: String
        var lastDayTester = ""
        if (sortedItems?.isNotEmpty()!!) {
            for (i in 0 until sortedItems.size) {
                firstDay = ""
                lastDay = ""
                lastDate = ""
                for (j in i + 1 until sortedItems.size) {
                    if (sortedItems[j-1].substring(0, 2).toInt() + 1 == sortedItems[j].substring(0, 2).toInt()) {
                        firstDay = sortedItems[i].substring(0, 2)
                        lastDay = sortedItems[j].substring(0, 2)
                        lastDate = sortedItems[j]
                    } else {
                        if (firstDay == "") {
                            if (sortedItems[i].substring(0, 2) != lastDayTester) {

                                if (!sortedItems[i].contains("LastDayOfMonth")) {
                                    contiguousItems.add("il giorno " + sortedItems[i].substring(0, 2))
                                } else {
                                    val month = sdf.parse(sortedItems[i].substring(3, 5))
                                    val currentMonth = getSimpleMonth2(sdf.format(month!!))
                                    contiguousItems.add("il giorno " + sortedItems[i].substring(0, 2) + " del mese di " + currentMonth)
                                }
                            }
                        }
                        break
                    }
                }
                if (lastDay != "" && lastDay != lastDayTester) {
                    if (!lastDate.contains("LastDayOfMonth")) {
                        contiguousItems.add("i giorni dal $firstDay al $lastDay")
                    } else {
                        val month = sdf.parse(lastDate.substring(3, 5))
                        val currentMonth = getSimpleMonth2(sdf.format(month!!))
                        contiguousItems.add("i giorni dal $firstDay al $lastDay del mese di $currentMonth")
                    }
                    lastDayTester = lastDay
                }
            }
            if (sortedItems[sortedItems.size - 1].substring(0, 2) != lastDayTester) {
                val month = sdf.parse(sortedItems[sortedItems.size - 1].substring(3, 5))
                val currentMonth = getSimpleMonth2(sdf.format(month!!))
                contiguousItems.add("il giorno " + sortedItems[sortedItems.size - 1].substring(0, 2) + " del mese di " + currentMonth)
            }
        }
        return turnArrayListToString(contiguousItems)
    }

    private fun sendSmartworkingCommunication() {
        onSendSmartworkingCommunicationSuccess()
    }

    private fun onSendSmartworkingCommunicationSuccess() {

        val selectedDaysResponse = SelectedDaysResponse()
        selectedDaysResponse.selectedDays = items
        saveSelectedDaysToPreferences(selectedDaysResponse)

        currentActivity.runOnUiThread {

            val customPopup = CustomDialogPopup(currentActivity, R.style.PopupTheme)
            customPopup.setCancelable(false)
            customPopup.setTitle("", "")
            customPopup.setMessage(resources.getString(R.string.smartworking_message_success))
            customPopup.setButtons(resources.getString(R.string.button_ok)) {
                customPopup.dismiss()
                onSuccess = true
                currentActivity.onBackPressed()
            }
            customPopup.show()
        }
    }
}