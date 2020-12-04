@file:Suppress("DEPRECATION")

package it.giovanni.arkivio.fragments.detail.nearby.beacons

import android.app.IntentService
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import androidx.core.app.NotificationCompat
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.messages.Message
import com.google.android.gms.nearby.messages.MessageListener
import it.giovanni.arkivio.R

class NearbyBeaconsService : IntentService("BackgroundSubscribeIntentService") {

    companion object {
        private const val MESSAGES_NOTIFICATION_ID = 1
        private const val NUM_MESSAGES_IN_NOTIFICATION = 5
    }

    override fun onCreate() {
        super.onCreate()
        updateNotification()
    }

    override fun onHandleIntent(intent: Intent?) {

        if (intent != null) {
            Nearby.Messages.handleIntent(intent, object : MessageListener() {
                override fun onFound(message: Message) {
                    NearbyBeaconsUtils.saveFoundMessage(applicationContext, message)
                    updateNotification()
                }

                override fun onLost(message: Message) {
                    NearbyBeaconsUtils.removeLostMessage(applicationContext, message)
                    updateNotification()
                }
            })
        }
    }

    private fun updateNotification() {
        val messages = NearbyBeaconsUtils.getCachedMessages(applicationContext)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val launchIntent = Intent(applicationContext, NearbyBeaconsFragment::class.java)
        launchIntent.action = Intent.ACTION_MAIN
        launchIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val contentTitle = getContentTitle(messages)
        val contentText = getContentText(messages)
        val notificationBuilder = NotificationCompat.Builder(this)
            .setSmallIcon(R.drawable.logo_audioslave)
            .setContentTitle(contentTitle)
            .setContentText(contentText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(contentText))
            .setOngoing(true)
            .setContentIntent(pendingIntent)

        notificationManager.notify(MESSAGES_NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun getContentTitle(messages: List<String?>): String {
        return when (messages.size) {
            0 -> resources.getString(R.string.scanning)
            1 -> resources.getString(R.string.one_message)
            else -> resources.getString(R.string.many_messages, messages.size)
        }
    }

    private fun getContentText(messages: List<String?>): String? {
        val newline = System.getProperty("line.separator")
        return if (newline != null) {
            if (messages.size < NUM_MESSAGES_IN_NOTIFICATION)
                TextUtils.join(newline, messages)
            else
                TextUtils.join(newline, messages.subList(0, NUM_MESSAGES_IN_NOTIFICATION)) + newline + "&#8230;"
        } else
            null
    }
}