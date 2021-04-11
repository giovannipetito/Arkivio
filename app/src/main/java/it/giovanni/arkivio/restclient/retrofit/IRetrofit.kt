package it.giovanni.arkivio.restclient.retrofit

interface IRetrofit {

    // fun onRetrofitSuccess(message: String?, response: Response?)
    fun onRetrofitSuccess(message: String?, list: List<User?>?)
    fun onRetrofitFailure(message: String?)
}