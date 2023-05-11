package it.giovanni.arkivio.fragments.detail.puntonet.getpost

import android.os.Parcel
import android.os.Parcelable

/**
 * Questa classe rappresenta un data item di tutti i team NBA. Implementa l'interfaccia Parcelable
 * che ne consente il passaggio tra i componenti di un'applicazione Android utilizzando un Intent.
 *
 * La classe ha sette proprietà, corrispondenti ai sette attributi di un team NBA:
 *
 * - id: integer che rappresenta l'identificatore univoco del team.
 * - abbreviation: stringa che rappresenta l'abbreviazione del team.
 * - city: stringa che rappresenta la città del team.
 * - conference: stringa che rappresenta la conference a cui appartiene il team (Eastern or Western).
 * - division: stringa che rappresenta la divisione a cui appartiene la squadra
 *   (Atlantic, Central, Southeast, Northwest, Pacific o Southwest).
 * - full_name: stringa che rappresenta il nome completo del team.
 * - name: stringa che rappresenta il nome breve del team.
 *
 * La classe ha un costruttore primario che accetta valori per tutte queste proprietà. Ha anche un
 * costruttore secondario che accetta un oggetto Parcel che viene utilizzato per creare un'istanza
 * della classe da un modulo serializzato. Il metodo writeToParcel viene utilizzato per scrivere i
 * dati dell'oggetto in un Parcel e il metodo createFromParcel viene utilizzato per creare un
 * oggetto da un Parcel.
 *
 * L'oggetto CREATOR è un oggetto speciale che implementa l'interfaccia Parcelable.Creator. Viene
 * utilizzato per creare istanze della classe ListUsersDataItem da un oggetto Parcel. Il metodo
 * newArray viene utilizzato per creare un array della classe ListUsersDataItem, utilizzato dal
 * sistema per contenere più istanze della classe.
 */

class ListUsersDataItem(

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

    companion object CREATOR : Parcelable.Creator<ListUsersDataItem> {
        override fun createFromParcel(parcel: Parcel): ListUsersDataItem {
            return ListUsersDataItem(parcel)
        }

        override fun newArray(size: Int): Array<ListUsersDataItem?> {
            return arrayOfNulls(size)
        }
    }
}