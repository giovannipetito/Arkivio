package it.giovanni.arkivio.fragments.detail.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.NonNull

class NotificationReceiver2: BroadcastReceiver() {

    override fun onReceive(@NonNull context: Context?, @NonNull intent: Intent?) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            context?.startForegroundService(Intent(context, NotificationService2::class.java))
        else
            context?.startService(Intent(context, NotificationService2::class.java))
    }
}