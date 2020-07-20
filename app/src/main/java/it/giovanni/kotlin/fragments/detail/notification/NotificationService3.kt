package it.giovanni.kotlin.fragments.detail.notification

import android.annotation.SuppressLint
import android.app.*
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import androidx.annotation.NonNull
import androidx.core.app.AlarmManagerCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import it.giovanni.kotlin.R

class NotificationService3 : Service() {

    private var counter = 0

    private var notificationManager: NotificationManager? = null
    private lateinit var notifyPendingIntent: PendingIntent
    private lateinit var notifyIntent: Intent

    private val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    private companion object {
        private const val SECOND: Long = 1_000L
        private const val REQUEST_CODE = 3 // NOTIFICATION ID
    }

    @SuppressLint("ObsoleteSdkInt")
    override fun onCreate() {
        super.onCreate()

        notificationManager = if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.O))
            getSystemService(NotificationManager::class.java)
        else
            ContextCompat.getSystemService(this, NotificationManager::class.java) as NotificationManager

        notifyIntent = Intent(this, NotificationReceiver3::class.java)
        notifyPendingIntent = PendingIntent.getBroadcast(application, REQUEST_CODE, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        searchForBluetooth()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return if (intent == null)
            START_NOT_STICKY
        else {
            createNotificationChannel()
            startTimer()
            START_STICKY
        }
    }

    private fun searchForBluetooth() {
        registerReceiver(receiver, IntentFilter(BluetoothDevice.ACTION_FOUND))
        bluetoothAdapter.startDiscovery()
    }

    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {

        override fun onReceive(@NonNull context: Context?, @NonNull intent: Intent?) {
            val action = intent?.action
            if (BluetoothDevice.ACTION_FOUND == action) {
                val rssi = intent.getShortExtra(
                    BluetoothDevice.EXTRA_RSSI,
                    Short.MIN_VALUE
                ).toInt()

                val bluetoothName = intent.getStringExtra(BluetoothDevice.EXTRA_NAME)

                if (bluetoothName != null) {
                    Log.i("TAG_NOTIFY", bluetoothName + " ===> " + rssi + "dBm")
                    createNotificationChannel()
                    startTimer()
                }
            }
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                getString(R.string.egg_time_channel_id_3),
                getString(R.string.egg_time_channel_name_3),
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.setShowBadge(true)
            notificationChannel.lightColor = Color.BLUE
            notificationChannel.enableLights(true)
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Time for breakfast"

            // val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(notificationChannel)
        }
    }

    // Creates a new alarm, notification and timer.
    private fun startTimer() {

        val selectedInterval = SECOND * 3
        val triggerTime = SystemClock.elapsedRealtime() + selectedInterval

        // Get an instance of NotificationManager.
        // val notificationManager = ContextCompat.getSystemService(this, NotificationManager::class.java) as NotificationManager
        notificationManager?.cancelAll() // Cancel notifications.

        // Builds and delivers the notification.
        notificationManager?.sendNotification(applicationContext)

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        AlarmManagerCompat.setExactAndAllowWhileIdle(
            alarmManager,
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            triggerTime,
            notifyPendingIntent
        )

        Log.i("TAG_NOTIFY", "========== " + counter++ + " ==========")
    }

    private fun NotificationManager.sendNotification(context: Context) {

        // Create the content intent for the notification, which launches this activity
        val intent = Intent(context, NotificationFragment::class.java)

        // You created the intent, but the notification is displayed outside your app.
        // To make an intent work outside your app, you need to create a new PendingIntent.
        val pendingIntent = PendingIntent.getActivity(context, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        // Add style
        val eggImage = BitmapFactory.decodeResource(context.resources, R.drawable.cooked_egg)
        val bigPictureStyle = NotificationCompat.BigPictureStyle().bigPicture(eggImage).bigLargeIcon(null)

        /*
        Get an instance of NotificationCompat.Builder
        Starting with API level 26, all notifications must be assigned to a channel.
        Notification Channels are a way to group notifications. By grouping together similar types
        of notifications, developers and users can control all of the notifications in the channel.
        Once a channel is created, it can be used to deliver any number of notifications.
        */

        // Build the notification
        val builder = NotificationCompat.Builder(context,
            context.getString(R.string.egg_time_channel_id_3))
            .setSmallIcon(R.drawable.cooked_egg)
            .setStyle(bigPictureStyle)
            .setLargeIcon(eggImage)
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentText(context.getString(R.string.egg_ready))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        /*
        Call notify
        notificationId represents the current notification instance and is needed for updating or
        canceling this notification. Since your app will only have one active notification at a
        given time, you can use the same ID for all your notifications.
        */

        val notification: Notification? = builder.build()
        notify(REQUEST_CODE, notification)
        startForeground(REQUEST_CODE, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        // unregisterReceiver(receiver)
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
}