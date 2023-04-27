package it.giovanni.arkivio.fragments.detail.puntonet.mvvvm.utenti.api

import it.giovanni.arkivio.restclient.retrofit.User

interface IUsers {

    fun onSuccess(message: String?, users: List<User?>?)
    fun onFailure(message: String?)
}