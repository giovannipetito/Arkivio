package it.giovanni.arkivio.fragments.detail.nearby.search

import com.google.android.gms.nearby.messages.Message
import com.google.gson.Gson
import java.nio.charset.StandardCharsets

class DeviceMessage {

    private var mUUID: String? = null
    private var mMessageBody: String? = null

    private constructor(uuid: String, messageBody: String) {
        mUUID = uuid
        mMessageBody = messageBody
        // TODO: add other fields that must be included in the Nearby Message payload.
    }

    companion object {

        private val gson = Gson()

        fun newNearbyMessage(instanceId: String?, messageBody: String?): Message {
            val deviceMessage = DeviceMessage(instanceId!!, messageBody!!)
            return Message(gson.toJson(deviceMessage).toByteArray(StandardCharsets.UTF_8))
        }

        fun fromNearbyMessage(message: Message): DeviceMessage? {
            val nearbyMessageString = String(message.content).trim { it <= ' ' }
            return gson.fromJson(
                String(nearbyMessageString.toByteArray(StandardCharsets.UTF_8)),
                DeviceMessage::class.java
            )
        }
    }

    fun getMessageBody(): String? {
        return mMessageBody
    }
}