package it.giovanni.kotlin

import android.app.IntentService
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import it.giovanni.kotlin.activities.DeepLinkActivity.Companion.TAG
import it.giovanni.kotlin.activities.MainActivity

class LocalNotificationService : IntentService("LocalNotificationService") {

    private var builder: NotificationCompat.Builder? = null
    private val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private var notificationManager: NotificationManagerCompat? = null
    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (BluetoothDevice.ACTION_FOUND == action) {
                val rssi = intent.getShortExtra(
                    BluetoothDevice.EXTRA_RSSI,
                    Short.MIN_VALUE
                ).toInt()
                val name = intent.getStringExtra(BluetoothDevice.EXTRA_NAME)
                val messageRSSI = name + " => " + rssi + "dBm"
                Log.i(TAG, messageRSSI)

                // notificationId is a unique int for each notification that you must define
                notificationManager?.notify(1, builder!!.build())
            }
        }
    }

    /**
     * The IntentService calls this method from the default worker thread with
     * the intent that started the service. When this method returns, IntentService
     * stops the service, as appropriate.
     */
    override fun onHandleIntent(intent: Intent?) {
        // Normally we would do some work here, like download a file.
        // For our sample, we just sleep for 5 seconds.
        try {
            Thread.sleep(5000)
        } catch (e: InterruptedException) {
            // Restore interrupt status.
            Thread.currentThread().interrupt()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        createNotificationChannel()

        // Bluetooth
        registerReceiver(receiver, IntentFilter(BluetoothDevice.ACTION_FOUND))

        // Create an explicit intent for an Activity in your app

        // PRIMO MODO
        /*
        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        */

        // SECONDO MODO
        val pendingIntent = Intent(this, MainActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent, 0)
        }

        val logo : Bitmap = BitmapFactory.decodeResource(App.context.resources, R.mipmap.logo_audioslave_light_blue)
        val safetyDistance : Bitmap = BitmapFactory.decodeResource(App.context.resources, R.drawable.safety_distance)
        val uri = Uri.parse("android.resource://" + App.context.packageName + "/" + R.raw.intro)
        val vibrate = longArrayOf(0, 100, 200, 300)

        // Local notification
        builder = NotificationCompat.Builder(this, "channelId")
            .setSmallIcon(R.mipmap.logo_audioslave_light_blue)
            .setContentTitle("Covid-19 Alert!")
            .setContentText("Sei troppo vicino al tuo collega, allontanati!")
            // .setStyle(NotificationCompat.BigTextStyle().bigText("Ti sei avvicinato troppo ad un tuo collega, allontanati!"))
            .setLargeIcon(logo)
            .setStyle(NotificationCompat.BigPictureStyle().bigPicture(safetyDistance))
            // .setStyle(NotificationCompat.BigPictureStyle().bigPicture(safetyDistance).bigLargeIcon(null))
            // .setStyle(NotificationCompat.InboxStyle().addLine("messageSnippet1").addLine("messageSnippet2"))
            .setPriority(NotificationCompat.PRIORITY_MAX) // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            // .setSound(uri)
            // .setVibrate(vibrate)
            // .setDefaults(Notification.DEFAULT_SOUND)
            // .setDefaults(Notification.DEFAULT_VIBRATE)
            .setAutoCancel(true)

        notificationManager = NotificationManagerCompat.from(this)
        bluetoothAdapter.startDiscovery()

        return super.onStartCommand(intent, flags, startId)
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "channelId"
            val name: CharSequence = getString(R.string.app_name)
            val description = getString(R.string.app_name)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, name, importance)
            channel.description = description
            // Register the channel with the system; you can't change the importance or other notification behaviors after this
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}