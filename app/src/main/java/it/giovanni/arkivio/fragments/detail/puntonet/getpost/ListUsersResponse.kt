package it.giovanni.arkivio.fragments.detail.puntonet.getpost

import com.google.gson.annotations.SerializedName

/**
 * Questa è una data class che rappresenta la response di una chiamata API per ottenere una lista
 * di team di basket. Utilizza l'annotazione @SerializedName della libreria Gson per mappare le
 * chiavi JSON alle properties della classe.
 *
 * La classe ListUsersResponse ha due classi interne, Data e Support, che rappresentano rispettivamente
 * l'oggetto team e le support informazioni. La property data è una lista di oggetti Data, mentre la
 * property support è un'istanza della classe Support.
 *
 * Ogni property nella classe Data è annotata con @SerializedName ed è mappata a una chiave nella
 * response JSON. La classe Support ha properties che rappresentano i metadati per l'impaginazione come
 * total_pages, current_page, next_page, per_page e total_count.
 *
 * Questa classe viene in genere utilizzata con Retrofit per deserializzare la response JSON in un
 * oggetto Kotlin. L'annotazione @SerializedName viene utilizzata per mappare le chiavi JSON alla
 * property corrispondente nella classe.
 */
class ListUsersResponse {

    @SerializedName("page")
    var page = 0

    @SerializedName("per_page")
    var perPage = 0

    @SerializedName("total")
    var total = 0

    @SerializedName("total_pages")
    var totalPages = 0

    @SerializedName("data")
    var data: List<Data>? = null

    @SerializedName("support")
    var support: Support? = null
}