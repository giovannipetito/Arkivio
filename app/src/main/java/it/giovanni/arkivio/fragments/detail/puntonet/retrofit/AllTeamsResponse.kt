package it.giovanni.arkivio.fragments.detail.puntonet.retrofit

import com.google.gson.annotations.SerializedName

/**
 * Questa è una data class che rappresenta la response di una chiamata API per ottenere una lista
 * di team di basket. Utilizza l'annotazione @SerializedName della libreria Gson per mappare le
 * chiavi JSON alle properties della classe.
 *
 * La classe AllTeamsResponse ha due classi interne, Team e Meta, che rappresentano rispettivamente
 * l'oggetto team e le meta informazioni. La property teams è una lista di oggetti Team, mentre la
 * property meta è un'istanza della classe Meta.
 *
 * Ogni property nella classe Team è annotata con @SerializedName ed è mappata a una chiave nella
 * response JSON. La classe Meta ha properties che rappresentano i metadati per l'impaginazione come
 * total_pages, current_page, next_page, per_page e total_count.
 *
 * Questa classe viene in genere utilizzata con Retrofit per deserializzare la response JSON in un
 * oggetto Kotlin. L'annotazione @SerializedName viene utilizzata per mappare le chiavi JSON alla
 * property corrispondente nella classe.
 */
class AllTeamsResponse {

    @SerializedName("data")
    var teams: List<Team>? = null

    @SerializedName("meta")
    var meta: Meta? = null

    class Team {

        @SerializedName("id")
        var id: Int? = null

        @SerializedName("abbreviation")
        var abbreviation: String? = null

        @SerializedName("city")
        var city: String? = null

        @SerializedName("conference")
        var conference: String? = null

        @SerializedName("division")
        var division: String? = null

        @SerializedName("full_name")
        var full_name: String? = null

        @SerializedName("name")
        var name: String? = null
    }

    class Meta {

        @SerializedName("total_pages")
        var total_pages: Int? = null

        @SerializedName("current_page")
        var current_page: Int? = null

        @SerializedName("next_page")
        var next_page: Int? = null

        @SerializedName("per_page")
        var per_page: Int? = null

        @SerializedName("total_count")
        var total_count: Int? = null
    }
}