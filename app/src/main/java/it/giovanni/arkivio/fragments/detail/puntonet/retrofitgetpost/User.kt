package it.giovanni.arkivio.fragments.detail.puntonet.retrofitgetpost

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("name") val name: String,
    @SerializedName("job") val job: String,
)

class User2 {

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

class User3(name: String, job: String) {

    @SerializedName("name")
    var name: String? = name

    @SerializedName("job")
    var job: String? = job
}