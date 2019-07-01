package it.giovanni.kotlin.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import it.giovanni.kotlin.utils.DeepLinkDescriptor

class DeepLinkActivity : AppCompatActivity() {

    companion object {
        val DEEP_LINK = "DEEP_LINK"
        val DEEP_LINK_URI = "DEEP_LINK_URI"
        val TAG = "DeepLinkActivity"
    }

    private var broadcastManager: LocalBroadcastManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        broadcastManager = LocalBroadcastManager.getInstance(this)
        val data = intent.data
        if (data != null) {

            if (MainActivity.running) {
                // send broadcast
                val intentBroadcast = Intent(DeepLinkDescriptor.DEEP_LINK_ACTION)
                intentBroadcast.putExtra(DeepLinkDescriptor.DEEP_LINK_URI, intent.data)
                broadcastManager!!.sendBroadcast(intentBroadcast)
            } else {
                // open activity
                val intent = Intent(this@DeepLinkActivity, MainActivity::class.java)
                val extras = Bundle()
                extras.putString("action", DeepLinkDescriptor.DEEP_LINK_ACTION)
                extras.putParcelable(DeepLinkDescriptor.DEEP_LINK_URI, data)
                intent.putExtra(DeepLinkDescriptor.DEEP_LINK, extras)

                startActivity(intent)
            }
        }
        finish()
    }
}