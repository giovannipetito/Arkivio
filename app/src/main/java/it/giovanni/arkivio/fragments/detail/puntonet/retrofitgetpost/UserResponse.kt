package it.giovanni.arkivio.fragments.detail.puntonet.retrofitgetpost

import com.google.gson.annotations.SerializedName

class UserResponse {

    @SerializedName("name")
    var name: String? = null

    @SerializedName("job")
    var job: String? = null

    @SerializedName("id")
    var id = 0

    @SerializedName("createdAt")
    var createdAt: String? = null
}