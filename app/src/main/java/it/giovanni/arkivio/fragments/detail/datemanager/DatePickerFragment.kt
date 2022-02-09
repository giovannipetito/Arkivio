package it.giovanni.arkivio.fragments.detail.datemanager

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.DatePicker
import android.widget.NumberPicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.DatePickerLayoutBinding
import it.giovanni.arkivio.databinding.DatepickerSingleDateBinding
import it.giovanni.arkivio.databinding.TimepickerRangeTimeBinding
import it.giovanni.arkivio.databinding.TimepickerSingleTimeBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter
import it.giovanni.arkivio.utils.DateManager
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class DatePickerFragment : DetailFragment(), DatePickerDialog.OnDateSetListener {

    private var layoutBinding: DatePickerLayoutBinding? = null
    private val binding get() = layoutBinding

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

    override fun getTitle(): Int {
        return R.string.date_picker_title
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

    override fun isRefreshEnabled(): Boolean {
        return true
    }

    override fun refresh() {
        stopSwipeRefresh()
    }

    override fun onCreateBindingView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        layoutBinding = DatePickerLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this, requireContext())
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        calendar = Calendar.getInstance()

        startCurrentDate = DateManager(Date())
        endCurrentDate = DateManager(Date().time + (60 * 60 * 1000))

        val dataInizio = "06/02/1988 06:00:00"
        val dataFine = "06/02/1988 12:30:00"
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ITALY)
        startDate = DateManager(dateFormat.parse(dataInizio)!!)
        endDate = DateManager(dateFormat.parse(dataFine)!!)

        timeDate = DateManager(dateFormat.parse(dataInizio)!!)
        timeDate?.setTimeDate(currentHours, currentMinutes)

        binding?.datepickerdialogMindate?.setOnClickListener(datePickerDialogMinDateClickListener)
        binding?.datepickerdialogMaxdate?.setOnClickListener(datePickerDialogMaxDateClickListener)

        binding?.currentTimePickerLabel?.setOnClickListener(currentTimePickerDialogClickListener)

        binding?.startTimePickerLabel?.setOnClickListener(startTimePickerDialogClickListener)

        binding?.rangeTimePickerLabel?.setOnClickListener(rangeTimePickerDialogClickListener)

        // CUSTOM PICKERS

        giorno = calendar?.get(Calendar.DAY_OF_MONTH)
        mese = calendar?.get(Calendar.MONTH)!! + 1
        anno = calendar?.get(Calendar.YEAR)

        binding?.datePicker?.setOnClickListener {
            showDatePicker()
        }

        binding?.singleTimePicker?.setOnClickListener {
            showSingleTimePicker()
        }

        binding?.rangeTimePicker1?.setOnClickListener {
            showRangeTimePicker1()
        }

        binding?.rangeTimePicker2?.setOnClickListener {
            showRangeTimePicker2()
        }

        binding?.scrollContainer?.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            detailLayoutBinding?.swipeRefreshLayout?.isEnabled = scrollY == 0
        }

        detailLayoutBinding?.swipeRefreshLayout?.setOnRefreshListener {
            refresh()
        }
    }

    private val datePickerDialogMinDateClickListener = View.OnClickListener {

        val datePickerDialog = DatePickerDialog(requireContext(), R.style.PickerDialogTheme, this, calendar?.get(Calendar.YEAR)!!, calendar?.get(Calendar.MONTH)!!, calendar?.get(Calendar.DAY_OF_MONTH)!!)

        val maxDate: Calendar = Calendar.getInstance()
        maxDate.set(Calendar.getInstance().get(Calendar.YEAR), 11, 31, 23, 59, 59)

        datePickerDialog.datePicker.minDate = calendar?.timeInMillis!! // minDate è il giorno corrente.
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

        val datePickerDialog = DatePickerDialog(requireContext(), R.style.PickerDialogTheme, this, calendar?.get(Calendar.YEAR)!!, calendar?.get(Calendar.MONTH)!!, calendar?.get(Calendar.DAY_OF_MONTH)!!)

        val minDate: Calendar = Calendar.getInstance()
        minDate.set(Calendar.getInstance().get(Calendar.YEAR), 0, 1, 0, 0, 0)

        datePickerDialog.datePicker.minDate = minDate.timeInMillis // minDate è il 1 gennaio dell'anno corrente.
        datePickerDialog.datePicker.maxDate = calendar?.timeInMillis!! // maxDate è il giorno corrente.
        isMinDate = false

        datePickerDialog.setCancelable(false)
        datePickerDialog.show()
        /*
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)?.setOnClickListener {
            Toast.makeText(context, "DatePickerDialog dismissed", Toast.LENGTH_SHORT).show()
            datePickerDialog.dismiss()
        }
        */
    }

    override fun onDateSet(datePicker: DatePicker, year: Int, month: Int, day: Int) {

        pickerData = String.format("%02d/%02d/%04d", day, month + 1, year) // "dd/MM/yyyy"
        if (isMinDate!!)
            binding?.datepickerdialogMindate?.text = pickerData
        else
            binding?.datepickerdialogMaxdate?.text = pickerData
    }

    private val currentTimePickerDialogClickListener = View.OnClickListener {
        calendar = Calendar.getInstance()
        val timePickerDialog = TimePickerDialog(context, R.style.PickerDialogTheme, { _, hourOfDay, minute ->

            startTime = String.format("%02d:%02d", hourOfDay, minute) + ":00"
            binding?.currentTimePickerLabel?.text = startTime
        }, calendar?.get(Calendar.HOUR_OF_DAY)!!, calendar?.get(Calendar.MINUTE)!!, true)

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
            binding?.startTimePickerLabel?.text = startTime
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
                binding?.rangeTimePickerLabel?.text = rangeTime

            }, 18, 0, true)

            endTimePickerDialog.setCancelable(false)
            endTimePickerDialog.show()

        }, 9, 0, true)

        startTimePickerDialog.setCancelable(false)
        startTimePickerDialog.show()
    }

    private fun showDatePicker() {

        val builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(false)

        val datepickerBinding = DatepickerSingleDateBinding.inflate(layoutInflater)

        builder.setView(datepickerBinding.root)
        val dialog = builder.create()
        if (dialog.window != null) {
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window?.setBackgroundDrawableResource(R.color.white)
        }

        datepickerBinding.datePicker.setOnDateChangedListener { _, year, monthOfYear, dayOfMonth ->
            giorno = dayOfMonth
            mese = monthOfYear + 1
            anno = year
        }
        datepickerBinding.back.setOnClickListener {
            dialog.dismiss()
        }
        datepickerBinding.ok.setOnClickListener {
            val result = "$giorno/$mese/$anno"
            binding?.datePicker?.text = result
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showSingleTimePicker() {

        val builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(false)

        val timepickerBinding = TimepickerSingleTimeBinding.inflate(layoutInflater)

        builder.setView(timepickerBinding.root)
        val dialog = builder.create()
        if (dialog.window != null) {
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window?.setBackgroundDrawableResource(R.color.white)
        }

        calendar = Calendar.getInstance()
        startTime = String.format("%02d:%02d", calendar?.get(Calendar.HOUR_OF_DAY), calendar?.get(Calendar.MINUTE)) + ":00"
        timepickerBinding.timeValue.text = String.format("%02d:%02d", calendar?.get(Calendar.HOUR_OF_DAY), calendar?.get(Calendar.MINUTE))

        timepickerBinding.timePicker.setIs24HourView(true)
        timepickerBinding.timePicker.setOnTimeChangedListener { _, hourOfDay, minute ->
            startTime = String.format("%02d:%02d", hourOfDay, minute) + ":00"
            timepickerBinding.timeValue.text = String.format("%02d:%02d", hourOfDay, minute)
        }
        timepickerBinding.singleTimeAnnulla.setOnClickListener {
            dialog.dismiss()
        }
        timepickerBinding.singleTimeOk.setOnClickListener {
            binding?.singleTimePicker?.text = startTime
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showRangeTimePicker1() {

        val builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(false)

        val timepickerBinding = TimepickerRangeTimeBinding.inflate(layoutInflater)

        builder.setView(timepickerBinding.root)
        val dialog = builder.create()
        if (dialog.window != null) {
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window?.setBackgroundDrawableResource(R.color.white)
        }

        calendar = Calendar.getInstance()
        startCurrentDate = DateManager(Date())

        startTime = String.format("%02d:%02d:%02d", 9, 0, 0)
        endTime = String.format("%02d:%02d:%02d", 18, 0, 0)
        timepickerBinding.startTimeValue.text = String.format("%02d:%02d", 9, 0)
        timepickerBinding.endTimeValue.text = String.format("%02d:%02d", 18, 0)
        rangeTime = "Dalle $startTime alle $endTime"

        timepickerBinding.startTimePicker.setIs24HourView(true)
        timepickerBinding.startTimePicker.hour = 9
        timepickerBinding.startTimePicker.minute = 0
        setTimePickerInterval(timepickerBinding.startTimePicker)
        timepickerBinding.endTimePicker.setIs24HourView(true)
        timepickerBinding.endTimePicker.hour = 18
        timepickerBinding.endTimePicker.minute = 0
        setTimePickerInterval(timepickerBinding.endTimePicker)

        var startHour: Int? = 9
        var startMinute: Int? = 0
        var endHour: Int? = 18
        var endMinute: Int? = 0

        timepickerBinding.startTimePicker.setOnTimeChangedListener { _, hourOfDay, minute ->

            timepickerBinding.startTimeValue.text = String.format("%02d:%02d", hourOfDay, minute * 15)

            startTime = String.format("%02d:%02d", hourOfDay, minute * 15) + ":00"
            rangeTime = "Dalle $startTime alle $endTime"

            startHour = hourOfDay
            startMinute = minute
        }
        timepickerBinding.endTimePicker.setOnTimeChangedListener { _, hourOfDay, minute ->

            timepickerBinding.endTimeValue.text = String.format("%02d:%02d", hourOfDay, minute * 15)

            endTime = String.format("%02d:%02d", hourOfDay, minute * 15) + ":00"
            rangeTime = "Dalle $startTime alle $endTime"

            endHour = hourOfDay
            endMinute = minute
        }
        timepickerBinding.rangeTimeAnnulla.setOnClickListener {
            dialog.dismiss()
        }
        timepickerBinding.rangeTimeOk.setOnClickListener {

            if (startHour!! > endHour!! || (startHour == endHour && startMinute!! > endMinute!!)) {

                showPopupError("L'ora di fine non può essere precedente a quella di inizio.") {
                    popupError?.dismiss()
                }
            } else if (startHour!! > endHour!! || (startHour == endHour && startMinute!! == endMinute!!)) {

                showPopupError("L'ora di inizio non può coincidere con quella di fine.") {
                    popupError?.dismiss()
                }
            } else {
                binding?.rangeTimePicker1?.text = rangeTime
            }

            dialog.dismiss()
        }
        timepickerBinding.startTimeContainer.setOnClickListener {
            timepickerBinding.startTimePicker.visibility = View.VISIBLE
            timepickerBinding.endTimePicker.visibility = View.GONE
            timepickerBinding.startTabIndicator.visibility = View.VISIBLE
            timepickerBinding.endTabIndicator.visibility = View.GONE
            timepickerBinding.inizio.alpha = 1F
            timepickerBinding.fine.alpha = 0.4F
        }
        timepickerBinding.endTimeContainer.setOnClickListener {
            timepickerBinding.startTimePicker.visibility = View.GONE
            timepickerBinding.endTimePicker.visibility = View.VISIBLE
            timepickerBinding.startTabIndicator.visibility = View.GONE
            timepickerBinding.endTabIndicator.visibility = View.VISIBLE
            timepickerBinding.inizio.alpha = 0.4F
            timepickerBinding.fine.alpha = 1F
        }
        dialog.show()
    }

    private fun showRangeTimePicker2() {

        val builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(false)

        val timepickerBinding = TimepickerRangeTimeBinding.inflate(layoutInflater)

        builder.setView(timepickerBinding.root)
        val dialog = builder.create()
        if (dialog.window != null) {
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window?.setBackgroundDrawableResource(R.color.white)
        }

        calendar = Calendar.getInstance()
        startCurrentDate = DateManager(Date())

        startDate = DateManager(Date())
        endDate = DateManager(Date(Date().time + (60 * 60 * 1000)))

        initDate()
        // startDate?.setIntervalTimeDate(calendar?.get(Calendar.HOUR_OF_DAY)!!, calendar?.get(Calendar.MINUTE)!!)
        // endDate?.setIntervalTimeDate(calendar?.get(Calendar.HOUR_OF_DAY)!! + 1, calendar?.get(Calendar.MINUTE)!!)

        timepickerBinding.startTimeValue.text = startDate?.getFormatTime()
        timepickerBinding.endTimeValue.text = endDate?.getFormatTime()

        timepickerBinding.startTimePicker.setIs24HourView(true)
        timepickerBinding.startTimePicker.hour = startDate?.getDatePickerHour()!!
        timepickerBinding.startTimePicker.minute = startDate?.getDatePickerMinute()!!/15
        setTimePickerInterval(timepickerBinding.startTimePicker)

        timepickerBinding.endTimePicker.setIs24HourView(true)
        timepickerBinding.endTimePicker.hour = endDate?.getDatePickerHour()!!
        timepickerBinding.endTimePicker.minute = endDate?.getDatePickerMinute()!!/15
        setTimePickerInterval(timepickerBinding.endTimePicker)

        timepickerBinding.startTimePicker.setOnTimeChangedListener { _, hourOfDay, minute ->

            timepickerBinding.startTimeValue.text = String.format("%02d:%02d", hourOfDay, minute * 15)
            startDate?.setTimeDate(hourOfDay, minute * 15)
        }
        timepickerBinding.endTimePicker.setOnTimeChangedListener { _, hourOfDay, minute ->

            timepickerBinding.endTimeValue.text = String.format("%02d:%02d", hourOfDay, minute * 15)
            endDate?.setTimeDate(hourOfDay, minute * 15)
        }
        timepickerBinding.rangeTimeAnnulla.setOnClickListener {
            dialog.dismiss()
        }
        timepickerBinding.rangeTimeOk.setOnClickListener {

            if (startDate?.getFormatDate3() == startCurrentDate?.getFormatDate3()) {

                /*
                if (startDate?.getDatePickerHour()!! < startCurrentDate?.getDatePickerHour()!! ||
                    endDate?.getDatePickerHour()!! < startCurrentDate?.getDatePickerHour()!! ||
                    endDate?.getDatePickerHour()!! < startDate?.getDatePickerHour()!! ||
                    (startDate?.getDatePickerHour()!! == startCurrentDate?.getDatePickerHour()!! &&
                            startDate?.getDatePickerMinute()!! <= startCurrentDate?.getDatePickerMinute()!!) ||
                    (startDate?.getDatePickerHour() == endDate?.getDatePickerHour() &&
                            startDate?.getDatePickerMinute()!! >= endDate?.getDatePickerMinute()!!)) {

                    initDate()

                    showPopupError("Intervallo di tempo non valido.") {
                        popupError?.dismiss()
                    }
                } else {
                    val result = "Dalle " + startDate?.getFormatTime() + " alle " + endDate?.getFormatTime()
                    binding?.rangeTimePicker2?.text = result
                }
                */

                if (startDate?.getDatePickerHour()!! < startCurrentDate?.getDatePickerHour()!! ||
                    (startDate?.getDatePickerHour() == startCurrentDate?.getDatePickerHour() &&
                            startDate?.getDatePickerMinute()!! < startCurrentDate?.getDatePickerMinute()!!)) {

                    wrongTimeInterval("L'ora di inizio non può essere precedente a quella attuale.")
                }
                else if (startDate?.getDatePickerHour()!! > endDate?.getDatePickerHour()!! ||
                    (startDate?.getDatePickerHour() == endDate?.getDatePickerHour() &&
                            startDate?.getDatePickerMinute()!! > endDate?.getDatePickerMinute()!!)) {

                    wrongTimeInterval("L'ora di fine non può essere precedente a quella di inizio.")
                }
                else if (startDate?.getDatePickerHour() == endDate?.getDatePickerHour() &&
                    startDate?.getDatePickerMinute() == endDate?.getDatePickerMinute()) {

                    wrongTimeInterval("L'ora di inizio non può coincidere con quella di fine.")
                }
                else {
                    val result = "Dalle " + startDate?.getFormatTime() + " alle " + endDate?.getFormatTime()
                    binding?.rangeTimePicker2?.text = result
                }

            } else {

                if (startDate?.getDatePickerHour()!! > endDate?.getDatePickerHour()!! ||
                    (startDate?.getDatePickerHour() == endDate?.getDatePickerHour() &&
                            startDate?.getDatePickerMinute()!! > endDate?.getDatePickerMinute()!!)) {

                    wrongTimeInterval("L'ora di fine non può essere precedente a quella di inizio.")
                }
                else if (startDate?.getDatePickerHour() == endDate?.getDatePickerHour() &&
                    startDate?.getDatePickerMinute() == endDate?.getDatePickerMinute()) {

                    wrongTimeInterval("L'ora di inizio non può coincidere con quella di fine.")
                }
                else {
                    val result = "Dalle " + startDate?.getFormatTime() + " alle " + endDate?.getFormatTime()
                    binding?.rangeTimePicker2?.text = result
                }

                /*
                if (endDate?.getDatePickerHour()!! < startDate?.getDatePickerHour()!! ||
                    (startDate?.getDatePickerHour() == endDate?.getDatePickerHour() &&
                            startDate?.getDatePickerMinute()!! >= endDate?.getDatePickerMinute()!!)) {

                    initDate()

                    showPopupError("Intervallo di tempo non valido.") {
                        popupError?.dismiss()
                    }
                } else {
                    val result = "Dalle " + startDate?.getFormatTime() + " alle " + endDate?.getFormatTime()
                    binding?.rangeTimePicker2?.text = result
                }
                */
            }

            dialog.dismiss()
        }
        timepickerBinding.startTimeContainer.setOnClickListener {
            timepickerBinding.startTimePicker.visibility = View.VISIBLE
            timepickerBinding.endTimePicker.visibility = View.GONE
            timepickerBinding.startTabIndicator.visibility = View.VISIBLE
            timepickerBinding.endTabIndicator.visibility = View.GONE
            timepickerBinding.inizio.alpha = 1F
            timepickerBinding.fine.alpha = 0.4F
        }
        timepickerBinding.endTimeContainer.setOnClickListener {
            timepickerBinding.startTimePicker.visibility = View.GONE
            timepickerBinding.endTimePicker.visibility = View.VISIBLE
            timepickerBinding.startTabIndicator.visibility = View.GONE
            timepickerBinding.endTabIndicator.visibility = View.VISIBLE
            timepickerBinding.inizio.alpha = 0.4F
            timepickerBinding.fine.alpha = 1F
        }
        dialog.show()
    }

    private fun wrongTimeInterval(message: String) {
        initDate()
        showPopupError(message) {
            popupError?.dismiss()
        }
    }

    private fun initDate() {
        startDate?.setIntervalTimeDate(calendar?.get(Calendar.HOUR_OF_DAY)!!, calendar?.get(Calendar.MINUTE)!!)
        endDate?.setIntervalTimeDate(calendar?.get(Calendar.HOUR_OF_DAY)!! + 1, calendar?.get(Calendar.MINUTE)!!)
    }

    private fun setTimePickerInterval(picker: TimePicker) {
        val numValues = 60 / interval
        val displayedValues = arrayOfNulls<String>(numValues)
        for (i in 0 until numValues) {
            displayedValues[i] = formatter.format(i * interval)
        }

        val minute = picker.findViewById<View>(Resources.getSystem().getIdentifier("minute", "id", "android"))
        if (minute != null && minute is NumberPicker) {
            minutePicker = minute
            minutePicker?.minValue = 0
            minutePicker?.maxValue = numValues - 1
            minutePicker?.displayedValues = displayedValues
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}