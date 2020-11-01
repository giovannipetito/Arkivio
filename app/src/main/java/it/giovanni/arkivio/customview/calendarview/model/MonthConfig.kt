package it.giovanni.arkivio.customview.calendarview.model

import it.giovanni.arkivio.customview.calendarview.next
import it.giovanni.arkivio.customview.calendarview.roundDiv
import kotlinx.coroutines.Job
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.WeekFields

internal data class MonthConfig(
    internal val outDate: OutDate,
    internal val inDate: InDate,
    internal val maxRowCount: Int,
    internal val startMonth: YearMonth,
    internal val endMonth: YearMonth,
    internal val firstDayOfWeek: DayOfWeek,
    internal val hasBoundaries: Boolean,
    internal val job: Job
) {

    internal val months: List<Month> = run {
        return@run if (hasBoundaries) {
            generateBoundedMonths(startMonth, endMonth, firstDayOfWeek, maxRowCount, inDate, outDate, job)
        } else {
            generateUnboundedMonths(startMonth, endMonth, firstDayOfWeek, maxRowCount, inDate, outDate, job)
        }
    }

    internal companion object {

        private val uninterruptedJob = Job()

        /**
         * A [YearMonth] will have multiple [Month] instances if the [maxRowCount] is
         * less than 6. Each [Month] will hold just enough [Day] instances(weekDays)
         * to fit in the [maxRowCount].
         */
        fun generateBoundedMonths(
            startMonth: YearMonth,
            endMonth: YearMonth,
            firstDayOfWeek: DayOfWeek,
            maxRowCount: Int,
            inDate: InDate,
            outDate: OutDate,
            job: Job = uninterruptedJob
        ): List<Month> {
            val months = mutableListOf<Month>()
            var currentMonth = startMonth
            while (currentMonth <= endMonth && job.isActive) {
                val generateInDates = when (inDate) {
                    InDate.ALL_MONTHS -> true
                    InDate.FIRST_MONTH -> currentMonth == startMonth
                    InDate.NONE -> false
                }

                val weekDaysGroup = generateWeekDays(currentMonth, firstDayOfWeek, generateInDates, outDate)
                // Group rows by maxRowCount into CalendarMonth classes.
                val calendarMonths = mutableListOf<Month>()
                val numberOfSameMonth = weekDaysGroup.size roundDiv maxRowCount
                var indexInSameMonth = 0
                calendarMonths.addAll(weekDaysGroup.chunked(maxRowCount) { monthDays ->
                    // Use monthDays.toList() to create a copy of the ephemeral list.
                    Month(currentMonth, monthDays.toList(), indexInSameMonth++, numberOfSameMonth)
                })

                months.addAll(calendarMonths)
                if (currentMonth != endMonth) currentMonth = currentMonth.next else break
            }

            return months
        }

        internal fun generateUnboundedMonths(
            startMonth: YearMonth,
            endMonth: YearMonth,
            firstDayOfWeek: DayOfWeek,
            maxRowCount: Int,
            inDate: InDate,
            outDate: OutDate,
            job: Job = uninterruptedJob
        ): List<Month> {
            // Generate a flat list of all days in the given month range
            val allDays = mutableListOf<Day>()
            var currentMonth = startMonth
            while (currentMonth <= endMonth && job.isActive) {
                // If inDates are enabled with boundaries disabled, we show them on the first month only.
                val generateInDates = when (inDate) {
                    InDate.FIRST_MONTH, InDate.ALL_MONTHS -> currentMonth == startMonth
                    InDate.NONE -> false
                }

                allDays.addAll(
                    // We don't generate outDates for any month, they are added manually down below.
                    // This is because if outDates are enabled with boundaries disabled, we show them
                    // on the last month only.
                    generateWeekDays(currentMonth, firstDayOfWeek, generateInDates, OutDate.NONE).flatten()
                )
                if (currentMonth != endMonth) currentMonth = currentMonth.next else break
            }

            // Regroup data into 7 days. Use toList() to create a copy of the ephemeral list.
            val allDaysGroup = allDays.chunked(7).toList()

            val calendarMonths = mutableListOf<Month>()
            val calMonthsCount = allDaysGroup.size roundDiv maxRowCount
            allDaysGroup.chunked(maxRowCount) { ephemeralMonthWeeks ->
                val monthWeeks = ephemeralMonthWeeks.toMutableList()
                // Add the outDates for the last row if needed.
                if (monthWeeks.last().size < 7 && outDate == OutDate.END_OF_ROW || outDate == OutDate.END_OF_GRID) {
                    val lastWeek = monthWeeks.last()
                    val lastDay = lastWeek.last()
                    val outDates = (1..7 - lastWeek.size).map {
                        Day(lastDay.date.plusDays(it.toLong()), DayOwner.NEXT_MONTH)
                    }
                    monthWeeks[monthWeeks.lastIndex] = lastWeek + outDates
                }

                // Add the outDates needed to make the number of rows in this index match the desired maxRowCount.
                while (monthWeeks.size < maxRowCount && outDate == OutDate.END_OF_GRID ||
                    // This will be true when we add the first inDates and the last week row in the CalendarMonth is not filled up.
                    monthWeeks.size == maxRowCount && monthWeeks.last().size < 7 && outDate == OutDate.END_OF_GRID
                ) {
                    // Since boundaries are disabled hence months will overflow, if we have maxRowCount
                    // set to 6 and the last index has only one row left with some missing dates in it,
                    // e.g the last row has only one day in it, if we attempt to fill the grid(up to maxRowCount)
                    // with outDates and the next month does not provide enough dates to fill the grid,
                    // we get more outDates from the following month.

                    /*
                    MON TUE WED THU FRI SAT SUN
                    30  31  01  02  03  04  05 => First outDates start here (month + 1)
                    06  07  08  09  10  11  12
                    13  14  15  16  17  18  19
                    20  21  22  23  24  25  26
                    27  28  29  30  01  02  03 => Second outDates start here (month + 2)
                    04  05  06  07  08  09  10
                    */

                    val lastDay = monthWeeks.last().last()

                    val nextRowDates = (1..7).map {
                        Day(lastDay.date.plusDays(it.toLong()), DayOwner.NEXT_MONTH)
                    }

                    if (monthWeeks.last().size < 7) {
                        // Update the last week to 7 days instead of adding a new row.
                        // Handles the case when we've added all the first inDates and the
                        // last week row in the CalendarMonth is not filled up to 7 days.
                        monthWeeks[monthWeeks.lastIndex] = (monthWeeks.last() + nextRowDates).take(7)
                    } else {
                        monthWeeks.add(nextRowDates)
                    }
                }

                calendarMonths.add(
                    // numberOfSameMonth is the total number of all months and
                    // indexInSameMonth is basically this item's index in the entire month list.
                    Month(startMonth, monthWeeks, calendarMonths.size, calMonthsCount)
                )
            }

            return calendarMonths
        }

        /**
         * Generates the necessary number of weeks for a [YearMonth].
         */
        private fun generateWeekDays(
            yearMonth: YearMonth,
            firstDayOfWeek: DayOfWeek,
            generateInDates: Boolean,
            outDate: OutDate
        ): List<List<Day>> {

            val year = yearMonth.year
            val month = yearMonth.monthValue

            val thisMonthDays = (1..yearMonth.lengthOfMonth()).map {
                Day(LocalDate.of(year, month, it), DayOwner.THIS_MONTH)
            }

            val weekDaysGroup = if (generateInDates) {
                // Group days by week of month so we can add the in dates if necessary.
                val weekOfMonthField = WeekFields.of(firstDayOfWeek, 1).weekOfMonth()
                val groupByWeekOfMonth = thisMonthDays.groupBy { it.date.get(weekOfMonthField) }.values.toMutableList()
                // Add in-dates if necessary
                val firstWeek = groupByWeekOfMonth.first()
                if (firstWeek.size < 7) {
                    val previousMonth = yearMonth.minusMonths(1)
                    val inDates = (1..previousMonth.lengthOfMonth()).toList()
                        .takeLast(7 - firstWeek.size).map {
                            Day(
                                LocalDate.of(previousMonth.year, previousMonth.month, it),
                                DayOwner.PREVIOUS_MONTH
                            )
                        }
                    groupByWeekOfMonth[0] = inDates + firstWeek
                }
                groupByWeekOfMonth
            } else {
                // Group days by 7, first day shown on the month will be day 1.
                // Use toMutableList() to create a copy of the ephemeral list.
                thisMonthDays.chunked(7).toMutableList()
            }

            if (outDate == OutDate.END_OF_ROW || outDate == OutDate.END_OF_GRID) {
                // Add out-dates for the last row.
                if (weekDaysGroup.last().size < 7) {
                    val lastWeek = weekDaysGroup.last()
                    val lastDay = lastWeek.last()
                    val outDates = (1..7 - lastWeek.size).map {
                        Day(lastDay.date.plusDays(it.toLong()), DayOwner.NEXT_MONTH)
                    }
                    weekDaysGroup[weekDaysGroup.lastIndex] = lastWeek + outDates
                }

                // Add more rows to form a 6 x 7 grid
                if (outDate == OutDate.END_OF_GRID) {
                    while (weekDaysGroup.size < 6) {
                        val lastDay = weekDaysGroup.last().last()
                        val nextRowDates = (1..7).map {
                            Day(lastDay.date.plusDays(it.toLong()), DayOwner.NEXT_MONTH)
                        }
                        weekDaysGroup.add(nextRowDates)
                    }
                }
            }

            return weekDaysGroup
        }
    }
}