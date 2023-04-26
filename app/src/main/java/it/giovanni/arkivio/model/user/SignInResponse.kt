package it.giovanni.arkivio.model.user

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class SignInResponse: Serializable {

    @SerializedName("kind")
    @Expose
    var kind : String? = null

    @SerializedName("localId")
    @Expose
    var localId : String? = null

    @SerializedName("email")
    @Expose
    var email : String? = null

    @SerializedName("displayName")
    @Expose
    var displayName : String? = null

    @SerializedName("idToken")
    @Expose
    var idToken : String? = null

    @SerializedName("registered")
    @Expose
    var registered : Boolean? = null

    @SerializedName("refreshToken")
    @Expose
    var refreshToken : String? = null

    @SerializedName("expiresIn")
    @Expose
    var expiresIn : String? = null
}