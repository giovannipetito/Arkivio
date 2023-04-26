package it.giovanni.arkivio.model.advertising

import android.os.Parcel
import android.os.Parcelable

data class Media(var durationInSeconds: Int, override var identifier: String, var url: String? = null) :
    AdvertisingMedia {

    class Builder() {
        constructor(other: Media) : this() {
            this.identifier = other.identifier
            this.url = other.url
            this.durationInSeconds = other.durationInSeconds
        }

        constructor(other: AdvertisingMedia) : this() {
            this.identifier = other.identifier
            if (other is Media) {
                this.url = other.url
                this.durationInSeconds = other.durationInSeconds
            }
        }

        private var durationInSeconds: Int? = null

        private var identifier: String? = null

        private var url: String? = null

        fun durationInSeconds(value: Int) = apply { this.durationInSeconds = value }

        fun identifier(value: String) = apply { this.identifier = value }

        fun url(value: String) = apply { this.url = value }

        fun build(): Media? {
            return if (null != durationInSeconds && null != identifier) {
                Media(durationInSeconds!!, identifier!!, url)
            } else null
        }
    }

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()
    )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
        parcel.writeInt(durationInSeconds)
        parcel.writeString(identifier)
        parcel.writeString(url)
    }

    companion object CREATOR : Parcelable.Creator<Media> {
        override fun createFromParcel(parcel: Parcel): Media {
            return Media(parcel)
        }

        override fun newArray(size: Int): Array<Media?> {
            return arrayOfNulls(size)
        }
    }
}