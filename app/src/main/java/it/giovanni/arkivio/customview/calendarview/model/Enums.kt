package it.giovanni.arkivio.customview.calendarview.model

/**
 * Describes the month to which a [Day] belongs.
 */
enum class DayOwner {
    PREVIOUS_MONTH, // Belongs to the previous month on the calendar. Such days are referred to as inDates.
    THIS_MONTH, // Belongs to the current month on the calendar. Such days are referred to as monthDates.
    NEXT_MONTH // Belongs to the next month on the calendar. Such days are referred to as outDates.
}

/**
 * Determines how outDates are generated for each month on the calendar.
 */
enum class OutDate {
    END_OF_ROW, // The calendar will generate outDates until it reaches the first end of a row.
                // This means that if a month has 6 rows, it will display 6 rows and if a month
                // has 5 rows, it will display 5 rows.
    END_OF_GRID, // The calendar will generate outDates until it reaches the end of a 6 x 7 grid. This means that all months will have 6 rows.
    NONE // outDates will not be generated.
}

/**
 * Determines how inDates are generated for each month on the calendar.
 */
enum class InDate {
    ALL_MONTHS, // inDates will be generated for all months.
    FIRST_MONTH, // inDates will be generated for the first month only.
    NONE // inDates will not be generated, this means that there will be no offset on any month.
}

/**
 * The scrolling behavior of the calendar.
 */
enum class ScrollMode {
    CONTINUOUS, // The calendar will snap to the nearest month after a scroll or swipe action.
    PAGED // The calendar scrolls normally.
}