package it.giovanni.arkivio.fragments.detail.notification

import android.annotation.SuppressLint
import android.app.*
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.NonNull
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import it.giovanni.arkivio.App
import it.giovanni.arkivio.R
import it.giovanni.arkivio.activities.MainActivity
import java.util.*

class NotificationService4 : Service() {

    private var timer: Timer? = null
    private var counter = 0
    private val REQUEST_CODE = 3 // NOTIFICATION ID

    private var builder: NotificationCompat.Builder? = null

    private val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private var notificationManager: NotificationManagerCompat? = null

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
                    val notification: Notification? = builder?.build()
                    notificationManager?.notify(REQUEST_CODE, notification!!)
                    startForeground(REQUEST_CODE, notification)
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        startTimer()
    }

    override fun onStartCommand(@NonNull intent: Intent?, @NonNull flags: Int, @NonNull startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return if (intent == null)
            START_NOT_STICKY
        else {
            startTimer()
            START_STICKY
        }
    }

    private fun startTimer() {
        timer = Timer()
        val timerTask: TimerTask = object : TimerTask() {
            @SuppressLint("ObsoleteSdkInt")
            override fun run() {
                Log.i("TAG_NOTIFY", "========== " + counter++ + " ==========")
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                    createNotificationChannel()
                    startNotificationService()
                }
                else
                    startForeground(REQUEST_CODE, Notification())
            }
        }
        timer!!.schedule(timerTask, 2000, 2000)
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = getString(R.string.notification_channel_id)
            val name: CharSequence = getString(R.string.notification_channel_name)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, name, importance)
            channel.setShowBadge(true)
            channel.lightColor = Color.BLUE
            channel.enableLights(true)
            channel.enableVibration(true)
            channel.description = getString(R.string.notification_channel_description)
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    private fun startNotificationService() {
        // Bluetooth
        registerReceiver(receiver, IntentFilter(BluetoothDevice.ACTION_FOUND))

        val pendingIntent = PendingIntent.getActivity(this, REQUEST_CODE, Intent(this, MainActivity::class.java), 0)

        val logo : Bitmap = BitmapFactory.decodeResource(
            App.context.resources,
            R.mipmap.logo_audioslave_light_blue
        )
        val safetyDistance : Bitmap = BitmapFactory.decodeResource(
            App.context.resources,
            R.drawable.safety_distance
        )

        val channelId = getString(R.string.notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        builder = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Covid-19 Alert!")
            .setContentText("Sei troppo vicino al tuo collega, allontanati!")
            .setSmallIcon(R.mipmap.logo_audioslave_light_blue)
            .setLargeIcon(logo)
            .setStyle(NotificationCompat.BigPictureStyle().bigPicture(safetyDistance))
            .setPriority(NotificationCompat.PRIORITY_MAX) // Set the intent that will fire when the user taps the notification
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        notificationManager = NotificationManagerCompat.from(this)
        bluetoothAdapter.startDiscovery()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        unregisterReceiver(receiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
        stopTimertask()
        val broadcastIntent = Intent()
        broadcastIntent.action = "restartservice"
        broadcastIntent.setClass(this, NotificationReceiver4::class.java)
        stopForeground(true)
        this.sendBroadcast(broadcastIntent)
    }

    private fun stopTimertask() {
        if (timer != null) {
            timer!!.cancel()
            timer = null
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}