package it.giovanni.arkivio.model.advertising

import android.os.Parcel
import android.os.Parcelable

data class Tracking(var adIdentifier: String, var clientIdentifier: String, var events: List<Event>) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createTypedArrayList(Event)!!
    )

    class Builder {
        private var adIdentifier: String? = null
        private var clientIdentifier: String? = null
        private var events = mutableListOf<Event>()

        fun adIdentifier(value: String?) = apply { this.adIdentifier = value }
        fun clientIdentifier(value: String?) = apply { this.clientIdentifier = value }
        fun addEvent(event: Event) = apply { this.events.add(event) }
        fun addEvents(events: List<Event>) = apply { this.events.addAll(events) }

        fun build(): Tracking? {
            return if (adIdentifier != null && clientIdentifier != null) {
                Tracking(adIdentifier!!, clientIdentifier!!, events.toList())
            } else null
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(adIdentifier)
        parcel.writeString(clientIdentifier)
        parcel.writeTypedList(events)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Tracking> {
        override fun createFromParcel(parcel: Parcel): Tracking {
            return Tracking(parcel)
        }

        override fun newArray(size: Int): Array<Tracking?> {
            return arrayOfNulls(size)
        }
    }
}