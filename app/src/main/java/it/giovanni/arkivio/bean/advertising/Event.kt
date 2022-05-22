package it.giovanni.arkivio.bean.advertising

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

data class Event(var type: String, var uri: String) : Parcelable {
    // it is only populated when type is 'progress'
    var offsetSeconds: Int = 0

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!
    ) {
        offsetSeconds = parcel.readInt()
    }

    class Builder {
        private var type: String? = null

        private var uri: String? = null

        private var offsetSeconds: Int = 0

        fun type(value: String) = apply { this.type = value }

        fun uri(value: String) = apply { this.uri = value }

        fun offset(seconds: Int) = apply { this.offsetSeconds = seconds }

        fun build(): Event? {
            return if (null != type && null != uri) {
                Event(type!!, uri!!).also { event -> event.offsetSeconds = offsetSeconds }
            } else null
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(type)
        parcel.writeString(uri)
        parcel.writeInt(offsetSeconds)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Event> {
        override fun createFromParcel(parcel: Parcel): Event {
            return Event(parcel)
        }

        override fun newArray(size: Int): Array<Event?> {
            return arrayOfNulls(size)
        }
    }

    @Suppress("unused")
    enum class GT12EventType(val value: String) : Serializable {
        CreativeView("creativeView"), // Sent when the view with the image (Pause Ad) is displayed
        Close("close"), // Sent when the view is closed: either the playback is resumed or the user has started some other playback or navigates to the UI
        OverlayViewDuration("overlayViewDuration"), // will be sent when the image/view is shown for 10 sec
        OtherAdInteraction("otherAdInteraction"), // will be sent when the image/view is shown for 120 sec
        FirstQuartile("firstQuartile"),
        Midpoint("midpoint"),
        ThirdQuartile("thirdQuartile"),
        Progress("progress"),
        Complete("complete"), // ??
        Error("error"); // when an error occurs, the error code that need to be sent are documented at https://wiki.swisscom.com/x/j8saGg.

        companion object {
            @JvmStatic
            fun fromString(input: String): GT12EventType? {
                return values().firstOrNull { it.name.equals(input, ignoreCase = true) }
            }
        }
    }
}