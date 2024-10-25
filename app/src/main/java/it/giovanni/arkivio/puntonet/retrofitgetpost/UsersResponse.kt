package it.giovanni.arkivio.puntonet.retrofitgetpost

import com.google.gson.annotations.SerializedName

/**
 * La classe UsersResponse rappresenta la response di una chiamata API per ottenere una lista di
 * utenti sotto forma di oggetto JSON. Viene utilizzata come wrapper per la lista di oggetti User
 * restituiti dalla chiamata API, permettendo a tale lista di essere facilmente utilizzata nel
 * codice dell'applicazione.
 *
 * Utilizza l'annotazione @SerializedName per mappare le chiavi JSON alle properties corrispondenti
 * della classe e implementa l'interfaccia Serializable della libreria gson per serializzare e
 * deserializzare la classe stessa in modo che possa essere passata tra diversi componenti di
 * un'applicazione Android.
 *
 * Ha due classi interne, User e Support, annotate rispettivamente dalla property data che indica
 * una lista di oggetti User, e dalla property support che indica un'istanza della classe Support.
 */
class UsersResponse {

    @SerializedName("page")
    var page = 0

    @SerializedName("per_page")
    var perPage = 0

    @SerializedName("total")
    var total = 0

    @SerializedName("total_pages")
    var totalPages = 0

    @SerializedName("data")
    var users: List<User>? = null
}

/*
class UsersResponse(
    @SerializedName("page")
    var page: Int,

    @SerializedName("per_page")
    var perPage: Int,

    @SerializedName("total")
    var total: Int,

    @SerializedName("total_pages")
    var totalPages: Int,

    @SerializedName("data")
    var users: List<User> = null,
): Serializable
*/