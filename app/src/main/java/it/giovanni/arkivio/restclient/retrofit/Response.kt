package it.giovanni.arkivio.restclient.retrofit

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Response: Serializable {

    @SerializedName("users")
    @Expose
    var users : List<User?>? = null
}