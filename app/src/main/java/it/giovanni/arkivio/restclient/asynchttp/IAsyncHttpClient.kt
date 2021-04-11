package it.giovanni.arkivio.restclient.asynchttp

interface IAsyncHttpClient {

    fun onAsyncHttpSuccess(message: String?, response: Response?)
    fun onAsyncHttpFailure(message: String?)
}