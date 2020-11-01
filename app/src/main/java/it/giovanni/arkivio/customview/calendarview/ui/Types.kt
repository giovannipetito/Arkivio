package it.giovanni.arkivio.customview.calendarview.ui

import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.annotation.Px
import it.giovanni.arkivio.customview.calendarview.model.Day
import it.giovanni.arkivio.customview.calendarview.model.Month
import java.time.LocalDateTime

open class ViewContainer(val view: View)

interface DayBinder<T : ViewContainer> {
    fun create(view: View): T
    fun bind(container: T, day: Day)
}

interface MonthHeaderFooterBinder<T : ViewContainer> {
    fun create(view: View): T
    fun bind(container: T, month: Month)
}

internal data class ViewConfig(
    @LayoutRes val dayViewRes: Int,
    @LayoutRes val monthHeaderRes: Int,
    @LayoutRes val monthFooterRes: Int,
    val monthViewClass: String?
)

internal data class DayConfig(
    val size: Size,
    @LayoutRes val dayViewRes: Int,
    val viewBinder: DayBinder<ViewContainer>
)

/**
 * Class for describing width and height dimensions in pixels.
 * Basically [android.util.Size], but allows this library to keep minSdk < 21.
 */
data class Size(@Px val width: Int, @Px val height: Int)

typealias MonthScrollListener = (Month) -> Unit

internal typealias LP = ViewGroup.LayoutParams

typealias Completion = () -> Unit

data class Badge(val time: LocalDateTime, @ColorRes val color: Int)