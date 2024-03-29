package it.giovanni.arkivio.model.user

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class UserResponse: Serializable {

    @SerializedName("users")
    @Expose
    var users : ArrayList<User>? = null
}