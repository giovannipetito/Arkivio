package it.giovanni.arkivio.customview.calendarview.model

import java.io.Serializable
import java.time.YearMonth

data class Month(
    val yearMonth: YearMonth,
    val weekDays: List<List<Day>>,
    internal val indexInSameMonth: Int,
    internal val numberOfSameMonth: Int) : Comparable<Month>, Serializable {

    val year: Int = yearMonth.year
    val month: Int = yearMonth.monthValue

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        (other as Month)
        return yearMonth == other.yearMonth &&
                weekDays.first().first() == other.weekDays.first().first() &&
                weekDays.last().last() == other.weekDays.last().last()
    }

    override fun compareTo(other: Month): Int {
        val monthResult = yearMonth.compareTo(other.yearMonth)
        if (monthResult == 0) { // Same yearMonth
            return indexInSameMonth.compareTo(other.indexInSameMonth)
        }
        return monthResult
    }

    override fun hashCode(): Int {
        return 31 * yearMonth.hashCode() + weekDays.first().first().hashCode() + weekDays.last().last().hashCode()
    }

    override fun toString(): String {
        return "CalendarMonth { first = ${weekDays.first().first()}, last = ${weekDays.last().last()}} " +
                "indexInSameMonth = $indexInSameMonth, numberOfSameMonth = $numberOfSameMonth"
    }
}