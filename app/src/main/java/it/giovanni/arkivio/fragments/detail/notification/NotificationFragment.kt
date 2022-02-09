package it.giovanni.arkivio.fragments.detail.notification

import android.app.*
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.AlarmManagerCompat
import androidx.core.content.ContextCompat
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.NotificationLayoutBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter

class NotificationFragment: DetailFragment() {

    private var layoutBinding: NotificationLayoutBinding? = null
    private val binding get() = layoutBinding

    private lateinit var notifyPendingIntent: PendingIntent
    private lateinit var notifyIntent: Intent

    private companion object {
        private const val SECOND: Long = 1_000L
        private const val REQUEST_CODE = 1
    }

    override fun getTitle(): Int {
        return R.string.notification_title
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

    override fun onCreateBindingView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        layoutBinding = NotificationLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this, requireContext())
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val notificationItem1 = binding?.notificationItem1
        val notificationItem2 = binding?.notificationItem2
        val notificationItem3 = binding?.notificationItem3
        val notificationItem4 = binding?.notificationItem4

        val serviceText1 = notificationItem1?.serviceText
        val serviceText2 = notificationItem2?.serviceText
        val serviceText3 = notificationItem3?.serviceText
        val serviceText4 = notificationItem4?.serviceText

        serviceText1?.setText(R.string.service_1)
        serviceText2?.setText(R.string.service_2)
        serviceText3?.setText(R.string.service_3)
        serviceText4?.setText(R.string.service_4)

        val serviceSwitch1 = notificationItem1?.serviceSwitch
        val serviceSwitch2 = notificationItem2?.serviceSwitch
        val serviceSwitch3 = notificationItem3?.serviceSwitch
        val serviceSwitch4 = notificationItem4?.serviceSwitch

        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled) {
            // launcher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)) // todo: da testare.
            startActivityForResult(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),1)
        }

        serviceSwitch1?.setOnClickListener {
            if (serviceSwitch1.isChecked) {
                serviceSwitch1.setText(R.string.stop_service)

                notifyIntent = Intent(context, NotificationReceiver1::class.java)
                notifyPendingIntent = PendingIntent.getBroadcast(currentActivity.application, REQUEST_CODE, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                createChannel()
                startTimer()
            } else {
                serviceSwitch1.setText(R.string.start_service)
            }
        }
        serviceSwitch2?.setOnClickListener {
            if (serviceSwitch2.isChecked) {
                serviceSwitch2.setText(R.string.stop_service)

                startNotificationService2()
            }
            else {
                serviceSwitch2.setText(R.string.start_service)
            }
        }
        serviceSwitch3?.setOnClickListener {
            if (serviceSwitch3.isChecked) {
                serviceSwitch3.setText(R.string.stop_service)

                startNotificationService3()
            }
            else {
                serviceSwitch3.setText(R.string.start_service)
            }
        }
        serviceSwitch4?.setOnClickListener {
            if (serviceSwitch4.isChecked) {
                serviceSwitch4.setText(R.string.stop_service)

                startNotificationService4()
            }
            else {
                serviceSwitch4.setText(R.string.start_service)
            }
        }
    }

    private fun createChannel() {

        val notificationChannel = NotificationChannel(
            getString(R.string.egg_time_channel_id_1),
            getString(R.string.egg_time_channel_name_1),
            NotificationManager.IMPORTANCE_HIGH)

        notificationChannel.setShowBadge(true)
        notificationChannel.lightColor = Color.BLUE
        notificationChannel.enableLights(true)
        notificationChannel.enableVibration(true)
        notificationChannel.description = "Time for breakfast"

        val notificationManager = currentActivity.getSystemService(NotificationManager::class.java)
        notificationManager?.createNotificationChannel(notificationChannel)
    }

    // Creates a new alarm, notification and timer.
    private fun startTimer() {

        val selectedInterval = SECOND * 3
        val triggerTime = SystemClock.elapsedRealtime() + selectedInterval

        // Get an instance of NotificationManager.
        val notificationManager = ContextCompat.getSystemService(requireContext(), NotificationManager::class.java) as NotificationManager
        notificationManager.cancelNotifications()

        val alarmManager = currentActivity.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        AlarmManagerCompat.setExactAndAllowWhileIdle(
            alarmManager,
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            triggerTime,
            notifyPendingIntent
        )
    }

    private fun NotificationManager.cancelNotifications() {
        cancelAll()
    }

    private fun startNotificationService2() {
        val notificationService = NotificationService2()
        val intentService = Intent(context, notificationService::class.java)
        if (!isServiceRunning(notificationService::class.java)) {
            currentActivity.startService(intentService)
        }
    }

    private fun startNotificationService3() {
        val notificationService = NotificationService3()
        val intentService = Intent(context, notificationService::class.java)
        if (!isServiceRunning(notificationService::class.java)) {
            currentActivity.startService(intentService)
        }
    }

    private fun startNotificationService4() {
        val notificationService = NotificationService4()
        val intentService = Intent(context, notificationService::class.java)
        if (!isServiceRunning(notificationService::class.java)) {
            currentActivity.startService(intentService)
        }
    }

    private fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = currentActivity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                Log.i("Service status", "Running")
                return true
            }
        }
        Log.i("Service status", "Not running")
        return false
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        /*
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
        }
        */
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}