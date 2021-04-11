package it.giovanni.arkivio.restclient.volley

interface IVolley {

    fun onVolleyGetSuccess(message: String?)
    fun onVolleyPostSuccess(message: String?)
    fun onVolleyFailure(message: String?)
}