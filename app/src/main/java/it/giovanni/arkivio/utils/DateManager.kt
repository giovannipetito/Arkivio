package it.giovanni.arkivio.utils

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

class DateManager {

    companion object {

        var BASE_FORMAT: String = "E MMM dd HH:mm:ss z yyyy"
        var REQUEST_DATE_FORMAT: String = "yyyy-MM-dd HH:mm:ss"

        var GRANULARITY_HOUR: Int = 1
        var GRANULARITY_MINUTE: Int = 2

        // HH:mm - HH:mm
        fun getRangeTime(startDate: String, endDate: String): StringBuilder {
            val dataInizio = SimpleDateFormat("E MMM dd HH:mm:ss z yyyy", Locale.ITALY).parse(startDate)
            val dataFine = SimpleDateFormat("E MMM dd HH:mm:ss z yyyy", Locale.ITALY).parse(endDate)

            return StringBuilder()
                .append(SimpleDateFormat("HH:mm", Locale.ITALY).format(dataInizio!!))
                .append(" - ")
                .append(SimpleDateFormat("HH:mm", Locale.ITALY).format(dataFine!!))
        }

        // dd MMMM yyyy | HH:mm - HH:mm
        fun getRangeDate1(start_date: String, end_date: String): StringBuilder {
            val startDate = SimpleDateFormat("E MMM dd HH:mm:ss z yyyy", Locale.ITALY).parse(start_date)
            val endDate = SimpleDateFormat("E MMM dd HH:mm:ss z yyyy", Locale.ITALY).parse(end_date)

            return StringBuilder()
                .append(SimpleDateFormat("dd MMMM yyyy", Locale.ITALY).format(startDate!!))
                .append(" | ")
                .append(SimpleDateFormat("HH:mm", Locale.ITALY).format(startDate))
                .append(" - ")
                .append(SimpleDateFormat("HH:mm", Locale.ITALY).format(endDate!!))
        }

        // Sab, 06 Feb 1988 | 06:00 - 12:30
        fun getRangeDate2(start_date: String, end_date: String): StringBuilder {
            val startDate = SimpleDateFormat("E MMM dd HH:mm:ss z yyyy", Locale.ITALY).parse(start_date)
            val endDate = SimpleDateFormat("E MMM dd HH:mm:ss z yyyy", Locale.ITALY).parse(end_date)

            var day = SimpleDateFormat("E", Locale.ITALY).format(startDate!!)
            var month = SimpleDateFormat("MMM", Locale.ITALY).format(startDate)
            day = day.substring(0, 1).uppercase() + day.substring(1)
            month = month.substring(0, 1).uppercase() + month.substring(1)

            return StringBuilder()
                .append(day)
                .append(", ")
                .append(SimpleDateFormat("dd", Locale.ITALY).format(startDate))
                .append(" ")
                .append(month)
                .append(" ")
                .append(SimpleDateFormat("yyyy", Locale.ITALY).format(startDate))
                .append(" | ")
                .append(SimpleDateFormat("HH:mm", Locale.ITALY).format(startDate))
                .append(" - ")
                .append(SimpleDateFormat("HH:mm", Locale.ITALY).format(endDate!!))
        }

        // dd MMM yyyy
        fun getSimpleDate1(date: String): StringBuilder {
            val formattedDate = SimpleDateFormat("E MMM dd HH:mm:ss z yyyy", Locale.ITALY).parse(date)
            return StringBuilder().append(SimpleDateFormat("dd MMM yyyy", Locale.ITALY).format(formattedDate!!))
        }

        // dd MMMM yyyy
        fun getSimpleDate2(date: String): StringBuilder {
            val formattedDate = SimpleDateFormat("E MMM dd HH:mm:ss z yyyy", Locale.ITALY).parse(date)
            return StringBuilder().append(SimpleDateFormat("dd MMMM yyyy", Locale.ITALY).format(formattedDate!!))
        }

        // yyyy/MM/dd
        fun getSimpleDate3(date: String): String {
            val formattedDate = SimpleDateFormat("dd MMM yyyy", Locale.ITALY).parse(date)
            val stringBuilder = StringBuilder().append(SimpleDateFormat("yyyy/MM/dd", Locale.ITALY).format(formattedDate!!))
            return stringBuilder.toString()
        }

        // dd MMMM yyyy HH:mm
        fun getSimpleDate4(date: String): StringBuilder {
            val formattedDate = SimpleDateFormat("E MMM dd HH:mm:ss z yyyy", Locale.ITALY).parse(date)
            return StringBuilder().append(SimpleDateFormat("dd MMMM yyyy HH:mm", Locale.ITALY).format(formattedDate!!))
        }

        // HH:mm:ss
        fun getSimpleTime(date: String): StringBuilder {
            val formattedDate = SimpleDateFormat("E MMM dd HH:mm:ss z yyyy", Locale.ITALY).parse(date)
            return StringBuilder().append(SimpleDateFormat("HH:mm:ss", Locale.ITALY).format(formattedDate!!))
        }

        // EEEE
        fun getSimpleName(date: String): StringBuilder {
            val formattedDate = SimpleDateFormat("E MMM dd HH:mm:ss z yyyy", Locale.ITALY).parse(date)
            return StringBuilder().append(SimpleDateFormat("EEEE", Locale.ITALY).format(formattedDate!!))
        }

        // dd
        fun getSimpleDay(date: String): StringBuilder {
            val formattedDate = SimpleDateFormat("E MMM dd HH:mm:ss z yyyy", Locale.ITALY).parse(date)
            return StringBuilder().append(SimpleDateFormat("dd", Locale.ITALY).format(formattedDate!!))
        }

        // MMMM
        fun getSimpleMonth1(date: String): StringBuilder {
            val formattedDate = SimpleDateFormat("E MMM dd HH:mm:ss z yyyy", Locale.ITALY).parse(date)
            return StringBuilder().append(SimpleDateFormat("MMMM", Locale.ITALY).format(formattedDate!!))
        }

        // MMMM
        fun getSimpleMonth2(date: String): String {
            val formattedDate = SimpleDateFormat("MM", Locale.ITALY).parse(date)
            var month = StringBuilder().append(SimpleDateFormat("MMMM", Locale.ITALY).format(formattedDate!!)).toString()
            month = month.substring(0, 1).uppercase() + month.substring(1)
            return month
        }

        // yyyy
        fun getSimpleYear(date: String): StringBuilder {
            val formattedDate = SimpleDateFormat("E MMM dd HH:mm:ss z yyyy", Locale.ITALY).parse(date)
            return StringBuilder().append(SimpleDateFormat("yyyy", Locale.ITALY).format(formattedDate!!))
        }

        // Sabato
        fun getUpperSimpleName1(date: String): String {
            val formattedDate = SimpleDateFormat("yyyy/MM/dd", Locale.ITALY).parse(date)
            var name = StringBuilder().append(SimpleDateFormat("EEEE", Locale.ITALY).format(formattedDate!!)).toString()
            name = name.substring(0, 1).uppercase() + name.substring(1)
            return name
        }

        // Sabato
        fun getUpperSimpleName2(date: String): String {
            val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.ITALY).parse(date)
            var name = StringBuilder().append(SimpleDateFormat("EEEE", Locale.ITALY).format(formattedDate!!)).toString()
            name = name.substring(0, 1).uppercase() + name.substring(1)
            return name
        }

        // 06 Feb 1988
        fun getUpperSimpleDate1(dataPicker: String): String {
            val formattedDate = SimpleDateFormat("yyyy/MM/dd", Locale.ITALY).parse(dataPicker)
            val stringBuilder = StringBuilder().append(SimpleDateFormat("dd MMM yyyy", Locale.ITALY).format(formattedDate!!))
            return stringBuilder.substring(0, 3) + stringBuilder.substring(3, 4).uppercase() + stringBuilder.substring(4, stringBuilder.length)
        }

        // 06 Feb 1988
        fun getUpperSimpleDate2(date: String): String {
            val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.ITALY).parse(date)
            val stringBuilder = StringBuilder().append(SimpleDateFormat("dd MMM yyyy", Locale.ITALY).format(formattedDate!!))
            return stringBuilder.substring(0, 3) + stringBuilder.substring(3, 4).uppercase() + stringBuilder.substring(4, stringBuilder.length)
        }

        // Febbraio
        fun getUpperSimpleDate3(date: String): String {
            val formattedDate = SimpleDateFormat("MM", Locale.ITALY).parse(date)
            val stringBuilder = StringBuilder().append(SimpleDateFormat("MMMM", Locale.ITALY).format(formattedDate!!))
            return stringBuilder.toString().uppercase()
        }

        // HH : mm
        fun getCustomFormatTime(time: String): String {
            val hour = SimpleDateFormat("HH:mm", Locale.ITALY).parse(time)
            val stringBuilder = StringBuilder()
                .append(SimpleDateFormat("HH", Locale.ITALY).format(hour!!))
                .append(" : ")
                .append(SimpleDateFormat("mm", Locale.ITALY).format(hour))
            return stringBuilder.toString()
        }

        // 8.00 ore
        // 1 giorno
        // 4 giorni
        fun getTimeRange1(dataInizio: String, dataFine: String, totaleOre: Float): String {
            if (dataInizio == dataFine) {
                val totOre = decimalFormatConverter(totaleOre)
                val sb = StringBuilder()
                    .append(totOre)
                    .append(" ore")

                return sb.toString()
            } else {
                val startDate = SimpleDateFormat("yyyy/MM/dd", Locale.ITALY).parse(dataInizio)
                val endDate = SimpleDateFormat("yyyy/MM/dd", Locale.ITALY).parse(dataFine)

                val difference = ((endDate?.time!! - startDate?.time!!) / (60 * 60 * 24 * 1000)).toInt()
                val sb = StringBuilder()
                    .append(difference)
                    .append(if (difference > 1) " giorni" else " giorno")

                return sb.toString()
            }
        }

        // 06 Febbraio 1988
        // 06 Feb 1988 - 07 Feb 1988
        fun getTimeRange2(dataInizio: String, dataFine: String): String {

            val startDate = SimpleDateFormat("yyyy/MM/dd", Locale.ITALY).parse(dataInizio)

            if (dataInizio == dataFine) {
                var month = SimpleDateFormat("MMMM", Locale.ITALY).format(startDate!!)
                month = month.substring(0, 1).uppercase() + month.substring(1)

                val sb = StringBuilder()
                    .append(SimpleDateFormat("dd", Locale.ITALY).format(startDate))
                    .append(" ")
                    .append(month)
                    .append(" ")
                    .append(SimpleDateFormat("yyyy", Locale.ITALY).format(startDate))

                return sb.toString()
            } else {
                val endDate = SimpleDateFormat("yyyy/MM/dd", Locale.ITALY).parse(dataFine)

                val sb = StringBuilder()
                    .append(SimpleDateFormat("dd MMM yyyy", Locale.ITALY).format(startDate!!))
                    .append(" - ")
                    .append(SimpleDateFormat("dd MMM yyyy", Locale.ITALY).format(endDate!!))

                return sb.substring(0, 3) +
                        sb.substring(3, 4).uppercase() +
                        sb.substring(4, 17) +
                        sb.substring(17, 18).uppercase() +
                        sb.substring(18, sb.length)
            }
        }

        // Sab, 06 Feb 1988
        // Sab, 06 Feb 1988 - Dom, 07 Feb 1988
        fun getTimeRange3(dataInizio: String, dataFine: String): String {

            val startDate = SimpleDateFormat("yyyy/MM/dd", Locale.ITALY).parse(dataInizio)

            if (dataInizio == dataFine) {
                var day = SimpleDateFormat("E", Locale.ITALY).format(startDate!!)
                var month = SimpleDateFormat("MMM", Locale.ITALY).format(startDate)
                day = day.substring(0, 1).uppercase() + day.substring(1)
                month = month.substring(0, 1).uppercase() + month.substring(1)

                val sb = StringBuilder()
                    .append(day)
                    .append(", ")
                    .append(SimpleDateFormat("dd", Locale.ITALY).format(startDate))
                    .append(" ")
                    .append(month)
                    .append(" ")
                    .append(SimpleDateFormat("yyyy", Locale.ITALY).format(startDate))
                return sb.toString()
            } else {
                val endDate = SimpleDateFormat("yyyy/MM/dd", Locale.ITALY).parse(dataFine)

                var startDay = SimpleDateFormat("E", Locale.ITALY).format(startDate!!)
                var startMonth = SimpleDateFormat("dd MMM yyyy", Locale.ITALY).format(startDate)
                startDay = startDay.substring(0, 1).uppercase() + startDay.substring(1)
                startMonth = startMonth.substring(0, 3) + startMonth.substring(3, 4).uppercase() + startMonth.substring(4, startMonth.length)

                var endDay = SimpleDateFormat("E", Locale.ITALY).format(endDate!!)
                var endMonth = SimpleDateFormat("dd MMM yyyy", Locale.ITALY).format(endDate)
                endDay = endDay.substring(0, 1).uppercase() + endDay.substring(1)
                endMonth = endMonth.substring(0, 3).uppercase() + endMonth.substring(3, 4).uppercase() + endMonth.substring(4, endMonth.length)

                val sb = StringBuilder()
                    .append(startDay)
                    .append(", ")
                    .append(startMonth)
                    .append(" - ")
                    .append(endDay)
                    .append(", ")
                    .append(endMonth)

                return sb.toString()
            }
        }

        // Sab, 06 Feb 1988
        // Sab, 06 Feb 1988 | 1.00 ore
        // Sab, 06 Feb 1988 - Sab, 06 Feb 1988 | 1 giorno
        // Sab, 06 Feb 1988 - Mer, 10 Feb 1988 | 5 giorni
        fun getTimeRange4(dataInizio: String, dataFine: String, totaleOre: Float): String {

            val startDate = SimpleDateFormat("yyyy/MM/dd", Locale.ITALY).parse(dataInizio)

            if (dataInizio == dataFine && totaleOre < 8) {

                val totOre: String = if (totaleOre < 1) "" else decimalFormatConverter(totaleOre)
                var day = SimpleDateFormat("E", Locale.ITALY).format(startDate!!)
                var month = SimpleDateFormat("MMM", Locale.ITALY).format(startDate)
                day = day.substring(0, 1).uppercase() + day.substring(1)
                month = month.substring(0, 1).uppercase() + month.substring(1)

                val sb: StringBuilder
                if (totOre != "") {
                    sb = StringBuilder()
                        .append(day)
                        .append(", ")
                        .append(SimpleDateFormat("dd", Locale.ITALY).format(startDate))
                        .append(" ")
                        .append(month)
                        .append(" ")
                        .append(SimpleDateFormat("yyyy", Locale.ITALY).format(startDate))
                        .append(" | ").append(totOre)
                    sb.append(" ore")
                } else {
                    sb = StringBuilder()
                        .append(day)
                        .append(", ")
                        .append(SimpleDateFormat("dd", Locale.ITALY).format(startDate))
                        .append(" ")
                        .append(month)
                        .append(" ")
                        .append(SimpleDateFormat("yyyy", Locale.ITALY).format(startDate))
                }

                return sb.toString()

            } else {
                val endDate = SimpleDateFormat("yyyy/MM/dd", Locale.ITALY).parse(dataFine)
                val difference = ((endDate?.time!! - startDate?.time!!) / (60 * 60 * 24 * 1000)).toInt() + 1

                var startDay = SimpleDateFormat("E", Locale.ITALY).format(startDate)
                var startMonth = SimpleDateFormat("dd MMM yyyy", Locale.ITALY).format(startDate)
                startDay = startDay.substring(0, 1).uppercase() + startDay.substring(1)
                startMonth = startMonth.substring(0, 3) + startMonth.substring(3, 4).uppercase() + startMonth.substring(4, startMonth.length)

                var endDay = SimpleDateFormat("E", Locale.ITALY).format(endDate)
                var endMonth = SimpleDateFormat("dd MMM yyyy", Locale.ITALY).format(endDate)
                endDay = endDay.substring(0, 1).uppercase() + endDay.substring(1)
                endMonth = endMonth.substring(0, 3).uppercase() + endMonth.substring(3, 4).uppercase() + endMonth.substring(4, endMonth.length)

                val sb = StringBuilder()
                    .append(startDay)
                    .append(", ")
                    .append(startMonth)
                    .append(" - ")
                    .append(endDay)
                    .append(", ")
                    .append(endMonth)
                    .append(" | ")
                    .append(difference)
                    .append(if (difference > 1) " giorni" else " giorno")

                return sb.toString()
            }
        }

        private fun decimalFormatConverter(f: Float): String {
            val df = DecimalFormat("0.00")
            df.decimalFormatSymbols = DecimalFormatSymbols.getInstance(Locale.US)
            return df.format(f)
        }
    }

    private var dataString: String = ""
    private var data: Date? = null

    constructor(date: Date) {
        val sdf = SimpleDateFormat(BASE_FORMAT, Locale.ITALY)
        sdf.timeZone = TimeZone.getDefault()
        dataString = sdf.format(date)
        data = date
    }

    constructor(millis: Long) {
        val date = Date(millis)
        val sdf = SimpleDateFormat(BASE_FORMAT, Locale.ITALY)
        sdf.timeZone = TimeZone.getDefault()
        dataString = sdf.format(date)
        data = date
    }

    constructor(dateString: String) {
        dataString = dateString
        val sdf = SimpleDateFormat(BASE_FORMAT, Locale.ITALY)
        sdf.timeZone = TimeZone.getDefault()
        data = sdf.parse(dataString)
    }

    fun getDate(): Date {
        return this.data!!
    }

    // HH:mm
    fun getFormatTime(): StringBuilder {
        val sdf = SimpleDateFormat(BASE_FORMAT, Locale.ITALY)
        sdf.timeZone = TimeZone.getDefault()
        val formattedHour = sdf.parse(dataString)
        return StringBuilder().append(SimpleDateFormat("HH:mm", Locale.ITALY).format(formattedHour!!))
    }

    fun getFormatDate(): String {
        return dataString
    }

    // dd MMMM yyyy
    fun getFormatDate1(): StringBuilder {
        val sdf = SimpleDateFormat(BASE_FORMAT, Locale.ITALY)
        sdf.timeZone = TimeZone.getDefault()
        val formattedDate = sdf.parse(dataString)
        return StringBuilder().append(SimpleDateFormat("dd MMMM yyyy", Locale.ITALY).format(formattedDate!!))
    }

    // E dd MMM yyyy (Sab 06 Feb 1988)
    fun getFormatDate2(): String {
        val sdf = SimpleDateFormat(BASE_FORMAT, Locale.ITALY)
        sdf.timeZone = TimeZone.getDefault()
        val formattedDate = sdf.parse(dataString)
        val stringBuilder = StringBuilder().append(SimpleDateFormat("E dd MMM yyyy", Locale.ITALY).format(formattedDate!!))
        return stringBuilder.substring(0, 1).uppercase() + stringBuilder.substring(1, 7) + stringBuilder.substring(7, 8).uppercase() + stringBuilder.substring(8, stringBuilder.length)
    }

    // E dd MMM yyyy (Sab, 06 Feb 1988)
    fun getFormatDate3(): String {
        val sdf = SimpleDateFormat(BASE_FORMAT, Locale.ITALY)
        sdf.timeZone = TimeZone.getDefault()
        val formattedDate = sdf.parse(dataString)

        var day = SimpleDateFormat("E", Locale.ITALY).format(formattedDate!!)
        var month = SimpleDateFormat("MMM", Locale.ITALY).format(formattedDate)
        day = day.substring(0, 1).uppercase() + day.substring(1)
        month = month.substring(0, 1).uppercase() + month.substring(1)

        val sb = StringBuilder()
            .append(day)
            .append(", ")
            .append(SimpleDateFormat("dd", Locale.ITALY).format(formattedDate))
            .append(" ")
            .append(month)
            .append(" ")
            .append(SimpleDateFormat("yyyy", Locale.ITALY).format(formattedDate))
        return sb.toString()
    }

    // YYYY-MM-dd HH:mm:ss
    fun getFormatDate4(): String {
        val sdf = SimpleDateFormat(BASE_FORMAT, Locale.ITALY)
        sdf.timeZone = TimeZone.getDefault()

        val sdfTo = SimpleDateFormat(REQUEST_DATE_FORMAT, Locale.ITALY)
        sdfTo.timeZone = TimeZone.getDefault()

        val dateFromFormatted = sdf.parse(dataString)
        return StringBuilder().append(sdfTo.format(dateFromFormatted!!)).toString()
    }

    // || dd || MM || yyyy ||
    fun getCustomFormatDate(dateToFormat: String): String {
        val formattedDate = SimpleDateFormat(BASE_FORMAT, Locale.ITALY).parse(dataString)
        return StringBuilder().append(SimpleDateFormat(dateToFormat, Locale.ITALY).format(formattedDate!!)).toString()
    }

    fun getGranularityDate(granularity: Int): String {

        val sdf = SimpleDateFormat(BASE_FORMAT, Locale.ITALY)
        sdf.timeZone = TimeZone.getDefault()

        var sdfTo = SimpleDateFormat(REQUEST_DATE_FORMAT, Locale.ITALY)
        sdfTo.timeZone = TimeZone.getDefault()

        val dateFromFormatted = sdf.parse(dataString)
        val dateToFormatted: String

        when (granularity) {
            GRANULARITY_MINUTE -> {
                sdfTo = SimpleDateFormat(REQUEST_DATE_FORMAT.replace(":ss", ":00"), Locale.ITALY)
                dateToFormatted = StringBuilder().append(sdfTo.format(dateFromFormatted!!)).toString()
            }
            GRANULARITY_HOUR -> {
                sdfTo = SimpleDateFormat(REQUEST_DATE_FORMAT.replace("mm:ss", "00:00"), Locale.ITALY)
                dateToFormatted = StringBuilder().append(sdfTo.format(dateFromFormatted!!)).toString()
            }
            else -> {
                dateToFormatted = StringBuilder().append(sdfTo.format(dateFromFormatted!!)).toString()
            }
        }
        return dateToFormatted
    }

    fun setTimeDate(hourOfDay: Int, minute: Int) {
        val sdf = SimpleDateFormat(BASE_FORMAT, Locale.ITALY)
        sdf.timeZone = TimeZone.getDefault()
        val formattedHour = sdf.parse(dataString)
        var baseFormat = BASE_FORMAT
        baseFormat = baseFormat.replace("HH", hourOfDay.toString())
        baseFormat = baseFormat.replace("mm", minute.toString())
        dataString = SimpleDateFormat(baseFormat, Locale.ITALY).format(formattedHour!!)
        data = sdf.parse(dataString)
    }

    fun setIntervalTimeDate(hourOfDay: Int, minute: Int) {

        var min = minute
        if (minute in 0..14) min = 15
        if (minute in 15..29) min = 30
        if (minute in 30..44) min = 45
        if (minute in 45..59) min = 0

        val sdf = SimpleDateFormat(BASE_FORMAT, Locale.ITALY)
        sdf.timeZone = TimeZone.getDefault()
        val formattedHour = sdf.parse(dataString)
        var baseFormat = BASE_FORMAT

        baseFormat =
            if (min == 0) baseFormat.replace("HH", (hourOfDay + 1).toString())
            else baseFormat.replace("HH", hourOfDay.toString())

        baseFormat = baseFormat.replace("mm", min.toString())
        dataString = SimpleDateFormat(baseFormat, Locale.ITALY).format(formattedHour!!)
        data = sdf.parse(dataString)
    }

    fun setDateFromPicker(year: Int, month: Int, dayOfMonth: Int) {
        val calendar: Calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        calendar.set(Calendar.HOUR_OF_DAY, getDatePickerHour())
        calendar.set(Calendar.MINUTE, getDatePickerMinute())
        val sdf = SimpleDateFormat(BASE_FORMAT, Locale.ITALY)
        sdf.timeZone = TimeZone.getDefault()
        dataString = sdf.format(calendar.time)
        data = calendar.time
    }

    fun getDatePickerHour(): Int {
        val sdf = SimpleDateFormat(BASE_FORMAT, Locale.ITALY)
        sdf.timeZone = TimeZone.getDefault()
        val formattedHour = sdf.parse(dataString)
        return SimpleDateFormat("HH", Locale.ITALY).format(formattedHour!!).toInt()
    }

    fun getDatePickerMinute(): Int {
        val sdf = SimpleDateFormat(BASE_FORMAT, Locale.ITALY)
        sdf.timeZone = TimeZone.getDefault()
        val formattedHour = sdf.parse(dataString)
        return SimpleDateFormat("mm", Locale.ITALY).format(formattedHour!!).toInt()
    }
}