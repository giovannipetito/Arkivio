package it.giovanni.arkivio.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class LinkSide(

    @SerializedName("analyticsLabel")
    @Expose
    var analyticsLabel: String,

    @SerializedName("name")
    @Expose
    var name: String,

    @SerializedName("link")
    @Expose
    var link: String,

    @SerializedName("position")
    @Expose
    var position: String,

    @SerializedName("type")
    @Expose
    var type: String,

    @SerializedName("appLinkAndroid")
    @Expose
    var appLinkAndroid: String

) : Serializable