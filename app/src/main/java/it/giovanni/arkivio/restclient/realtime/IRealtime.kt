package it.giovanni.arkivio.restclient.realtime

import it.giovanni.arkivio.bean.user.Response

interface IRealtime {

    fun onRealtimeSuccess(message: String?, response: Response?)
    fun onRealtimeFailure(message: String?)
}