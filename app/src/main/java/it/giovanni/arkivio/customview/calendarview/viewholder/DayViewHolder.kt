package it.giovanni.arkivio.customview.calendarview.viewholder

import android.view.View
import android.widget.LinearLayout
import androidx.core.view.*
import androidx.core.view.MarginLayoutParamsCompat.getMarginEnd
import androidx.core.view.MarginLayoutParamsCompat.getMarginStart
import it.giovanni.arkivio.customview.calendarview.inflate
import it.giovanni.arkivio.customview.calendarview.model.Day
import it.giovanni.arkivio.customview.calendarview.ui.DayConfig
import it.giovanni.arkivio.customview.calendarview.ui.ViewContainer

internal class DayViewHolder(private val config: DayConfig) {

    private lateinit var dateView: View
    private lateinit var viewContainer: ViewContainer
    private var day: Day? = null

    fun inflateDayView(parent: LinearLayout): View {
        dateView = parent.inflate(config.dayViewRes).apply {
            // This will be placed in the WeekLayout(A LinearLayout) hence we use
            // LinearLayout.LayoutParams and set the weight appropriately.
            // The parent's wightSum is already set to 7 to accommodate seven week days.
            updateLayoutParams<LinearLayout.LayoutParams> {
                width = config.size.width - getMarginStart(this) - getMarginEnd(this)
                height = config.size.height - marginTop - marginBottom
                weight = 1f
            }
        }
        return dateView
    }

    fun bindDayView(currentDay: Day?) {
        this.day = currentDay
        if (!::viewContainer.isInitialized) {
            viewContainer = config.viewBinder.create(dateView)
        }

        val dayHash = currentDay?.date.hashCode()
        if (viewContainer.view.tag != dayHash) {
            viewContainer.view.tag = dayHash
        }

        if (currentDay != null) {
            if (!viewContainer.view.isVisible) {
                viewContainer.view.isVisible = true
            }
            config.viewBinder.bind(viewContainer, currentDay)
        } else if (!viewContainer.view.isGone) {
            viewContainer.view.isGone = true
        }
    }

    fun reloadViewIfNecessary(day: Day): Boolean {
        return if (day == this.day) {
            bindDayView(this.day)
            true
        } else
            false
    }
}