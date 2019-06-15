package it.giovanni.kotlin.fragments.detail

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.DatePicker
import android.widget.NumberPicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import it.giovanni.kotlin.R
import it.giovanni.kotlin.fragments.DetailFragment
import it.giovanni.kotlin.utils.DateManager
import it.giovanni.kotlin.utils.DateManager.Companion.getCustomFormatTime
import it.giovanni.kotlin.utils.DateManager.Companion.getRangeDate1
import it.giovanni.kotlin.utils.DateManager.Companion.getRangeDate2
import it.giovanni.kotlin.utils.DateManager.Companion.getRangeTime
import it.giovanni.kotlin.utils.DateManager.Companion.getSimpleDate1
import it.giovanni.kotlin.utils.DateManager.Companion.getSimpleDate2
import it.giovanni.kotlin.utils.DateManager.Companion.getSimpleDate3
import it.giovanni.kotlin.utils.DateManager.Companion.getSimpleDate4
import it.giovanni.kotlin.utils.DateManager.Companion.getSimpleDay
import it.giovanni.kotlin.utils.DateManager.Companion.getSimpleMonth
import it.giovanni.kotlin.utils.DateManager.Companion.getSimpleName
import it.giovanni.kotlin.utils.DateManager.Companion.getSimpleTime
import it.giovanni.kotlin.utils.DateManager.Companion.getSimpleYear
import it.giovanni.kotlin.utils.DateManager.Companion.getTimeRange1
import it.giovanni.kotlin.utils.DateManager.Companion.getTimeRange2
import it.giovanni.kotlin.utils.DateManager.Companion.getTimeRange3
import it.giovanni.kotlin.utils.DateManager.Companion.getTimeRange4
import it.giovanni.kotlin.utils.DateManager.Companion.getUpperSimpleDate1
import it.giovanni.kotlin.utils.DateManager.Companion.getUpperSimpleDate2
import it.giovanni.kotlin.utils.DateManager.Companion.getUpperSimpleName1
import it.giovanni.kotlin.utils.DateManager.Companion.getUpperSimpleName2
import kotlinx.android.synthetic.main.date_manager_layout.*
import kotlinx.android.synthetic.main.datepicker_single_date.view.*
import kotlinx.android.synthetic.main.timepicker_range_time.view.*
import kotlinx.android.synthetic.main.timepicker_single_time.view.*
import kotlinx.android.synthetic.main.timepicker_single_time.view.back
import kotlinx.android.synthetic.main.timepicker_single_time.view.ok
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class DateManagerFragment : DetailFragment(), DatePickerDialog.OnDateSetListener {

    private var calendar : Calendar? = null

    private val currentHours = Date().hours
    private val currentMinutes = Date().minutes

    private var startCurrentDate: DateManager? = null
    private var endCurrentDate: DateManager? = null
    private var startDate: DateManager? = null
    private var endDate: DateManager? = null
    private var timeDate: DateManager? = null

    private var isMinDate: Boolean? = true
    private var pickerData: String? = null

    private var startTime: String? = null
    private var endTime: String? = null
    private var rangeTime: String? = null

    private var giorno : Int? = null
    private var mese : Int? = null
    private var anno : Int? = null

    private val interval = 15
    private val formatter = DecimalFormat("00")
    private var minutePicker: NumberPicker? = null

    private var granularityHour: Int = 1
    private var granularityMinute: Int = 2

    override fun getLayout(): Int {
        return R.layout.date_manager_layout
    }

    override fun getTitle(): Int {
        return R.string.custom_picker_title
    }

    override fun getActionTitle(): Int {
        return NO_TITLE
    }

    override fun searchAction(): Boolean {
        return false
    }

    override fun backAction(): Boolean {
        return true
    }

    override fun closeAction(): Boolean {
        return false
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

    @SuppressLint("SimpleDateFormat")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        calendar = Calendar.getInstance()

        startCurrentDate = DateManager(Date())
        endCurrentDate = DateManager(Date().time + (60 * 60 * 1000))

        current_hours?.text = currentHours.toString()
        current_minutes?.text = currentMinutes.toString()

        getdate_1?.text = DateManager(startCurrentDate!!.getFormatDate()).getDate().toString()
        getdate_2?.text = DateManager(startCurrentDate!!.getFormatDate()).getDate().time.toString()

        current_start_time?.text = startCurrentDate?.getFormatTime()
        current_end_time?.text = endCurrentDate?.getFormatTime()

        current_date_1?.text = startCurrentDate?.getFormatDate1()
        current_date_2?.text = startCurrentDate?.getFormatDate2()
        current_date_3?.text = startCurrentDate?.getFormatDate3()

        current_start_date?.text = startCurrentDate?.getFormatDate4()
        current_end_date?.text = endCurrentDate?.getFormatDate4()

        custom_format_date?.text = startCurrentDate?.getCustomFormatDate("|| dd || MM || yyyy ||")

        val dataInizio = "06/02/1988 06:00:00"
        val dataFine = "06/02/1988 12:30:00"
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        startDate = DateManager(dateFormat.parse(dataInizio))
        endDate = DateManager(dateFormat.parse(dataFine))

        response_start_time?.text = startDate?.getFormatTime()
        response_end_time?.text = endDate?.getFormatTime()

        response_start_date?.text = startDate?.getFormatDate4()
        response_end_date?.text = endDate?.getFormatDate4()

        current_range_time?.text = getRangeTime(startCurrentDate!!.getFormatDate(), endCurrentDate!!.getFormatDate())
        response_range_time?.text = getRangeTime(startDate!!.getFormatDate(), endDate!!.getFormatDate())

        current_range_date_1?.text = getRangeDate1(startCurrentDate!!.getFormatDate(), endCurrentDate!!.getFormatDate())
        response_range_date_1?.text = getRangeDate1(startDate!!.getFormatDate(), endDate!!.getFormatDate())

        current_range_date_2?.text = getRangeDate2(startCurrentDate!!.getFormatDate(), endCurrentDate!!.getFormatDate())

        simple_date_1?.text = getSimpleDate1(startCurrentDate!!.getFormatDate())
        simple_date_2?.text = getSimpleDate2(startCurrentDate!!.getFormatDate())
        simple_date_3?.text = getSimpleDate3("06 feb 1988") // dd MMM yyyy
        simple_date_4?.text = getSimpleDate4(startCurrentDate!!.getFormatDate())

        simple_time?.text = getSimpleTime(startCurrentDate!!.getFormatDate())
        simple_name?.text = getSimpleName(startCurrentDate!!.getFormatDate())
        simple_day?.text = getSimpleDay(startCurrentDate!!.getFormatDate())
        simple_month?.text = getSimpleMonth(startCurrentDate!!.getFormatDate())
        simple_year?.text = getSimpleYear(startCurrentDate!!.getFormatDate())

        upper_simple_name_1?.text = getUpperSimpleName1("1988/02/06")
        upper_simple_name_2?.text = getUpperSimpleName2("06/02/1988")
        upper_simple_date_1?.text = getUpperSimpleDate1("1988/02/06")
        upper_simple_date_2.text = getUpperSimpleDate2("06/02/1988")

        custom_format_time?.text = getCustomFormatTime("06:30")

        time_range_1?.text = getTimeRange1("1988/02/06", "1988/02/06", 8F)
        time_range_2?.text = getTimeRange1("1988/02/06", "1988/02/07", 8F)
        time_range_3?.text = getTimeRange1("1988/02/06", "1988/02/10", 8F)

        time_range_4?.text = getTimeRange2("1988/02/06", "1988/02/06")
        time_range_5?.text = getTimeRange2("1988/02/06", "1988/02/07")

        time_range_6?.text = getTimeRange3("1988/02/06", "1988/02/06")
        time_range_7?.text = getTimeRange3("1988/02/06", "1988/02/07")

        time_range_8?.text = getTimeRange4("1988/02/06", "1988/02/06", 0.5F)
        time_range_9?.text = getTimeRange4("1988/02/06", "1988/02/06", 1F)
        time_range_10?.text = getTimeRange4("1988/02/06", "1988/02/06", 8F)
        time_range_11?.text = getTimeRange4("1988/02/06", "1988/02/10", 8F)

        granularity_date_1?.text = startCurrentDate?.getGranularityDate(granularityHour)
        granularity_date_2?.text = startCurrentDate?.getGranularityDate(granularityMinute)

        timeDate = DateManager(dateFormat.parse(dataInizio))
        timeDate?.setTimeDate(currentHours, currentMinutes)
        time_date?.text = getSimpleDate4(timeDate!!.getFormatDate())

        datepickerdialog_mindate?.setOnClickListener(datePickerDialogMinDateClickListener)
        datepickerdialog_maxdate?.setOnClickListener(datePickerDialogMaxDateClickListener)

        current_time_picker_label?.setOnClickListener(currentTimePickerDialogClickListener)

        start_time_picker_label?.setOnClickListener(startTimePickerDialogClickListener)

        range_time_picker_label?.setOnClickListener(rangeTimePickerDialogClickListener)

        // CUSTOM PICKERS

        number_picker.minValue = 0
        number_picker.maxValue = 12

        giorno = calendar?.get(Calendar.DAY_OF_MONTH)
        mese = calendar?.get(Calendar.MONTH)!! + 1
        anno = calendar?.get(Calendar.YEAR)

        date_picker.setOnClickListener {
            showDatePicker()
        }

        single_time_picker.setOnClickListener {
            showSingleTimePicker()
        }

        range_time_picker_1.setOnClickListener {
            showRangeTimePicker1()
        }

        range_time_picker_2.setOnClickListener {
            // showRangeTimePicker2()
        }
    }

    private val datePickerDialogMinDateClickListener = View.OnClickListener {

        val datePickerDialog = DatePickerDialog(context!!, R.style.PickerDialogTheme, this, calendar?.get(Calendar.YEAR)!!, calendar?.get(Calendar.MONTH)!!, calendar?.get(Calendar.DAY_OF_MONTH)!!)

        val maxDate: Calendar = Calendar.getInstance()
        maxDate.set(Calendar.getInstance().get(Calendar.YEAR), 11, 31, 23, 59, 59)

        datePickerDialog.datePicker.minDate = calendar!!.timeInMillis // minDate è il giorno corrente.
        datePickerDialog.datePicker.maxDate = maxDate.timeInMillis // maxDate è il 31 dicembre dell'anno corrente.
        isMinDate = true

        datePickerDialog.setCancelable(false)
        datePickerDialog.show()
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)?.setOnClickListener {
            Toast.makeText(context, "DatePickerDialog dismissed", Toast.LENGTH_SHORT).show()
            datePickerDialog.dismiss()
        }
    }

    private val datePickerDialogMaxDateClickListener = View.OnClickListener {

        val datePickerDialog = DatePickerDialog(context!!, R.style.PickerDialogTheme, this, calendar?.get(Calendar.YEAR)!!, calendar?.get(Calendar.MONTH)!!, calendar?.get(Calendar.DAY_OF_MONTH)!!)

        val minDate: Calendar = Calendar.getInstance()
        minDate.set(Calendar.getInstance().get(Calendar.YEAR), 0, 1, 0, 0, 0)

        datePickerDialog.datePicker.minDate = minDate.timeInMillis // minDate Ë il 1 gennaio dell'anno corrente.
        datePickerDialog.datePicker.maxDate = calendar!!.timeInMillis // maxDate Ë il giorno corrente.
        isMinDate = false

        datePickerDialog.setCancelable(false)
        datePickerDialog.show()
//        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)?.setOnClickListener {
//            Toast.makeText(context, "DatePickerDialog dismissed", Toast.LENGTH_SHORT).show()
//            datePickerDialog.dismiss()
//        }
    }

    override fun onDateSet(datePicker: DatePicker, year: Int, month: Int, day: Int) {

        pickerData = String.format("%02d/%02d/%04d", day, month + 1, year) // "dd/MM/yyyy"
        if (isMinDate!!)
            datepickerdialog_mindate!!.text = pickerData
        else
            datepickerdialog_maxdate!!.text = pickerData
    }

    private val currentTimePickerDialogClickListener = View.OnClickListener {
        calendar = Calendar.getInstance()
        val timePickerDialog = TimePickerDialog(context, R.style.PickerDialogTheme, { _, hourOfDay, minute ->

            startTime = String.format("%02d:%02d", hourOfDay, minute) + ":00"
            current_time_picker_label!!.text = startTime
        }, calendar!!.get(Calendar.HOUR_OF_DAY), calendar!!.get(Calendar.MINUTE), true)

        timePickerDialog.setCancelable(false)
        timePickerDialog.show()
        timePickerDialog.getButton(TimePickerDialog.BUTTON_NEGATIVE)?.setOnClickListener {
            Toast.makeText(context, "TimePickerDialog dismissed", Toast.LENGTH_SHORT).show()
            timePickerDialog.dismiss()
        }
    }

    private val startTimePickerDialogClickListener = View.OnClickListener {
        calendar = Calendar.getInstance()
        val timePickerDialog = TimePickerDialog(context, R.style.PickerDialogTheme, { _, hourOfDay, minute ->

            startTime = String.format("%02d:%02d", hourOfDay, minute) + ":00"
            start_time_picker_label!!.text = startTime
        }, 9, 0, true)

        timePickerDialog.setCancelable(false)
        timePickerDialog.show()
    }

    private val rangeTimePickerDialogClickListener = View.OnClickListener {
        calendar = Calendar.getInstance()
        val startTimePickerDialog = TimePickerDialog(context, R.style.PickerDialogTheme, { _, startHourOfDay, startMinute ->

            startTime = String.format("%02d:%02d", startHourOfDay, startMinute) + ":00"

            val endTimePickerDialog = TimePickerDialog(context, R.style.PickerDialogTheme, { _, endHourOfDay, endMinute ->

                endTime = String.format("%02d:%02d", endHourOfDay, endMinute) + ":00"
                rangeTime = "Dalle $startTime alle $endTime"
                range_time_picker_label!!.text = rangeTime

            }, 18, 0, true)

            endTimePickerDialog.setCancelable(false)
            endTimePickerDialog.show()

        }, 9, 0, true)

        startTimePickerDialog.setCancelable(false)
        startTimePickerDialog.show()
    }

    @SuppressLint("InflateParams")
    private fun showDatePicker() {

        val builder = AlertDialog.Builder(context!!)
        builder.setCancelable(false)
        val inflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.datepicker_single_date, null)
        builder.setView(view)
        val dialog = builder.create()
        if (dialog.window != null) {
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window!!.setBackgroundDrawableResource(R.color.white)
        }

        // view.date.maxDate = calendar!!.timeInMillis
        // view.date.minDate = calendar!!.timeInMillis

        view.date_picker.setOnDateChangedListener { _, year, monthOfYear, dayOfMonth ->
            giorno = dayOfMonth
            mese = monthOfYear + 1
            anno = year
        }
        view.back.setOnClickListener {
            dialog.dismiss()
        }
        view.ok.setOnClickListener {
            val result = "$giorno/$mese/$anno"
            date_picker.text = result
            dialog.dismiss()
        }
        dialog.show()
    }

    @SuppressLint("InflateParams")
    private fun showSingleTimePicker() {

        val builder = AlertDialog.Builder(context!!)
        builder.setCancelable(false)
        val inflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.timepicker_single_time, null)
        builder.setView(view)
        val dialog = builder.create()
        if (dialog.window != null) {
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window!!.setBackgroundDrawableResource(R.color.white)
        }

        calendar = Calendar.getInstance()
        startTime = String.format("%02d:%02d", calendar?.get(Calendar.HOUR_OF_DAY), calendar?.get(Calendar.MINUTE))
        view.time_value.text = startTime

        view.time_picker.setIs24HourView(true)
        view.time_picker.setOnTimeChangedListener { _, hourOfDay, minute ->
            startTime = String.format("%02d:%02d", hourOfDay, minute) + ":00"
            view.time_value.text = String.format("%02d:%02d", hourOfDay, minute)
        }
        view.back.setOnClickListener {
            dialog.dismiss()
        }
        view.ok.setOnClickListener {
            single_time_picker.text = startTime
            dialog.dismiss()
        }
        dialog.show()
    }

    @SuppressLint("InflateParams")
    private fun showRangeTimePicker1() {

        val builder = AlertDialog.Builder(context!!)
        builder.setCancelable(false)
        val inflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.timepicker_range_time, null)
        builder.setView(view)
        val dialog = builder.create()
        if (dialog.window != null) {
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window!!.setBackgroundDrawableResource(R.color.white)
        }

        calendar = Calendar.getInstance()
        startTime = String.format("%02d:%02d:%02d", 9, 0, 0)
        endTime = String.format("%02d:%02d:%02d", 18, 0, 0)
        view.start_time_value.text = String.format("%02d:%02d", 9, 0)
        view.end_time_value.text = String.format("%02d:%02d", 18, 0)
        rangeTime = "Dalle $startTime alle $endTime"

        view.start_time_picker.setIs24HourView(true)
        view.start_time_picker.hour = 9
        view.start_time_picker.minute = 0
        setMinutePicker(view.start_time_picker)
        view.end_time_picker.setIs24HourView(true)
        view.end_time_picker.hour = 18
        view.end_time_picker.minute = 0
        setMinutePicker(view.end_time_picker)

        var startHour: Int? = 9
        var startMinute: Int? = 0
        var endHour: Int? = 18
        var endMinute: Int? = 0

        view.start_time_picker.setOnTimeChangedListener { _, hourOfDay, minute ->

            view.start_time_value.text = String.format("%02d:%02d", hourOfDay, minute * 15)

            startTime = String.format("%02d:%02d", hourOfDay, minute * 15) + ":00"
            rangeTime = "Dalle $startTime alle $endTime"

            startHour = hourOfDay
            startMinute = minute
        }
        view.end_time_picker.setOnTimeChangedListener { _, hourOfDay, minute ->

            view.end_time_value.text = String.format("%02d:%02d", hourOfDay, minute * 15)

            endTime = String.format("%02d:%02d", hourOfDay, minute * 15) + ":00"
            rangeTime = "Dalle $startTime alle $endTime"

            endHour = hourOfDay
            endMinute = minute
        }
        view.back_label.setOnClickListener {
            dialog.dismiss()
        }
        view.ok_label.setOnClickListener {

            if (startHour!! > endHour!! || (startHour == endHour && startMinute!! >= endMinute!!)) {

                showPopupError("Intervallo di tempo non valido.", View.OnClickListener {
                    popupError!!.dismiss()
                })
            } else
                range_time_picker_1.text = rangeTime

            dialog.dismiss()
        }
        view.start_time_container.setOnClickListener {
            view.start_time_picker.visibility = View.VISIBLE
            view.end_time_picker.visibility = View.GONE
            view.start_tab_indicator.visibility = View.VISIBLE
            view.end_tab_indicator.visibility = View.GONE
            view.inizio.alpha = 1F
            view.fine.alpha = 0.4F
        }
        view.end_time_container.setOnClickListener {
            view.start_time_picker.visibility = View.GONE
            view.end_time_picker.visibility = View.VISIBLE
            view.start_tab_indicator.visibility = View.GONE
            view.end_tab_indicator.visibility = View.VISIBLE
            view.inizio.alpha = 0.4F
            view.fine.alpha = 1F
        }
        dialog.show()
    }

    private fun setMinutePicker(picker: TimePicker) {
        val numValues = 60 / interval
        val displayedValues = arrayOfNulls<String>(numValues)
        for (i in 0 until numValues) {
            displayedValues[i] = formatter.format(i * interval)
        }

        val minute = picker.findViewById<View>(Resources.getSystem().getIdentifier("minute", "id", "android"))
        if (minute != null && minute is NumberPicker) {
            minutePicker = minute
            minutePicker!!.minValue = 0
            minutePicker!!.maxValue = numValues - 1
            minutePicker!!.displayedValues = displayedValues
        }
    }
}