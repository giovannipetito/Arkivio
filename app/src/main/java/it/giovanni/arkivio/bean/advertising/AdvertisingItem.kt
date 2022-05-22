package it.giovanni.arkivio.bean.advertising

import android.os.Parcel
import android.os.Parcelable

data class AdvertisingItem(var order: Int, var media: AdvertisingMedia, var tracking: Tracking, var bumper: Boolean) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readParcelable(AdvertisingMedia::class.java.classLoader)!!,
        parcel.readParcelable(Tracking::class.java.classLoader)!!,
        parcel.readByte() == 1.toByte()
    )

    class Builder() {
        constructor(item: AdvertisingItem) : this() {
            this.order = item.order
            this.media = item.media
            this.tracking = item.tracking
            this.bumper = item.bumper
        }

        var order: Int? = null

        var media: AdvertisingMedia? = null

        var tracking: Tracking? = null

        var bumper: Boolean = false

        fun order(value: Int?) = apply { this.order = value }

        fun media(value: AdvertisingMedia?) = apply { this.media = value }

        fun tracking(value: Tracking?) = apply { this.tracking = value }

        fun bumper(value: Boolean) = apply { this.bumper = value }

        fun build(): AdvertisingItem? {
            return if (null != order && null != media && null != tracking) {
                AdvertisingItem(order!!, media!!, tracking!!, bumper)
            } else null
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(order)
        parcel.writeParcelable(media, flags)
        parcel.writeParcelable(tracking, flags)
        parcel.writeByte(if (bumper) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AdvertisingItem> {
        override fun createFromParcel(parcel: Parcel): AdvertisingItem {
            return AdvertisingItem(parcel)
        }

        override fun newArray(size: Int): Array<AdvertisingItem?> {
            return arrayOfNulls(size)
        }
    }
}