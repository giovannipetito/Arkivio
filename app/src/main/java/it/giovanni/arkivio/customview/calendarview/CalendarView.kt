@file:Suppress("DEPRECATION")

package it.giovanni.arkivio.customview.calendarview

import android.content.Context
import android.util.AttributeSet
import android.view.View.MeasureSpec.UNSPECIFIED
import androidx.annotation.Px
import androidx.core.content.withStyledAttributes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import it.giovanni.arkivio.R
import it.giovanni.arkivio.customview.calendarview.model.*
import it.giovanni.arkivio.customview.calendarview.model.MonthConfig
import it.giovanni.arkivio.customview.calendarview.ui.*
import it.giovanni.arkivio.customview.calendarview.ui.CalendarAdapter
import it.giovanni.arkivio.customview.calendarview.ui.CalendarLayoutManager
import it.giovanni.arkivio.customview.calendarview.ui.CalendarPagerSnapHelper
import it.giovanni.arkivio.customview.calendarview.ui.ViewConfig
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

class CalendarView : RecyclerView {

    companion object {
        /**
         * A value for [dayWidth] and [dayHeight] which indicates that the day cells should have equal
         * width and height. Each view's width and height will be the width of the calender divided by 7.
         */
        const val DAY_SIZE_SQUARE = Int.MIN_VALUE

        /**
         * A value for [daySize] which indicates that the day cells should have equal width and height.
         * Each view's width and height will be the width of the calender divided by 7.
         */
        val SIZE_SQUARE = Size(DAY_SIZE_SQUARE, DAY_SIZE_SQUARE)
    }

    /**
     * The [DayBinder] instance used for managing day cell views creation and reuse. Changing the binder
     * means that the view creation logic could have changed too. We refresh the Calender.
     */
    var dayBinder: DayBinder<*>? = null
        set(value) {
            field = value
            invalidateViewHolders()
        }

    /**
     * The [MonthHeaderFooterBinder] instance used for managing header views. The header view is shown above each month on the Calendar.
     */
    var monthHeaderBinder: MonthHeaderFooterBinder<*>? = null
        set(value) {
            field = value
            invalidateViewHolders()
        }

    /**
     * The [MonthHeaderFooterBinder] instance used for managing footer views. The footer view is shown below each month on the Calendar.
     */
    var monthFooterBinder: MonthHeaderFooterBinder<*>? = null
        set(value) {
            field = value
            invalidateViewHolders()
        }

    /**
     * Called when the calender scrolls to a new month. Mostly beneficial if [ScrollMode] is [ScrollMode.PAGED].
     */
    var monthScrollListener: MonthScrollListener? = null

    /**
     * The xml resource that is inflated and used as the day cell view. This must be provided.
     */
    var dayItem = 0
        set(value) {
            if (field != value) {
                if (value == 0)
                    throw IllegalArgumentException("'dayItemResource' attribute not provided.")
                field = value
                updateAdapterViewConfig()
            }
        }

    /**
     * The xml resource that is inflated and used as a header for every month. Set zero to disable.
     */
    var monthHeader = 0
        set(value) {
            if (field != value) {
                field = value
                updateAdapterViewConfig()
            }
        }

    /**
     * The xml resource that is inflated and used as a footer for every month. Set zero to disable.
     */
    var monthFooter = 0
        set(value) {
            if (field != value) {
                field = value
                updateAdapterViewConfig()
            }
        }

    /**
     * A [ViewGroup] which is instantiated and used as the background for each month.
     * This class must have a constructor which takes only a [Context]. You should
     * exclude the name and constructor of this class from code obfuscation if enabled.
     */
    var month: String? = null
        set(value) {
            if (field != value) {
                field = value
                updateAdapterViewConfig()
            }
        }

    /**
     * The [RecyclerView.Orientation] used for the layout manager. This determines the scroll direction of the the calendar.
     */
    @Orientation
    var orientation = VERTICAL
        set(value) {
            if (field != value) {
                field = value
                setup(startMonth ?: return, endMonth ?: return, firstDayOfWeek ?: return)
            }
        }

    /**
     * The scrolling behavior of the calendar. If [ScrollMode.PAGED], the calendar will snap to the
     * nearest month after a scroll or swipe action. If [ScrollMode.CONTINUOUS], the calendar scrolls normally.
     */
    var scrollMode = ScrollMode.CONTINUOUS
        set(value) {
            if (field != value) {
                field = value
                pagerSnapHelper.attachToRecyclerView(if (value == ScrollMode.PAGED) this else null)
            }
        }

    /**
     * Determines how inDates are generated for each month on the calendar.
     * If set to [InDate.ALL_MONTHS], inDates will be generated for all months.
     * If set to [InDate.FIRST_MONTH], inDates will be generated for the first month only.
     * If set to [InDate.NONE], inDates will not be generated, this means there will be no offset on any month.
     *
     * Note: This causes calendar data to be regenerated, consider using [updateMonthConfiguration]
     * if updating this property alongside [outDateStyle], [maxRowCount] or [inMonth].
     */
    var inDateStyle = InDate.ALL_MONTHS
        set(value) {
            if (field != value) {
                field = value
                updateAdapterMonthConfig()
            }
        }

    /**
     * Determines how outDates are generated for each month on the calendar.
     * If set to [OutDate.END_OF_ROW], the calendar will generate outDates until
     * it reaches the first end of a row. This means that if a month has 6 rows,
     * it will display 6 rows and if a month has 5 rows, it will display 5 rows.
     * If set to [OutDate.END_OF_GRID], the calendar will generate outDates until
     * it reaches the end of a 6 x 7 grid. This means that all months will have 6 rows.
     * If set to [OutDate.NONE], no outDates will be generated.
     *
     * Note: This causes calendar data to be regenerated, consider using [updateMonthConfiguration]
     * if updating this value property [inDateStyle], [maxRowCount] or [inMonth].
     */
    var outDateStyle = OutDate.END_OF_ROW
        set(value) {
            if (field != value) {
                field = value
                updateAdapterMonthConfig()
            }
        }

    /**
     * The maximum number of rows(1 to 6) to show on each month. If a month has a total of 6
     * rows and [maxRowCount] is set to 4, there will be two appearances of that month on the,
     * calendar the first one will show 4 rows and the second one will show the remaining 2 rows.
     * To show a week mode calendar, set this value to 1.
     *
     * Note: This causes calendar data to be regenerated, consider using [updateMonthConfiguration]
     * if updating this property alongside [inDateStyle], [outDateStyle] or [inMonth].
     */
    var maxRowCount = 6
        set(value) {
            if (!(1..6).contains(value)) throw IllegalArgumentException("'maxRowCount' should be between 1 to 6")
            if (field != value) {
                field = value
                updateAdapterMonthConfig()
            }
        }

    /**
     * Determines if dates of a month should stay in its section or can flow into another month's section.
     * If true, a section can only contain dates belonging to that month, its inDates and outDates.
     * if false, the dates are added continuously, irrespective of month sections.
     *
     * When this property is false, a few things behave slightly differently:
     * - If [InDate] is either [InDate.ALL_MONTHS] or [InDate.FIRST_MONTH], only the first index will contain inDates.
     * - If [OutDate] is either [OutDate.END_OF_ROW] or [OutDate.END_OF_GRID], only the last index will contain outDates.
     * - If [OutDate] is [OutDate.END_OF_GRID], outDates are generated for the last index until it satisfies the [maxRowCount] requirement.
     *
     * Note: This causes calendar data to be regenerated, consider using [updateMonthConfiguration]
     * if updating this property alongside [inDateStyle], [outDateStyle] or [maxRowCount].
     */
    var inMonth = true
        set(value) {
            if (field != value) {
                field = value
                updateAdapterMonthConfig()
            }
        }

    /**
     * The duration in milliseconds of the animation used to adjust the CalendarView's height when
     * [scrollMode] is [ScrollMode.PAGED] and the CalendarView's height is set to `wrap_content`.
     * The height change happens when the CalendarView scrolls to a month which has less or more rows
     * than the previous one. Default value is 200. To disable the animation, set this value to zero.
     */
    var animationDuration = 200

    private val pagerSnapHelper = CalendarPagerSnapHelper()

    private var startMonth: YearMonth? = null
    private var endMonth: YearMonth? = null
    private var firstDayOfWeek: DayOfWeek? = null

    private var autoSize = true
    private var autoSizeHeight = DAY_SIZE_SQUARE
    private var sizedInternally = false

    internal val isVertical: Boolean
        get() = orientation == VERTICAL

    private var configJob: Job? = null
    private var internalConfigUpdate = false

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs, defStyleAttr, defStyleAttr)
    }

    private fun init(attributeSet: AttributeSet, defStyleAttr: Int, defStyleRes: Int) {
        if (isInEditMode) return
        setHasFixedSize(true)
        context!!.withStyledAttributes(attributeSet, R.styleable.CalendarView, defStyleAttr, defStyleRes) {
            month = getString(R.styleable.CalendarView_month)
            inMonth = getBoolean(R.styleable.CalendarView_in_month, inMonth)
            dayItem = getResourceId(R.styleable.CalendarView_day_item, dayItem)
            scrollMode = ScrollMode.values()[getInt(R.styleable.CalendarView_scroll_mode, scrollMode.ordinal)]
            monthHeader = getResourceId(R.styleable.CalendarView_month_header, monthHeader)
            monthFooter = getResourceId(R.styleable.CalendarView_month_footer, monthFooter)
            maxRowCount = getInt(R.styleable.CalendarView_max_row_count, maxRowCount)
            orientation = getInt(R.styleable.CalendarView_orientation, orientation)
            inDateStyle = InDate.values()[getInt(R.styleable.CalendarView_in_date_style, inDateStyle.ordinal)]
            outDateStyle = OutDate.values()[getInt(R.styleable.CalendarView_out_date_style, outDateStyle.ordinal)]
            animationDuration = getInt(R.styleable.CalendarView_animation_duration, animationDuration)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (autoSize && !isInEditMode) {
            val widthMode = MeasureSpec.getMode(widthMeasureSpec)
            val widthSize = MeasureSpec.getSize(widthMeasureSpec)
            val heightMode = MeasureSpec.getMode(heightMeasureSpec)

            if (widthMode == UNSPECIFIED && heightMode == UNSPECIFIED) {
                throw UnsupportedOperationException("Cannot calculate the values for day Width/Height with the current configuration.")
            }

            // +0.5 => round to the nearest pixel
            val size = (((widthSize - (monthPaddingStart + monthPaddingEnd)) / 7f) + 0.5).toInt()

            val height = if (autoSizeHeight == DAY_SIZE_SQUARE) size else autoSizeHeight
            val computedSize = daySize.copy(width = size, height = height)
            if (daySize != computedSize) {
                sizedInternally = true
                daySize = computedSize
                sizedInternally = false
                invalidateViewHolders()
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    /**
     * The width, in pixels for each day cell view. Set this to [DAY_SIZE_SQUARE] to have a nice square item view.
     * @see [DAY_SIZE_SQUARE]
     */
    @Px
    @Deprecated("The new `daySize` property clarifies how cell sizing should be done.",
        replaceWith = ReplaceWith("daySize"))
    var dayWidth: Int = DAY_SIZE_SQUARE
        get() = daySize.width
        set(value) {
            field = value
            daySize = Size(field, dayHeight)
        }

    /**
     * The height, in pixels for each day cell view. Set this to [DAY_SIZE_SQUARE] to have a nice square item view.
     * @see [DAY_SIZE_SQUARE]
     */
    @Px
    @Deprecated("The new `daySize` property clarifies how cell sizing should be done.",
        replaceWith = ReplaceWith("daySize"))
    var dayHeight: Int = DAY_SIZE_SQUARE
        get() = daySize.height
        set(value) {
            field = value
            daySize = Size(dayWidth, field)
        }

    /**
     * The size in pixels for each day cell view. Set this to [SIZE_SQUARE] to have a nice square item view.
     * @see [SIZE_SQUARE]
     */
    var daySize: Size = SIZE_SQUARE
        set(value) {
            field = value
            if (!sizedInternally) {
                autoSize = value == SIZE_SQUARE || value.width == DAY_SIZE_SQUARE
                autoSizeHeight = value.height
                invalidateViewHolders()
            }
        }

    /**
     * The padding, in pixels to be applied to the start of each month view.
     */
    @Px
    var monthPaddingStart = 0
        @Deprecated("Directly setting this along with related properties causes repeated invalidation of view holders.",
            replaceWith = ReplaceWith("setMonthPadding"))
        set(value) {
            field = value
            invalidateViewHolders()
        }

    /**
     * The padding, in pixels to be applied to the end of each month view.
     */
    @Px
    var monthPaddingEnd = 0
        @Deprecated("Directly setting this along with related properties causes repeated invalidation of view holders.",
            replaceWith = ReplaceWith("setMonthPadding"))
        set(value) {
            field = value
            invalidateViewHolders()
        }

    /**
     * The padding, in pixels to be applied to the top of each month view.
     */
    @Px
    var monthPaddingTop = 0
        @Deprecated("Directly setting this along with related properties causes repeated invalidation of view holders.",
            replaceWith = ReplaceWith("setMonthPadding"))
        set(value) {
            field = value
            invalidateViewHolders()
        }

    /**
     * The padding, in pixels to be applied to the bottom of each month view.
     */
    @Px
    var monthPaddingBottom = 0
        @Deprecated("Directly setting this along with related properties causes repeated invalidation of view holders.",
            replaceWith = ReplaceWith("setMonthPadding"))
        set(value) {
            field = value
            invalidateViewHolders()
        }

    /**
     * The margin, in pixels to be applied to the start of each month view.
     */
    @Px
    var monthMarginStart = 0
        @Deprecated("Directly setting this along with related properties causes repeated invalidation of view holders.",
            replaceWith = ReplaceWith("setMonthMargins"))
        set(value) {
            field = value
            invalidateViewHolders()
        }

    /**
     * The margin, in pixels to be applied to the end of each month view.
     */
    @Px
    var monthMarginEnd = 0
        @Deprecated("Directly setting this along with related properties causes repeated invalidation of view holders.",
            replaceWith = ReplaceWith("setMonthMargins"))
        set(value) {
            field = value
            invalidateViewHolders()
        }

    /**
     * The margin, in pixels to be applied to the top of each month view.
     */
    @Px
    var monthMarginTop = 0
        @Deprecated("Directly setting this along with related properties causes repeated invalidation of view holders.",
            replaceWith = ReplaceWith("setMonthMargins"))
        set(value) {
            field = value
            invalidateViewHolders()
        }

    /**
     * The margin, in pixels to be applied to the bottom of each month view.
     */
    @Px
    var monthMarginBottom = 0
        @Deprecated("Directly setting this along with related properties causes repeated invalidation of view holders.",
            replaceWith = ReplaceWith("setMonthMargins"))
        set(value) {
            field = value
            invalidateViewHolders()
        }

    private val calendarLayoutManager: CalendarLayoutManager
        get() = layoutManager as CalendarLayoutManager

    private val calendarAdapter: CalendarAdapter
        get() = adapter as CalendarAdapter

    private fun updateAdapterViewConfig() {
        if (adapter != null) {
            calendarAdapter.viewConfig = ViewConfig(dayItem, monthHeader, monthFooter, month)
            invalidateViewHolders()
        }
    }

    private fun invalidateViewHolders() {
        // recycledViewPool.clear() // This does not remove visible views.
        // removeAndRecycleViews() // This removes all views but is internal.
        if (internalConfigUpdate)
            return
        if (adapter == null || layoutManager == null)
            return
        val state = layoutManager?.onSaveInstanceState()
        adapter = adapter
        layoutManager?.onRestoreInstanceState(state)
        post { calendarAdapter.notifyMonthScrollListenerIfNeeded() }
    }

    private fun updateAdapterMonthConfig(config: MonthConfig? = null) {
        if (internalConfigUpdate)
            return
        if (adapter != null) {
            calendarAdapter.monthConfig = config ?: MonthConfig(
                outDateStyle,
                inDateStyle,
                maxRowCount,
                startMonth ?: return,
                endMonth ?: return,
                firstDayOfWeek ?: return,
                inMonth, Job()
            )
            calendarAdapter.notifyDataSetChanged()
            post { calendarAdapter.notifyMonthScrollListenerIfNeeded() }
        }
    }

    /**
     * Update [inDate], [outDate], [maxRowCount] and [hasBoundaries]
     * without generating the underlying calendar data multiple times.
     * See [updateMonthConfigurationAsync] if you wish to do this asynchronously.
     */
    fun updateMonthConfiguration(
        inDate: InDate = this.inDateStyle,
        outDate: OutDate = this.outDateStyle,
        maxRowCount: Int = this.maxRowCount,
        hasBoundaries: Boolean = this.inMonth
    ) {
        configJob?.cancel()
        internalConfigUpdate = true
        this.inDateStyle = inDate
        this.outDateStyle = outDate
        this.maxRowCount = maxRowCount
        this.inMonth = hasBoundaries
        internalConfigUpdate = false
        updateAdapterMonthConfig()
    }

    /**
     * Update [inDate], [outDate], [maxRowCount] and [hasBoundaries] asynchronously without generating the
     * underlying calendar data multiple times. Useful if your [startMonth] and [endMonth] values are many years apart.
     * See [updateMonthConfiguration] if you wish to do this synchronously.
     */
    fun updateMonthConfigurationAsync(
        inDate: InDate = this.inDateStyle,
        outDate: OutDate = this.outDateStyle,
        maxRowCount: Int = this.maxRowCount,
        hasBoundaries: Boolean = this.inMonth,
        completion: Completion? = null
    ) {
        configJob?.cancel()
        internalConfigUpdate = true
        this.inDateStyle = inDate
        this.outDateStyle = outDate
        this.maxRowCount = maxRowCount
        this.inMonth = hasBoundaries
        internalConfigUpdate = false
        configJob = GlobalScope.launch {
            val monthConfig = generateMonthConfig(job)
            withContext(Main) {
                updateAdapterMonthConfig(monthConfig)
                completion?.invoke()
            }
        }
    }

    /**
     * Scroll to a specific month on the calendar. This only shows the view for the month without any animations.
     * For a smooth scrolling effect, use [smoothScrollToMonth]
     */
    fun scrollToMonth(month: YearMonth) {
        calendarLayoutManager.scrollToMonth(month)
    }

    /**
     * Scroll to a specific month on the calendar using a smooth scrolling animation.
     * Just like [scrollToMonth], but with a smooth scrolling animation.
     */
    fun smoothScrollToMonth(month: YearMonth) {
        calendarLayoutManager.smoothScrollToMonth(month)
    }

    /**
     * Scroll to a specific [Day]. This brings the date cell view's top to the top of the CalendarVew
     * in vertical mode or the cell view's left edge to the left edge of the CalendarVew in horizontal
     * mode. No animation is performed. For a smooth scrolling effect, use [smoothScrollToDay].
     */
    fun scrollToDay(day: Day) {
        calendarLayoutManager.scrollToDay(day)
    }

    /**
     * Scroll to a specific [Day] using a smooth scrolling animation. Just like [scrollToDay], but with a smooth scrolling animation.
     */
    fun smoothScrollToDay(day: Day) {
        calendarLayoutManager.smoothScrollToDay(day)
    }

    /**
     * Notify the CalendarView to reload the cell for this [Day]. This causes [DayBinder.bind] to be
     * called with the [ViewContainer] at this position. Use this to reload a date cell on the Calendar.
     */
    fun notifyDayChanged(day: Day) {
        calendarAdapter.reloadDay(day)
    }

    /**
     * Shortcut for [notifyDayChanged] with a [LocalDate] instance.
     */
    @JvmOverloads
    fun notifyDateChanged(date: LocalDate, owner: DayOwner = DayOwner.THIS_MONTH) {
        notifyDayChanged(Day(date, owner))
    }

    private val scrollListenerInternal = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {}
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (newState == SCROLL_STATE_IDLE) {
                calendarAdapter.notifyMonthScrollListenerIfNeeded()
            }
        }
    }

    /**
     * Setup the CalendarView. You can call this any time to change the the
     * desired [startMonth], [endMonth] or [firstDayOfWeek] on the Calendar.
     * See [updateMonthRange] and [updateMonthRangeAsync] for more refined updates.
     *
     * @param startMonth The first month on the calendar.
     * @param endMonth The last month on the calendar.
     * @param firstDayOfWeek An instance of [DayOfWeek] enum to be the first day of week.
     */
    fun setup(startMonth: YearMonth, endMonth: YearMonth, firstDayOfWeek: DayOfWeek) {
        configJob?.cancel()
        if (this.startMonth != null && this.endMonth != null && this.firstDayOfWeek != null) {
            this.firstDayOfWeek = firstDayOfWeek
            updateMonthRange(startMonth, endMonth)
        } else {
            this.startMonth = startMonth
            this.endMonth = endMonth
            this.firstDayOfWeek = firstDayOfWeek
            finishSetup(MonthConfig(outDateStyle, inDateStyle, maxRowCount, startMonth, endMonth, firstDayOfWeek, inMonth, Job()))
        }
    }

    /**
     * Setup the CalendarView, asynchronously. You can call this any time to change
     * the desired [startMonth], [endMonth] or [firstDayOfWeek] on the Calendar.
     * Useful if your [startMonth] and [endMonth] values are many years apart.
     * See [updateMonthRange] and [updateMonthRangeAsync] for more refined updates.
     *
     * Note: the setup MUST finish before any other methods can are called. To be
     * notified when the setup is finished, provide a [completion] parameter.
     *
     * @param startMonth The first month on the calendar.
     * @param endMonth The last month on the calendar.
     * @param firstDayOfWeek An instance of [DayOfWeek] enum to be the first day of week.
     */
    @JvmOverloads
    fun setupAsync(
        startMonth: YearMonth,
        endMonth: YearMonth,
        firstDayOfWeek: DayOfWeek,
        completion: Completion? = null
    ) {
        configJob?.cancel()
        if (this.startMonth != null && this.endMonth != null && this.firstDayOfWeek != null) {
            this.firstDayOfWeek = firstDayOfWeek
            updateMonthRangeAsync(startMonth, endMonth, completion)
        } else {
            this.startMonth = startMonth
            this.endMonth = endMonth
            this.firstDayOfWeek = firstDayOfWeek
            configJob = GlobalScope.launch {
                val monthConfig = MonthConfig(outDateStyle, inDateStyle, maxRowCount, startMonth, endMonth, firstDayOfWeek, inMonth, job)
                withContext(Main) {
                    finishSetup(monthConfig)
                    completion?.invoke()
                }
            }
        }
    }

    private fun finishSetup(monthConfig: MonthConfig) {
        removeOnScrollListener(scrollListenerInternal)
        addOnScrollListener(scrollListenerInternal)
        layoutManager = CalendarLayoutManager(this, orientation)
        adapter = CalendarAdapter(this, ViewConfig(dayItem, monthHeader, monthFooter, month), monthConfig)
    }

    /**
     * Update the CalendarView's start month.
     * This can be called only if you have called [setup] in the past.
     * See [updateEndMonth] and [updateMonthRange].
     */
    @Deprecated("This helper method will be removed to clean up the library's API.",
        ReplaceWith("updateMonthRange()"))
    fun updateStartMonth(startMonth: YearMonth) = updateMonthRange(startMonth, requireEndMonth())

    /**
     * Update the CalendarView's end month.
     * This can be called only if you have called [setup] in the past.
     * See [updateStartMonth] and [updateMonthRange].
     */
    @Deprecated("This helper method will be removed to clean up the library's API.",
        ReplaceWith("updateMonthRange()"))
    fun updateEndMonth(endMonth: YearMonth) = updateMonthRange(requireStartMonth(), endMonth)

    /**
     * Update the CalendarView's start and end months.
     * This can be called only if you have called [setup] or [setupAsync] in the past.
     * See [updateMonthRangeAsync] if you wish to do this asynchronously.
     */
    @JvmOverloads
    fun updateMonthRange(startMonth: YearMonth = requireStartMonth(), endMonth: YearMonth = requireEndMonth()) {
        configJob?.cancel()
        this.startMonth = startMonth
        this.endMonth = endMonth
        val (config, diff) = getMonthUpdateData(Job())
        finishUpdateMonthRange(config, diff)
    }

    /**
     * Update the CalendarView's start and end months, asynchronously.
     * This can be called only if you have called [setup] or [setupAsync] in the past.
     * Useful if your [startMonth] and [endMonth] values are many years apart.
     * See [updateMonthRange] if you wish to do this synchronously.
     */
    @JvmOverloads
    fun updateMonthRangeAsync(
        startMonth: YearMonth = requireStartMonth(),
        endMonth: YearMonth = requireEndMonth(),
        completion: Completion? = null
    ) {
        configJob?.cancel()
        this.startMonth = startMonth
        this.endMonth = endMonth
        configJob = GlobalScope.launch {
            val (config, diff) = getMonthUpdateData(job)
            withContext(Main) {
                finishUpdateMonthRange(config, diff)
                completion?.invoke()
            }
        }
    }

    private fun getMonthUpdateData(job: Job): Pair<MonthConfig, DiffUtil.DiffResult> {
        val monthConfig = generateMonthConfig(job)
        val diffResult = DiffUtil.calculateDiff(MonthRangeDiffCallback(calendarAdapter.monthConfig.months, monthConfig.months), false)
        return Pair(monthConfig, diffResult)
    }

    private fun finishUpdateMonthRange(newConfig: MonthConfig, diffResult: DiffUtil.DiffResult) {
        calendarAdapter.monthConfig = newConfig
        diffResult.dispatchUpdatesTo(calendarAdapter)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        configJob?.cancel()
    }

    private class MonthRangeDiffCallback(private val oldItems: List<Month>, private val newItems: List<Month>) : DiffUtil.Callback() {

        override fun getOldListSize() = oldItems.size
        override fun getNewListSize() = newItems.size
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) = oldItems[oldItemPosition] == newItems[newItemPosition]
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) = areItemsTheSame(oldItemPosition, newItemPosition)
    }

    private fun generateMonthConfig(job: Job): MonthConfig {
        return MonthConfig(
            outDateStyle,
            inDateStyle,
            maxRowCount,
            requireStartMonth(),
            requireEndMonth(),
            requireFirstDayOfWeek(),
            inMonth,
            job
        )
    }

    private fun requireStartMonth(): YearMonth {
        return startMonth ?: throw IllegalStateException("startMonth is not set. Have you called `setup()`?")
    }

    private fun requireEndMonth(): YearMonth {
        return endMonth ?: throw IllegalStateException("endMonth is not set. Have you called `setup()`?")
    }

    private fun requireFirstDayOfWeek(): DayOfWeek {
        return firstDayOfWeek ?: throw IllegalStateException("firstDayOfWeek is not set. Have you called `setup()`?")
    }
}