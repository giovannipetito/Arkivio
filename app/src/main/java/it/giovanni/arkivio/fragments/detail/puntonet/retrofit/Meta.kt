package it.giovanni.arkivio.fragments.detail.puntonet.retrofit

import com.google.gson.annotations.SerializedName

class Meta {

    @SerializedName("total_pages")
    var total_pages: Int? = null

    @SerializedName("current_page")
    var current_page: Int? = null

    @SerializedName("next_page")
    var next_page: Int? = null

    @SerializedName("per_page")
    var per_page: Int? = null

    @SerializedName("total_count")
    var total_count: Int? = null
}