package it.giovanni.arkivio.fragments.detail.puntonet.getpost

import com.google.gson.annotations.SerializedName

class Data {

    constructor()

    constructor(id: Int?, email: String?, firstName: String?, lastName: String?, avatar: String?) {
        this.id = id
        this.email = email
        this.firstName = firstName
        this.lastName = lastName
        this.avatar = avatar
    }

    @SerializedName("id")
    var id: Int? = null

    @SerializedName("email")
    var email: String? = null

    @SerializedName("first_name")
    var firstName: String? = null

    @SerializedName("last_name")
    var lastName: String? = null

    @SerializedName("avatar")
    var avatar: String? = null
}