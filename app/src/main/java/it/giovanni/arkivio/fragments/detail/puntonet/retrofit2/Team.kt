package it.giovanni.arkivio.fragments.detail.puntonet.retrofit2

import com.google.gson.annotations.SerializedName

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