package it.giovanni.arkivio.fragments.detail.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.annotation.NonNull

class NotificationReceiver2: BroadcastReceiver() {

    override fun onReceive(@NonNull context: Context?, @NonNull intent: Intent?) {
        context?.startForegroundService(Intent(context, NotificationService2::class.java))
    }
}