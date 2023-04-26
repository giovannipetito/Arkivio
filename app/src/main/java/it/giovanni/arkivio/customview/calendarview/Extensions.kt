package it.giovanni.arkivio.customview.calendarview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import it.giovanni.arkivio.R
import it.giovanni.arkivio.model.SelectedDay
import it.giovanni.arkivio.customview.calendarview.ui.Badge
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.WeekFields
import java.util.*
import kotlin.collections.ArrayList

internal fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

internal fun Int?.orZero(): Int = this ?: 0

val LocalDate.yearMonth: YearMonth
    get() = YearMonth.of(year, month)

val YearMonth.next: YearMonth
    get() = this.plusMonths(1)

val YearMonth.previous: YearMonth
    get() = this.minusMonths(1)

internal const val NO_INDEX = -1

internal val CoroutineScope.job: Job
    get() = requireNotNull(coroutineContext[Job])

fun getDaysOfWeek(): Array<DayOfWeek> {
    val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
    var daysOfWeek = DayOfWeek.values()
    // Order daysOfWeek array so that firstDayOfWeek is at index 0.
    // Only necessary if firstDayOfWeek != DayOfWeek.MONDAY which has ordinal 0.
    if (firstDayOfWeek != DayOfWeek.MONDAY) {
        val rhs = daysOfWeek.sliceArray(firstDayOfWeek.ordinal..daysOfWeek.indices.last)
        val lhs = daysOfWeek.sliceArray(0 until firstDayOfWeek.ordinal)
        daysOfWeek = rhs + lhs
    }
    return daysOfWeek
}

// We want the remainder to be added as the division result. E.g: 5/2 should be 3.
infix fun Int.roundDiv(other: Int): Int {
    val div = this / other
    val rem = this % other
    // Add the last value dropped from div if rem is not zero
    return if (rem == 0) div else div + 1
}

fun generateBadge(year: Int, month: Int, dayOfMonth: Int): List<Badge> {

    val selectedDate: LocalDate = LocalDate.of(year, month, dayOfMonth)
    val badges = mutableListOf<Badge>()
    badges.add(Badge(selectedDate.atTime(9, 0), R.color.verde))
    return badges
}

fun generateBadges(list: ArrayList<SelectedDay>): List<Badge> {

    val badges = mutableListOf<Badge>()
    for (item in list) {
        val selectedDate: LocalDate = LocalDate.of(item.year?.toInt()!!, item.month?.toInt()!!, item.dayOfMonth?.toInt()!!)
        badges.add(Badge(selectedDate.atTime(9, 0), R.color.verde))
    }
    return badges
}