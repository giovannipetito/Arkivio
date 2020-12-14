package it.giovanni.arkivio.bean.user

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Response: Serializable {

    @SerializedName("users")
    @Expose
    var users : ArrayList<User>? = null
}