package it.giovanni.arkivio.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import it.giovanni.arkivio.deeplink.DeepLinkDescriptor

class DeepLinkActivity : AppCompatActivity() {

    companion object {
        val TAG = DeepLinkActivity::class.java.simpleName
        const val DEEP_LINK = "DEEP_LINK"
        const val DEEP_LINK_URI = "DEEP_LINK_URI"
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