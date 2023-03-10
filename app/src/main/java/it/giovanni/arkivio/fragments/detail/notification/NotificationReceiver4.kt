package it.giovanni.arkivio.fragments.detail.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationReceiver4: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.startForegroundService(Intent(context, NotificationService4::class.java))
    }
}