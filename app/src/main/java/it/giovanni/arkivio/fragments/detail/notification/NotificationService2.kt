package it.giovanni.arkivio.fragments.detail.notification

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import androidx.core.app.AlarmManagerCompat
import androidx.core.app.NotificationCompat
import it.giovanni.arkivio.R

class NotificationService2 : Service() {

    private companion object {
        private var TAG: String = NotificationService2::class.java.simpleName
        private const val SECOND: Long = 1_000L
        private const val REQUEST_CODE = 2 // NOTIFICATION ID
    }

    private var notificationManager: NotificationManager? = null
    private lateinit var notifyPendingIntent: PendingIntent
    private lateinit var notifyIntent: Intent
    private var counter = 0

    override fun onCreate() {
        super.onCreate()

        notificationManager = getSystemService(NotificationManager::class.java)

        notifyIntent = Intent(this, NotificationReceiver2::class.java)
        notifyPendingIntent = PendingIntent.getBroadcast(application, REQUEST_CODE, notifyIntent, PendingIntent.FLAG_IMMUTABLE)
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

    private fun createNotificationChannel() {

        val notificationChannel = NotificationChannel(
            getString(R.string.egg_time_channel_id_2),
            getString(R.string.egg_time_channel_name_2),
            NotificationManager.IMPORTANCE_HIGH)

        notificationChannel.setShowBadge(true)
        notificationChannel.lightColor = Color.BLUE
        notificationChannel.enableLights(true)
        notificationChannel.enableVibration(true)
        notificationChannel.description = "Time for breakfast"

        // val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager?.createNotificationChannel(notificationChannel)
    }

    // Creates a new alarm, notification and timer.
    private fun startTimer() {

        val selectedInterval = SECOND * 360
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

        Log.i(TAG, "counter: " + counter++)
    }

    private fun NotificationManager.sendNotification(context: Context) {

        // Create the content intent for the notification, which launches this activity
        val intent = Intent(context, NotificationFragment::class.java)

        // You created the intent, but the notification is displayed outside your app.
        // To make an intent work outside your app, you need to create a new PendingIntent.
        val pendingIntent = PendingIntent.getActivity(context, REQUEST_CODE, intent, PendingIntent.FLAG_IMMUTABLE)

        // Add style
        val eggImage = BitmapFactory.decodeResource(context.resources, R.drawable.cooked_egg)
        val bigPictureStyle = NotificationCompat.BigPictureStyle().bigPicture(eggImage).bigLargeIcon(eggImage)

        /*
        Get an instance of NotificationCompat.Builder
        Starting with API level 26, all notifications must be assigned to a channel.
        Notification Channels are a way to group notifications. By grouping together similar types
        of notifications, developers and users can control all of the notifications in the channel.
        Once a channel is created, it can be used to deliver any number of notifications.
        */

        // Build the notification
        val builder = NotificationCompat.Builder(context,
            context.getString(R.string.egg_time_channel_id_2))
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

        val notification: Notification = builder.build()
        notify(REQUEST_CODE, notification)
        startForeground(REQUEST_CODE, notification)
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
}