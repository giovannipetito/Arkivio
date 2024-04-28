package it.giovanni.arkivio.fragments.detail.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import it.giovanni.arkivio.R

class NotificationReceiver1: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        // Add call to sendNotification
        val notificationManager = ContextCompat.getSystemService(context, NotificationManager::class.java) as NotificationManager

        // Builds and delivers the notification.
        notificationManager.sendNotification(context)
    }

    private fun NotificationManager.sendNotification(context: Context) {

        val notificationId = 1 // requestCode

        // Create the content intent for the notification, which launches this activity
        val intent = Intent(context, NotificationFragment::class.java)

        // You created the intent, but the notification is displayed outside your app.
        // To make an intent work outside your app, you need to create a new PendingIntent.
        val pendingIntent = PendingIntent.getActivity(context, notificationId, intent, PendingIntent.FLAG_IMMUTABLE)

        // Add style
        val eggImage = BitmapFactory.decodeResource(context.resources, R.drawable.cooked_egg)
        val bigPictureStyle = NotificationCompat.BigPictureStyle().bigPicture(eggImage).bigLargeIcon(eggImage)

        // Build the notification
        val builder = NotificationCompat.Builder(context,
            context.getString(R.string.egg_time_channel_id_1))
            .setSmallIcon(R.drawable.cooked_egg)
            .setStyle(bigPictureStyle)
            .setLargeIcon(eggImage)
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentText(context.getString(R.string.egg_ready))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        notify(notificationId, builder.build())
    }
}