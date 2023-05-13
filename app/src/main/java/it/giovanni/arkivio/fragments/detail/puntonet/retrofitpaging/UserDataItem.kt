package it.giovanni.arkivio.fragments.detail.puntonet.retrofitpaging

import android.os.Parcel
import android.os.Parcelable

/**
 * Questa classe rappresenta un data item di tutti gli users. Implementa l'interfaccia Parcelable
 * che ne consente il passaggio tra i componenti di un'applicazione Android utilizzando un Intent.
 *
 * La classe ha cinque proprietà, corrispondenti ai cinque attributi di un user e ha un costruttore
 * primario che accetta valori per tutte queste proprietà. Ha anche un costruttore secondario che
 * accetta un oggetto Parcel che viene utilizzato per creare un'istanza della classe da un modulo
 * serializzato. Il metodo writeToParcel viene utilizzato per scrivere i dati dell'oggetto in un
 * Parcel e il metodo createFromParcel viene utilizzato per creare un oggetto da un Parcel.
 *
 * L'oggetto CREATOR è un oggetto speciale che implementa l'interfaccia Parcelable.Creator. Viene
 * utilizzato per creare istanze della classe UserDataItem da un oggetto Parcel. Il metodo
 * newArray viene utilizzato per creare un array della classe UserDataItem, utilizzato dal
 * sistema per contenere più istanze della classe.
 */

class UserDataItem(
    val id: Int,
    val email: String?,
    val firstName: String?,
    val lastName: String?,
    val avatar: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(email)
        parcel.writeString(firstName)
        parcel.writeString(lastName)
        parcel.writeString(avatar)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserDataItem> {
        override fun createFromParcel(parcel: Parcel): UserDataItem {
            return UserDataItem(parcel)
        }

        override fun newArray(size: Int): Array<UserDataItem?> {
            return arrayOfNulls(size)
        }
    }
}