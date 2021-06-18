package it.giovanni.arkivio.customview.calendarview.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.ViewCompat
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import it.giovanni.arkivio.customview.calendarview.CalendarView
import it.giovanni.arkivio.customview.calendarview.NO_INDEX
import it.giovanni.arkivio.customview.calendarview.inflate
import it.giovanni.arkivio.customview.calendarview.model.CalendarDay
import it.giovanni.arkivio.customview.calendarview.model.CalendarMonth
import it.giovanni.arkivio.customview.calendarview.model.CalendarMonthConfig
import it.giovanni.arkivio.customview.calendarview.model.ScrollMode
import it.giovanni.arkivio.customview.calendarview.orZero
import it.giovanni.arkivio.customview.calendarview.viewholder.DayViewHolder
import it.giovanni.arkivio.customview.calendarview.viewholder.MonthViewHolder
import it.giovanni.arkivio.customview.calendarview.viewholder.WeekViewHolder
import java.time.YearMonth

internal class CalendarAdapter(
    private val calView: CalendarView,
    internal var viewConfig: ViewConfig,
    internal var monthConfig: CalendarMonthConfig
) : RecyclerView.Adapter<MonthViewHolder>() {

    private var visibleMonth: CalendarMonth? = null
    private var calWrapsHeight: Boolean? = null
    private var initialLayout = true

    // Values of headerViewId & footerViewId will be replaced with IDs set in the XML if present.
    var headerViewId = ViewCompat.generateViewId()
    var footerViewId = ViewCompat.generateViewId()

    private val months: List<CalendarMonth>
        get() = monthConfig.months

    private val isAttached: Boolean
        get() = calView.adapter === this

    init {
        setHasStableIds(true)
        registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                initialLayout = true
            }
        })
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        calView.post { notifyMonthScrollListenerIfNeeded() }
    }

    private fun getItem(position: Int): CalendarMonth = months[position]

    override fun getItemId(position: Int): Long = getItem(position).hashCode().toLong()

    override fun getItemCount(): Int = months.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthViewHolder {
        val context = parent.context
        val rootLayout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
        }

        if (viewConfig.monthHeaderRes != 0) {
            val monthHeaderView = rootLayout.inflate(viewConfig.monthHeaderRes)
            // Don't overwrite ID set by the user.
            if (monthHeaderView.id == View.NO_ID) {
                monthHeaderView.id = headerViewId
            } else {
                headerViewId = monthHeaderView.id
            }
            rootLayout.addView(monthHeaderView)
        }

        val dayConfig = DayConfig(
            calView.daySize, viewConfig.dayViewRes,
            calView.dayBinder as DayBinder<ViewContainer>
        )

        val weekHolders = (1..6)
            .map { WeekViewHolder(createDayHolders(dayConfig)) }
            .onEach { weekHolder -> rootLayout.addView(weekHolder.inflateWeekView(rootLayout)) }

        if (viewConfig.monthFooterRes != 0) {
            val monthFooterView = rootLayout.inflate(viewConfig.monthFooterRes)
            // Don't overwrite ID set by the user.
            if (monthFooterView.id == View.NO_ID) {
                monthFooterView.id = footerViewId
            } else {
                footerViewId = monthFooterView.id
            }
            rootLayout.addView(monthFooterView)
        }

        fun setupRoot(root: ViewGroup) {
            ViewCompat.setPaddingRelative(
                root,
                calView.monthPaddingStart, calView.monthPaddingTop,
                calView.monthPaddingEnd, calView.monthPaddingBottom
            )
            root.layoutParams = ViewGroup.MarginLayoutParams(LP.WRAP_CONTENT, LP.WRAP_CONTENT).apply {
                bottomMargin = calView.monthMarginBottom
                topMargin = calView.monthMarginTop

                marginStart = calView.monthMarginStart
                marginEnd = calView.monthMarginEnd
            }
        }

        val userRoot = viewConfig.monthViewClass?.let {
            val customLayout = (Class.forName(it)
                .getDeclaredConstructor(Context::class.java)
                .newInstance(context) as ViewGroup)
            customLayout.apply {
                setupRoot(this)
                addView(rootLayout)
            }
        } ?: rootLayout.apply { setupRoot(this) }

        return MonthViewHolder(
            this,
            userRoot,
            weekHolders,
            calView.monthHeaderBinder as MonthHeaderFooterBinder<ViewContainer>?,
            calView.monthFooterBinder as MonthHeaderFooterBinder<ViewContainer>?
        )
    }

    private fun createDayHolders(dayConfig: DayConfig) = (1..7).map { DayViewHolder(dayConfig) }

    override fun onBindViewHolder(holder: MonthViewHolder, position: Int, payloads: List<Any>) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            payloads.forEach {
                holder.reloadDay(it as CalendarDay)
            }
        }
    }

    override fun onBindViewHolder(holder: MonthViewHolder, position: Int) {
        holder.bindMonth(getItem(position))
    }

    fun reloadDay(day: CalendarDay) {
        val position = getAdapterPosition(day)
        if (position != NO_INDEX) {
            notifyItemChanged(position, day)
        }
    }

    fun notifyMonthScrollListenerIfNeeded() {
        // Guard for cv.post() calls and other callbacks which use this method.
        if (!isAttached) return

        if (calView.isAnimating) {
            // Fixes an issue where findFirstVisibleMonthPosition() returns zero if called when the
            // RecyclerView is animating. This can be replicated in Example 1 when switching from
            // week to month mode. The property changes when switching modes in Example 1 cause
            // notifyDataSetChanged() to be called, hence the animation.
            calView.itemAnimator?.isRunning {
                notifyMonthScrollListenerIfNeeded()
            }
            return
        }
        val visibleItemPos = findFirstVisibleMonthPosition()
        if (visibleItemPos != RecyclerView.NO_POSITION) {
            val visibleMonth = months[visibleItemPos]

            if (visibleMonth != this.visibleMonth) {
                this.visibleMonth = visibleMonth
                calView.monthScrollListener?.invoke(visibleMonth)

                // Fixes issue where the calendar does not resize its height when in horizontal, paged
                // mode and the outDate is not endOfGrid hence the last row of a 5-row visible month
                // is empty. We set such week row's container visibility to GONE in the WeekHolder but
                // it seems the RecyclerView accounts for the items in the immediate previous and next
                // indices when calculating height and uses the tallest one of the three meaning that
                // the current index's view will end up having a blank space at the bottom unless the
                // immediate previous and next indices are also missing the last row. I think there
                // should be a better way to fix this. New: Also fixes issue where the calendar does
                // not wrap each month's height when in vertical, paged mode and just matches parent's
                // height instead.
                if (calView.scrollMode == ScrollMode.PAGED) {
                    val calWrapsHeight = calWrapsHeight ?: (calView.layoutParams.height == LP.WRAP_CONTENT).also {
                        // We modify the layoutParams so we save the initial value set by the user.
                        calWrapsHeight = it
                    }
                    if (!calWrapsHeight) return // Bug only happens when the CalenderView wraps its height.
                    val visibleVH =
                        calView.findViewHolderForAdapterPosition(visibleItemPos) as? MonthViewHolder
                            ?: return
                    val newHeight = visibleVH.headerView?.height.orZero() +
                            // visibleVH.bodyLayout.height` won't not give us the right height as it differs
                            // depending on row count in the month. So we calculate the appropriate height
                            // by checking the number of visible(non-empty) rows.
                            visibleMonth.weekDays.size * calView.daySize.height +
                            visibleVH.footerView?.height.orZero()
                    if (calView.height != newHeight) {
                        ValueAnimator.ofInt(calView.height, newHeight).apply {
                            // Don't animate when the view is shown initially.
                            duration = if (initialLayout) 0 else calView.animationDuration.toLong()
                            addUpdateListener {
                                calView.updateLayoutParams { height = it.animatedValue as Int }
                                visibleVH.itemView.requestLayout()
                            }
                            start()
                        }
                    }
                    if (initialLayout) {
                        initialLayout = false
                        // Request layout in case dataset was changed. See issue #199
                        visibleVH.itemView.requestLayout()
                    }
                }
            }
        }
    }

    internal fun getAdapterPosition(month: YearMonth): Int {
        return months.indexOfFirst { it.yearMonth == month }
    }

    internal fun getAdapterPosition(day: CalendarDay): Int {
        return if (monthConfig.hasBoundaries) {

            val firstMonthIndex = getAdapterPosition(day.positionYearMonth)
            if (firstMonthIndex == NO_INDEX)
                return NO_INDEX

            val firstCalMonth = months[firstMonthIndex]
            val sameMonths = months.slice(firstMonthIndex until firstMonthIndex + firstCalMonth.numberOfSameMonth)
            val indexWithDateInSameMonth = sameMonths.indexOfFirst { months ->
                months.weekDays.any { weeks -> weeks.any { it == day } }
            }

            if (indexWithDateInSameMonth == NO_INDEX)
                NO_INDEX
            else
                firstMonthIndex + indexWithDateInSameMonth

        } else {
            months.indexOfFirst { months ->
                months.weekDays.any { weeks -> weeks.any { it == day } }
            }
        }
    }

    private val layoutManager: CalendarLayoutManager
        get() = calView.layoutManager as CalendarLayoutManager

    private fun findFirstVisibleMonthPosition(): Int = findVisibleMonthPosition()

    private fun findVisibleMonthPosition(): Int {
        val visibleItemPos = layoutManager.findFirstVisibleItemPosition()

        if (visibleItemPos != RecyclerView.NO_POSITION) {
            // We make sure that the view for the returned position is visible to a reasonable degree.
            val visibleItemPx = Rect().let { rect ->
                val visibleItemView = layoutManager.findViewByPosition(visibleItemPos) ?: return NO_INDEX
                visibleItemView.getGlobalVisibleRect(rect)
                return@let if (calView.isVertical) {
                    rect.bottom - rect.top
                } else {
                    rect.right - rect.left
                }
            }

            // Fixes an issue where using DAY_SIZE_SQUARE with a paged calendar causes some dates to
            // stretch slightly outside the intended bounds due to pixel rounding. Hence finding the
            // first visible index will return the view with the px outside bounds. 7 is the number
            // of cells in a week.
            if (visibleItemPx <= 7) {
                val nextItemPosition = visibleItemPos + 1
                return if (months.indices.contains(nextItemPosition)) {
                    nextItemPosition
                } else {
                    visibleItemPos
                }
            }
        }
        return visibleItemPos
    }
}