package it.giovanni.arkivio.fragments.detail.puntonet.retrofitpaging

import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("id") var id: Int,
    @SerializedName("email") var email: String,
    @SerializedName("first_name") var firstName: String,
    @SerializedName("last_name") var lastName: String,
    @SerializedName("avatar") var avatar: String
)