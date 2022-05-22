package it.giovanni.arkivio.bean.advertising

import android.os.Parcel
import android.os.Parcelable

data class Advertising(var items: List<AdvertisingItem>) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.createTypedArrayList(AdvertisingItem)!!) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(items)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Advertising> {
        override fun createFromParcel(parcel: Parcel): Advertising {
            return Advertising(parcel)
        }

        override fun newArray(size: Int): Array<Advertising?> {
            return arrayOfNulls(size)
        }
    }
}