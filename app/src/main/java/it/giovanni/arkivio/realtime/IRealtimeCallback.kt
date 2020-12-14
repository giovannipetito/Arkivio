package it.giovanni.arkivio.realtime

import it.giovanni.arkivio.bean.user.Response

interface IRealtimeCallback {

    fun onRealtimeCallbackSuccess(message: String?, response: Response?)
    fun onRealtimeCallbackFailure(message: String?)
}