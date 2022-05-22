package it.giovanni.arkivio.bean.advertising

import android.os.Parcel
import android.os.Parcelable

interface AdvertisingMedia : Parcelable {

    var identifier: String

    override fun writeToParcel(parcel: Parcel, flags: Int) {}

    override fun describeContents(): Int {
        return 0
    }
}