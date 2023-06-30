package it.giovanni.arkivio.restclient.realtime

import it.giovanni.arkivio.model.user.UserResponse

interface IRealtime {

    fun onRealtimeSuccess(message: String?, response: UserResponse?)

    fun onRealtimeFailure(message: String?)
}