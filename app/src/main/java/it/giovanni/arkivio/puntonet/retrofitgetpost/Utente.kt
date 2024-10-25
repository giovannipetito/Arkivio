package it.giovanni.arkivio.puntonet.retrofitgetpost

import com.google.gson.annotations.SerializedName

data class Utente(
    @SerializedName("name") val name: String,
    @SerializedName("job") val job: String,
)

class Utente2 {

    constructor()

    constructor(name: String, job: String) {
        this.name = name
        this.job = job
    }

    @SerializedName("name")
    var name: String? = null

    @SerializedName("job")
    var job: String? = null
}

class Utente3(name: String, job: String) {

    @SerializedName("name")
    var name: String? = name

    @SerializedName("job")
    var job: String? = job
}